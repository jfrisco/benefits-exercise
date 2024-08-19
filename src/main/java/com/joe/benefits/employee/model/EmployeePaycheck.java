package com.joe.benefits.employee.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Table(name = "EMPLOYEE_PAYCHECK")
@Entity
@Builder
@IdClass(EmployeePaycheckId.class)
@AllArgsConstructor
@NoArgsConstructor
public class EmployeePaycheck {
    @Id
    @Column(name = "EMPLOYEE_ID")
    private Integer employeeId;
    @Id
    @Column(name = "PAYROLL_ID")
    private Integer payPeriodId;
    @ManyToOne
    @JoinColumn(name = "BENEFIT_ID")
    private BenefitPackage benefitPackage;
    @Column(name = "TOTAL")
    private BigDecimal total;
    @Column(name = "SALARY")
    private BigDecimal salary;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PayrollStatus status;
}
