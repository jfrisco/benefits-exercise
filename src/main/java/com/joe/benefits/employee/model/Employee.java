package com.joe.benefits.employee.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Entity
@Table(name = "EMPLOYEE")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(name ="FIRST_NAME")
    private String firstName;
    @Column(name ="LAST_NAME")
    private String lastName;
    @Column(name ="BENEFIT_ID")
    private Integer benefitId;
    @Column(name ="DATE_OF_BIRTH")
    private Instant dateOfBirth;
    @Column(name ="SALARY")
    private BigDecimal salary;
    @ManyToOne
    @JoinColumn(name = "BENEFIT_ID", insertable = false, updatable = false)
    private BenefitPackage benefitPackage;
    @OneToMany
    private List<Dependent> dependents;
}
