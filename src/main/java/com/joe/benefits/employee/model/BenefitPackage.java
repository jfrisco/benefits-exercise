package com.joe.benefits.employee.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "BENEFIT_PACKAGE")
public class BenefitPackage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer id;
    @Column(name = "name")
    public String name;
    @Column(name = "EMPLOYEE_DEDUCTION")
    public Double employeeDeduction;
    @Column(name = "DEPENDENT_DEDUCTION")
    public Double dependentDeduction;
}
