package com.joe.benefits.employee.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@Table(name = "PAYROLL_PERIOD")
public class PayrollPeriod {
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer Id;
    @Column(name = "START_DATE")
    public Instant startDate;
    @Column(name = "END_DATE")
    public Instant endDate;
}
