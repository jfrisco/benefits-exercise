package com.joe.benefits.employee.service;

import com.joe.benefits.employee.events.BenefitPackageUpdatedEvent;
import com.joe.benefits.employee.events.EmployeeCreatedEvent;
import com.joe.benefits.employee.events.EmployeeUpdatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class PayrollListener {

    private final PayrollService payrollService;

    public PayrollListener(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @EventListener
    public void handleEmployeeCreated(EmployeeCreatedEvent event) {
        payrollService.generateEmployeePayroll(event.getEmployeeId());
    }

    @EventListener
    public void handleEmployeeUpdated(EmployeeUpdatedEvent event) {
        payrollService.generateEmployeePayroll(event.getEmployeeId());
    }

    @EventListener
    public void handleBenefitPackageUpdated(BenefitPackageUpdatedEvent event) {
        payrollService.generateEmployeePayrollByBenefitId(event.getBenefitId());
    }
}
