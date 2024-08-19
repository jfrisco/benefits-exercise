package com.joe.benefits.employee.repository;

import com.joe.benefits.employee.model.BenefitPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BenefitPackageRepository extends JpaRepository<BenefitPackage, Integer> {
}
