package com.joe.benefits.employee.service;

import com.joe.benefits.employee.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PayrollService {
    public Page<PayrollPeriod> findAll(final Pageable pageable);

    public Optional<PayrollPeriod> findById(final Integer id);

    public void processPayroll(Integer payrollId);

    public List<EmployeePaycheck> generateEmployeePaychecks(Integer payrollId);
}
