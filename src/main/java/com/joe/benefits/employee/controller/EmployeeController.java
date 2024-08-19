package com.joe.benefits.employee.controller;

import com.joe.benefits.employee.exception.NotFoundException;
import com.joe.benefits.employee.model.Employee;
import com.joe.benefits.employee.repository.EmployeeRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve employee by id")
    public Employee getEmployeeById(@PathVariable int id){
        return employeeRepository.findById(id).orElseThrow(() -> new NotFoundException("Unable to find employee with id: " + id));
    }

    @GetMapping()
    @Operation(summary = "Return paginated view of employee")
    public Page<Employee> findAllEmployees(@RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
                                           @RequestParam(value = "pageSize", required = false, defaultValue = "50") Integer pageSize,
                                           @RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy){
        return employeeRepository.findAll(PageRequest.of(offset, pageSize, Sort.by(sortBy)));
    }

    @PostMapping
    @Operation(summary = "Create a new employee")
    public Employee newEmployee(@RequestBody Employee newEmployee) {
        return employeeRepository.save(newEmployee);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update existing employee")
    public Employee updateEmployee(@RequestBody Employee updatedEmployee, @PathVariable int id) {
        Optional<Employee> employeeRepositoryById = employeeRepository.findById(id);
        if (employeeRepositoryById.isPresent()) {
            Employee employeeToUpdate = employeeRepositoryById.get();
            employeeToUpdate.setFirstName(updatedEmployee.getFirstName());
            employeeToUpdate.setLastName(updatedEmployee.getFirstName());
            employeeToUpdate.setBenefitId(updatedEmployee.getBenefitId());
            employeeToUpdate.setSalary(updatedEmployee.getSalary());
            return employeeRepository.save(employeeToUpdate);
        }
        else{
            throw new NotFoundException("Unable to find employee to update with id: " + id);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an employee")
    public void deleteEmployee(@PathVariable int id) {
        employeeRepository.deleteById(id);
    }
}
