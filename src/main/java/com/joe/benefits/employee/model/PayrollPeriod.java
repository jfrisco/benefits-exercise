package com.joe.benefits.employee.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Table(name = "PAYROLL_PERIOD")
@Entity
public class PayrollPeriod {
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer Id;
    @Column(name = "START_DATE")
    public Instant startDate;
    @Column(name = "END_DATE")
    public Instant endDate;
}
