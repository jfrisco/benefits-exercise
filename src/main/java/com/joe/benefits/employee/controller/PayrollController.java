package com.joe.benefits.employee.controller;

import com.joe.benefits.employee.exception.NotFoundException;
import com.joe.benefits.employee.model.EmployeePaycheck;
import com.joe.benefits.employee.model.PayrollPeriod;
import com.joe.benefits.employee.service.PayrollService;
import com.joe.benefits.employee.service.PayrollServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payroll")
public class PayrollController {
    private final PayrollService payrollService;

    public PayrollController(PayrollServiceImpl payrollService) {
        this.payrollService = payrollService;
    }

    @GetMapping("/{id}")
    public PayrollPeriod getPayrollPeriodById(@PathVariable int id) {
        return payrollService.findById(id).orElseThrow(() -> new NotFoundException("Unable to find payroll period with id: " + id));
    }

    @GetMapping()
    public Page<PayrollPeriod> findAllPayrollPeriods(@RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
                                                     @RequestParam(value = "pageSize", required = false, defaultValue = "50") Integer pageSize,
                                                     @RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy){
        return payrollService.findAll(PageRequest.of(offset, pageSize, Sort.by(sortBy)));
    }

    @PostMapping("/{id}/process")
    public void processPayroll(@PathVariable Integer payrollPeriod){
        payrollService.processPayroll(payrollPeriod);
    }

    @PostMapping("/{id}/generate")
    public List<EmployeePaycheck> generatePayroll(@PathVariable Integer payrollPeriod){
        return payrollService.generateEmployeePaychecks(payrollPeriod);
    }
}
