package com.joe.benefits.employee.model;

public class EmployeePaycheckId {
    private Integer employeeId;
    private Integer payPeriodId;

    public EmployeePaycheckId(Integer employeeId, Integer payPeriodId) {
        this.employeeId = employeeId;
        this.payPeriodId = payPeriodId;
    }
}
