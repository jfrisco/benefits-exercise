package com.joe.benefits.employee.controller;

import com.joe.benefits.employee.exception.NotFoundException;
import com.joe.benefits.employee.model.EmployeePaycheck;
import com.joe.benefits.employee.model.PayrollPeriod;
import com.joe.benefits.employee.service.PayrollService;
import com.joe.benefits.employee.service.PayrollServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "Get Payroll Period by id")
    public PayrollPeriod getPayrollPeriodById(@PathVariable int id) {
        return payrollService.findById(id).orElseThrow(() -> new NotFoundException("Unable to find payroll period with id: " + id));
    }

    @GetMapping()
    @Operation(summary = "Get Paginated view of payroll period")
    public Page<PayrollPeriod> findAllPayrollPeriods(@RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
                                                     @RequestParam(value = "pageSize", required = false, defaultValue = "50") Integer pageSize,
                                                     @RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy){
        return payrollService.findAll(PageRequest.of(offset, pageSize, Sort.by(sortBy)));
    }

    @PostMapping
    @Operation(summary = "Create a new payroll period")
    public PayrollPeriod newPayrollPeriod(@RequestBody PayrollPeriod newPayrollPeriod) {
        return payrollService.save(newPayrollPeriod);
    }

    @GetMapping("/{id}/employee-paychecks")
    @Operation(summary = "Return employee payroll for a given period.")
    public List<EmployeePaycheck> getEmployeePaychecksForPayroll(@PathVariable Integer id){
        return payrollService.getEmployeePaychecksByPayrollId(id);
    }

    @PostMapping("/{id}/preview-payroll")
    @Operation(summary = "Return employee payroll for a given period. This will set payroll for given period in a pending state. (This would not be necessary in event driven route)")
    public List<EmployeePaycheck> generatePayroll(@PathVariable Integer id){
        return payrollService.generateEmployeePaychecksByPayrollId(id);
    }

    @PostMapping("/{id}/process-payroll")
    @Operation(summary = "Update payroll state to PROCESSED to verify payroll numbers are correct")
    public void processPayroll(@PathVariable Integer payrollPeriod){
        payrollService.processPayroll(payrollPeriod);
    }

}
