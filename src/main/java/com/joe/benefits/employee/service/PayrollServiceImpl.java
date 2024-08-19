package com.joe.benefits.employee.service;

import com.joe.benefits.employee.model.*;
import com.joe.benefits.employee.repository.BenefitDiscountRepository;
import com.joe.benefits.employee.repository.EmployeePaycheckRepository;
import com.joe.benefits.employee.repository.EmployeeRepository;
import com.joe.benefits.employee.repository.PayrollPeriodRepository;
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

    public PayrollServiceImpl(PayrollPeriodRepository payrollPeriodRepository, EmployeeRepository employeeRepository, BenefitDiscountRepository benefitDiscountRepository, EmployeePaycheckRepository employeePaycheckRepository) {
        this.payrollPeriodRepository = payrollPeriodRepository;
        this.employeeRepository = employeeRepository;
        this.benefitDiscountRepository = benefitDiscountRepository;
        this.employeePaycheckRepository = employeePaycheckRepository;
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
            EmployeePaycheck employeePaycheck = generateEmployeePaycheck(employee, benefitDiscountRepositoryByIsActive, payrollId);
            employeePaychecks.add(employeePaycheck);
        });

        return employeePaycheckRepository.saveAll(employeePaychecks);
    }

    private EmployeePaycheck generateEmployeePaycheck(Employee employee, List<BenefitDiscount> benefitDiscounts, Integer payrollId) {
        // Assuming the first match will be ok - a real system would need to know how to determine the appropriate discount to apply when an
        // employee or dependent meet multiple discount criteria
        BigDecimal employeeDeductions = calculateEmployeeDeduction(employee, benefitDiscounts);
        BigDecimal dependentDeductions = calculateDependentDeductions(employee.getDependents(), benefitDiscounts);

        return EmployeePaycheck.builder().employeeId(employee.getId()).salary(employee.getSalary()).benefitPackage(employee.getBenefitPackage()).total(employee.getSalary().subtract(employeeDeductions).subtract(dependentDeductions)).payPeriodId(payrollId).status(PayrollStatus.PROCESSING).build();
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
                    deduction = deduction.multiply(BigDecimal.valueOf( (double) (100 - benefitDiscount.getDiscountPercentage()) / 100));
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
