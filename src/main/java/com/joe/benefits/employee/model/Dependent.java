package com.joe.benefits.employee.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "DEPENDENT")
public class Dependent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(name = "FIRST_NAME")
    private String firstName;
    @Column(name = "LAST_NAME")
    private String lastName;
    @Column(name = "EMPLOYEE_ID")
    private Integer employeeId;
    @Column(name = "DATE_OF_BIRTH")
    @Temporal(TemporalType.DATE)
    private Instant dateOfBirth;
    @Column(name = "RELATIONSHIP")
    @Enumerated(EnumType.STRING)
    private Relationship relationship;
    @ManyToOne
    @JoinColumn(name = "EMPLOYEE_ID", insertable = false, updatable = false)
    private Employee employee;
}
