package com.joe.benefits.service;

import com.joe.benefits.employee.model.*;
import com.joe.benefits.employee.repository.BenefitDiscountRepository;
import com.joe.benefits.employee.repository.EmployeePaycheckRepository;
import com.joe.benefits.employee.repository.EmployeeRepository;
import com.joe.benefits.employee.service.PayrollServiceImpl;
import org.instancio.Instancio;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;

@ExtendWith(MockitoExtension.class)
public class PayrollServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private BenefitDiscountRepository benefitDiscountRepository;
    @Mock
    private EmployeePaycheckRepository employeePaycheckRepository;

    @InjectMocks
    private PayrollServiceImpl service;

    @Captor
    ArgumentCaptor<List<EmployeePaycheck>> employeePaycheckCaptor;

    @Test
    public void generateEmployeePaychecks_oneEmployeeWithNoDependentsNoDiscounts_returnsExpectedPaycheck(){
        // Arrange
        Employee employee = Instancio.create(Employee.class);
        employee.setDependents(new ArrayList<>());
        BigDecimal expectedSalary = BigDecimal.valueOf(2000);
        employee.setSalary(expectedSalary);
        Mockito.when(benefitDiscountRepository.findByIsActive(true)).thenReturn(new ArrayList<>());
        Mockito.when(employeeRepository.findAll()).thenReturn(List.of(employee));
        Mockito.when(employeePaycheckRepository.saveAll(anyList())).thenReturn(new ArrayList<>());
        BigDecimal expectedDeduction = BigDecimal.valueOf(1000 / 26);

        BigDecimal expectedTotal = expectedSalary.subtract(expectedDeduction);

        // Act
       service.generateEmployeePaychecksByPayrollId(1);

        // Assert
        Mockito.verify(employeePaycheckRepository).saveAll(employeePaycheckCaptor.capture());
        List<EmployeePaycheck> employeePaychecks = employeePaycheckCaptor.getValue();
        EmployeePaycheck paycheckToVerify = employeePaychecks.getFirst();
        Assert.assertEquals("Only 1 employee paycheck should be generated", 1, employeePaychecks.size());
        Assert.assertEquals(expectedSalary, paycheckToVerify.getSalary());
        Assert.assertEquals(expectedTotal, paycheckToVerify.getTotal());
        Assert.assertEquals(PayrollStatus.PROCESSING, paycheckToVerify.getStatus());
    }

    @Test
    public void generateEmployeePaychecks_oneEmployeeWithTwoDependentsNoDiscounts_returnsExpectedPaycheck(){
        // Arrange
        Employee employee = Instancio.create(Employee.class);
        employee.setDependents(Instancio.ofList(Dependent.class).size(2).create());
        BigDecimal expectedSalary = BigDecimal.valueOf(2000);
        employee.setSalary(expectedSalary);
        Mockito.when(benefitDiscountRepository.findByIsActive(true)).thenReturn(new ArrayList<>());
        Mockito.when(employeeRepository.findAll()).thenReturn(List.of(employee));
        Mockito.when(employeePaycheckRepository.saveAll(anyList())).thenReturn(new ArrayList<>());
        BigDecimal expectedDeduction = BigDecimal.valueOf(1000 / 26);
        BigDecimal expectedDependentDeductionDeduction = BigDecimal.valueOf(1000 / 26);
        BigDecimal expectedTotal = expectedSalary.subtract(expectedDeduction).subtract(expectedDependentDeductionDeduction);


        // Act
        service.generateEmployeePaychecksByPayrollId(1);

        // Assert
        Mockito.verify(employeePaycheckRepository).saveAll(employeePaycheckCaptor.capture());
        List<EmployeePaycheck> employeePaychecks = employeePaycheckCaptor.getValue();
        EmployeePaycheck paycheckToVerify = employeePaychecks.getFirst();
        Assert.assertEquals("Only 1 employee paycheck should be generated", 1, employeePaychecks.size());
        Assert.assertEquals(expectedSalary, paycheckToVerify.getSalary());
        Assert.assertEquals(expectedTotal, paycheckToVerify.getTotal());
        Assert.assertEquals(PayrollStatus.PROCESSING, paycheckToVerify.getStatus());
    }

    @Test
    public void generateEmployeePaychecks_oneEmployeeWithNoDependentsWithTenPercentDiscounts_returnsExpectedPaycheck(){
        // Arrange
        Employee employee = Instancio.create(Employee.class);
        employee.setLastName("Anderson");
        employee.setDependents(new ArrayList<>());
        BigDecimal expectedSalary = BigDecimal.valueOf(2000);
        employee.setSalary(expectedSalary);
        BenefitDiscount benefitDiscount = Instancio.of(BenefitDiscount.class).create();
        benefitDiscount.setDiscountType(DiscountType.LAST_NAME_STARTS_WITH);
        benefitDiscount.setTarget("A");
        benefitDiscount.setDiscountPercentage(10);

        Mockito.when(benefitDiscountRepository.findByIsActive(true)).thenReturn(List.of(benefitDiscount));
        Mockito.when(employeeRepository.findAll()).thenReturn(List.of(employee));
        Mockito.when(employeePaycheckRepository.saveAll(anyList())).thenReturn(new ArrayList<>());
        BigDecimal expectedDeduction = BigDecimal.valueOf(1000 / 26).multiply(BigDecimal.valueOf((double)90 / 100));
        BigDecimal expectedDependentDeductionDeduction = BigDecimal.ZERO;
        BigDecimal expectedTotal = expectedSalary.subtract(expectedDeduction).subtract(expectedDependentDeductionDeduction);


        // Act
        service.generateEmployeePaychecksByPayrollId(1);

        // Assert
        Mockito.verify(employeePaycheckRepository).saveAll(employeePaycheckCaptor.capture());
        List<EmployeePaycheck> employeePaychecks = employeePaycheckCaptor.getValue();
        EmployeePaycheck paycheckToVerify = employeePaychecks.getFirst();
        Assert.assertEquals("Only 1 employee paycheck should be generated", 1, employeePaychecks.size());
        Assert.assertEquals(expectedSalary, paycheckToVerify.getSalary());
        Assert.assertEquals(expectedTotal, paycheckToVerify.getTotal());
        Assert.assertEquals(PayrollStatus.PROCESSING, paycheckToVerify.getStatus());
    }

    @Test
    public void generateEmployeePaychecks_oneEmployeeWithTwoDependentsWithTenPercentDiscounts_returnsExpectedPaycheck(){
        // Arrange
        Employee employee = Instancio.create(Employee.class);
        employee.setLastName("Anderson");
        Dependent dependentOne = Instancio.create(Dependent.class);
        dependentOne.setLastName("Anderson");
        Dependent dependentTwo = Instancio.create(Dependent.class);
        dependentTwo.setLastName("Anderson");
        employee.setDependents(List.of(dependentOne, dependentTwo));
        BigDecimal expectedSalary = BigDecimal.valueOf(2000);
        employee.setSalary(expectedSalary);
        BenefitDiscount benefitDiscount = Instancio.of(BenefitDiscount.class).create();
        benefitDiscount.setDiscountType(DiscountType.LAST_NAME_STARTS_WITH);
        benefitDiscount.setTarget("A");
        benefitDiscount.setDiscountPercentage(10);

        Mockito.when(benefitDiscountRepository.findByIsActive(true)).thenReturn(List.of(benefitDiscount));
        Mockito.when(employeeRepository.findAll()).thenReturn(List.of(employee));
        Mockito.when(employeePaycheckRepository.saveAll(anyList())).thenReturn(new ArrayList<>());
        BigDecimal expectedDeduction = BigDecimal.valueOf(1000 / 26).multiply(BigDecimal.valueOf((double)90 / 100));
        BigDecimal expectedDependentDeductionDeduction = BigDecimal.valueOf(1000 / 26).multiply(BigDecimal.valueOf((double)90 / 100));
        BigDecimal expectedTotal = expectedSalary.subtract(expectedDeduction).subtract(expectedDependentDeductionDeduction);


        // Act
        service.generateEmployeePaychecksByPayrollId(1);

        // Assert
        Mockito.verify(employeePaycheckRepository).saveAll(employeePaycheckCaptor.capture());
        List<EmployeePaycheck> employeePaychecks = employeePaycheckCaptor.getValue();
        EmployeePaycheck paycheckToVerify = employeePaychecks.getFirst();
        Assert.assertEquals("Only 1 employee paycheck should be generated", 1, employeePaychecks.size());
        Assert.assertEquals(expectedSalary, paycheckToVerify.getSalary());
        Assert.assertEquals(expectedTotal, paycheckToVerify.getTotal());
        Assert.assertEquals(PayrollStatus.PROCESSING, paycheckToVerify.getStatus());
    }


}
