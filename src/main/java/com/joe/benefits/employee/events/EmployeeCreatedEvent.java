package com.joe.benefits.employee.events;

import lombok.Data;

@Data
public class EmployeeCreatedEvent {
    private Integer employeeId;

    public EmployeeCreatedEvent(Integer employeeId) {
        this.employeeId = employeeId;
    }
}
