package com.joe.benefits.employee.service;

import com.joe.benefits.employee.exception.NotFoundException;
import com.joe.benefits.employee.model.*;
import com.joe.benefits.employee.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PayrollServiceImpl implements PayrollService {
    // Using constants for these, but another step in the design would be to extract these values into
    // separate tables and classes (see BenefitPackage) to allow for configuration of different types of payroll and benefit setup
    private static final int YEARLY_COST_EMPLOYEE_BENEFIT = 1000;
    private static final int YEARLY_COST_DEPENDENT_BENEFIT = 500;
    private static final int YEARLY_PAY_PERIODS = 26;
    private static final int EMPLOYEE_BENEFIT_COST_PER_PAY_PERIOD = YEARLY_COST_EMPLOYEE_BENEFIT / YEARLY_PAY_PERIODS;
    private static final int DEPENDENT_BENEFIT_COST_PER_PAY_PERIOD = YEARLY_COST_DEPENDENT_BENEFIT / YEARLY_PAY_PERIODS;

    private final PayrollPeriodRepository payrollPeriodRepository;
    private final EmployeeRepository employeeRepository;
    private final BenefitDiscountRepository benefitDiscountRepository;
    private final EmployeePaycheckRepository employeePaycheckRepository;
    private final BenefitPackageRepository benefitPackageRepository;

    public PayrollServiceImpl(PayrollPeriodRepository payrollPeriodRepository, EmployeeRepository employeeRepository, BenefitDiscountRepository benefitDiscountRepository, EmployeePaycheckRepository employeePaycheckRepository, BenefitPackageRepository benefitPackageRepository) {
        this.payrollPeriodRepository = payrollPeriodRepository;
        this.employeeRepository = employeeRepository;
        this.benefitDiscountRepository = benefitDiscountRepository;
        this.employeePaycheckRepository = employeePaycheckRepository;
        this.benefitPackageRepository = benefitPackageRepository;
    }

    public Page<PayrollPeriod> findAll(final Pageable pageable) {
        return payrollPeriodRepository.findAll(pageable);
    }

    public Optional<PayrollPeriod> findById(final Integer id) {
        return payrollPeriodRepository.findById(id);
    }

    public void processPayroll(Integer payrollId) {
        employeePaycheckRepository.processPaychecksForPayPeriod(PayrollStatus.PROCESSED, payrollId);
    }

    @Transactional
    public List<EmployeePaycheck> generateEmployeePaychecks(Integer payrollId) {
        List<EmployeePaycheck> employeePaychecks = new ArrayList<>();
        List<Employee> employees = employeeRepository.findAll();
        List<BenefitDiscount> benefitDiscountRepositoryByIsActive = benefitDiscountRepository.findByIsActive(true);

        employees.stream().forEach(employee -> {
            EmployeePaycheck employeePaycheck = generateEmployeePaycheck(employee, employee.getBenefitPackage(), benefitDiscountRepositoryByIsActive, payrollId);
            employeePaychecks.add(employeePaycheck);
        });

        return employeePaycheckRepository.saveAll(employeePaychecks);
    }

    @Override
    public EmployeePaycheck generateEmployeePayroll(Integer employeeId) {
        // Get Current payroll
        Integer payroll = payrollPeriodRepository.findCurrentPayroll();
        List<EmployeePaycheck> employeePaychecks = new ArrayList<>();

        // Get Employee And Benefit Information
        Optional<Employee> employeesWithBenefit = employeeRepository.findById(employeeId);
        if (employeesWithBenefit.isEmpty()) {
            throw new NotFoundException("Employee not found");
        }

        // Save Employee Paycheck
        List<BenefitDiscount> benefitDiscountRepositoryByIsActive = benefitDiscountRepository.findByIsActive(true);
        EmployeePaycheck employeePaycheck = generateEmployeePaycheck(employeesWithBenefit.get(), employeesWithBenefit.get().getBenefitPackage(), benefitDiscountRepositoryByIsActive, payroll);
        return employeePaycheckRepository.save(employeePaycheck);
    }

    @Transactional
    public EmployeePaycheck getEmployeePayPreview(Integer benefitId, Integer employeeId){
        Optional<Employee> employees = employeeRepository.findById(employeeId);
        if (employees.isEmpty()) {
            throw new NotFoundException("Unable to find employee with id: " + employeeId);
        }
        Optional<BenefitPackage> benefitPackage = benefitPackageRepository.findById(benefitId);
        if (benefitPackage.isEmpty()) {
            throw new NotFoundException("Unable to find benefit with id: " + benefitId);
        }
        List<BenefitDiscount> benefitDiscountRepositoryByIsActive = benefitDiscountRepository.findByIsActive(true);

        return generateEmployeePaycheck(employees.get(), benefitPackage.get(), benefitDiscountRepositoryByIsActive, benefitId);
    }

    @Override
    public List<EmployeePaycheck> generateEmployeePayrollByBenefitId(Integer benefitId) {
        // Get Current payroll
        Integer payroll = payrollPeriodRepository.findCurrentPayroll();
        List<EmployeePaycheck> employeePaychecks = new ArrayList<>();

        // Get Employee And Benefit Information
        Optional<BenefitPackage> benefitPackage = benefitPackageRepository.findById(benefitId);
        if (benefitPackage.isEmpty()) {
            throw new NotFoundException("Unable to find benefit with id: " + benefitId);
        }

        List<Employee> employeesWithBenefit = employeeRepository.findByBenefitId(benefitId);

        // Save Employee Paycheck
        List<BenefitDiscount> benefitDiscountRepositoryByIsActive = benefitDiscountRepository.findByIsActive(true);
        employeesWithBenefit.stream().forEach(employee -> {
            EmployeePaycheck employeePaycheck = generateEmployeePaycheck(employee, employee.getBenefitPackage(), benefitDiscountRepositoryByIsActive, payroll);
            employeePaychecks.add(employeePaycheck);
        });

        return employeePaycheckRepository.saveAll(employeePaychecks);
    }

    @Override
    public List<EmployeePaycheck> getEmployeePaychecksByPayrollId(Integer payrollId) {
        return employeePaycheckRepository.findByPayPeriodId(payrollId);
    }

    @Override
    @Transactional
    public PayrollPeriod save(PayrollPeriod newPayrollPeriod) {
        return payrollPeriodRepository.save(newPayrollPeriod);
    }

    private EmployeePaycheck generateEmployeePaycheck(Employee employee, BenefitPackage benefitPackage, List<BenefitDiscount> benefitDiscounts, Integer payrollId) {
        // Assuming the first match on a discount will be ok - a real system would need to know how to determine the appropriate discount to apply when an
        // employee or dependent meet multiple discount criteria as well as determine payroll type by payrollId rather than using hard coded values
        return EmployeePaycheck.builder()
                .employeeId(employee.getId())
                .salary(employee.getSalary())
                .benefitPackage(benefitPackage)
                .total(calculateEmployeeTotal(employee, benefitDiscounts))
                .payPeriodId(payrollId)
                .status(PayrollStatus.PROCESSING)
                .build();
    }

    private BigDecimal calculateEmployeeTotal(Employee employee, List<BenefitDiscount> benefitDiscounts) {
        // Assuming the first match on a discount will be ok - a real system would need to know how to determine the appropriate discount to apply when an
        // employee or dependent meet multiple discount criteria as well as determine payroll type by payrollId rather than using hard coded values
        BigDecimal employeeDeductions = calculateEmployeeDeduction(employee, benefitDiscounts);
        BigDecimal dependentDeductions = calculateDependentDeductions(employee.getDependents(), benefitDiscounts);
        return employee.getSalary().subtract(employeeDeductions).subtract(dependentDeductions);
    }

    private BigDecimal calculateDependentDeductions(List<Dependent> dependents, List<BenefitDiscount> benefitDiscounts) {
        BigDecimal dependentDeductions = BigDecimal.ZERO;
        if (dependents.isEmpty()) {
            return dependentDeductions;
        }
        for (Dependent dependent : dependents) {
            BigDecimal deduction = BigDecimal.valueOf(DEPENDENT_BENEFIT_COST_PER_PAY_PERIOD);
            for (BenefitDiscount benefitDiscount : benefitDiscounts) {
                if (meetsDiscountCriteria(dependent, benefitDiscount)) {
                    deduction = deduction.multiply(BigDecimal.valueOf((double) (100 - benefitDiscount.getDiscountPercentage()) / 100));
                    break;
                }
            }
            dependentDeductions = dependentDeductions.add(deduction);
        }
        return dependentDeductions;
    }

    private BigDecimal calculateEmployeeDeduction(Employee employee, List<BenefitDiscount> benefitDiscounts) {
        BigDecimal employeeDeductions = BigDecimal.valueOf((EMPLOYEE_BENEFIT_COST_PER_PAY_PERIOD));
        for (BenefitDiscount benefitDiscount : benefitDiscounts) {
            if (meetsDiscountCriteria(employee, benefitDiscount)) {
                employeeDeductions = employeeDeductions.multiply(BigDecimal.valueOf((double) (100 - benefitDiscount.getDiscountPercentage()) / 100));
                break;
            }
        }
        return employeeDeductions;
    }

    private boolean meetsDiscountCriteria(Employee employee, BenefitDiscount benefitDiscount) {
        return switch (benefitDiscount.getDiscountType()) {
            case LAST_NAME_STARTS_WITH -> employee.getLastName().startsWith(benefitDiscount.getTarget());
            case FIRST_NAME_STARTS_WITH -> employee.getFirstName().startsWith(benefitDiscount.getTarget());
        };
    }

    private boolean meetsDiscountCriteria(Dependent dependent, BenefitDiscount benefitDiscount) {
        return switch (benefitDiscount.getDiscountType()) {
            case LAST_NAME_STARTS_WITH -> dependent.getLastName().startsWith(benefitDiscount.getTarget());
            case FIRST_NAME_STARTS_WITH -> dependent.getFirstName().startsWith(benefitDiscount.getTarget());
        };
    }


}
