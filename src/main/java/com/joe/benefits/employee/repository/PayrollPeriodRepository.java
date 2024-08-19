package com.joe.benefits.employee.repository;

import com.joe.benefits.employee.model.PayrollPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PayrollPeriodRepository extends JpaRepository<PayrollPeriod, Integer> {
    @Query(value = "SELECT MAX(id) FROM PayrollPeriod", nativeQuery = true)
    Integer findCurrentPayroll();
}
