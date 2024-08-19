package com.joe.benefits.employee.repository;

import com.joe.benefits.employee.model.PayrollPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayrollPeriodRepository extends JpaRepository<PayrollPeriod, Integer> {
}
