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

    List<EmployeePaycheck> getEmployeePaychecksByPayrollId(Integer payrollId);

    PayrollPeriod save(PayrollPeriod newPayrollPeriod);

    EmployeePaycheck getEmployeePayPreview(Integer benefitId, Integer employeeId);
}
