package com.joe.benefits.employee.service;

import com.joe.benefits.employee.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PayrollService {
    Page<PayrollPeriod> findAll(final Pageable pageable);

    Optional<PayrollPeriod> findById(final Integer id);

    void processPayroll(Integer payrollId);

    List<EmployeePaycheck> generateEmployeePaychecks(Integer payrollId);

    EmployeePaycheck generateEmployeePayroll(Integer employeeId);

    PayrollPeriod save(PayrollPeriod newPayrollPeriod);

    EmployeePaycheck getEmployeePayPreview(Integer benefitId, Integer employeeId);

    List<EmployeePaycheck> generateEmployeePayrollByBenefitId(Integer benefitId);

    List<EmployeePaycheck> getEmployeePaychecksByPayrollId(Integer payrollId);
}
