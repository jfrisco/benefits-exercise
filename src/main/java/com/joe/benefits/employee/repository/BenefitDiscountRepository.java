package com.joe.benefits.employee.repository;

import com.joe.benefits.employee.model.BenefitDiscount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BenefitDiscountRepository extends JpaRepository<BenefitDiscount, Integer> {
    List<BenefitDiscount> findByIsActive(Boolean active);
}
