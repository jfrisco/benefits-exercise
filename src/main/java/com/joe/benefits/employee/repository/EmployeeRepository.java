package com.joe.benefits.employee.repository;

import com.joe.benefits.employee.model.Employee;
import com.joe.benefits.employee.model.EmployeePaycheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Integer> {
    List<Employee> findByBenefitId(Integer benefitId);
}
