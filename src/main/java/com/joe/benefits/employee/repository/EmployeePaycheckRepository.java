package com.joe.benefits.employee.repository;

import com.joe.benefits.employee.model.EmployeePaycheck;
import com.joe.benefits.employee.model.EmployeePaycheckId;
import com.joe.benefits.employee.model.PayrollStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeePaycheckRepository extends JpaRepository<EmployeePaycheck, EmployeePaycheckId> {

    @Modifying
    @Query("update EmployeePaycheck e set e.status = ?1 where e.payPeriodId = ?2")
    int processPaychecksForPayPeriod(PayrollStatus status, Integer payrollPeriodId);

    List<EmployeePaycheck> findByPayPeriodId(Integer payPeriodId);
}
