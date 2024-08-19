package com.joe.benefits.employee.events;

import lombok.Data;

@Data
public class EmployeeUpdatedEvent {
    private Integer employeeId;

    public EmployeeUpdatedEvent(Integer employeeId) {
        this.employeeId = employeeId;
    }
}
