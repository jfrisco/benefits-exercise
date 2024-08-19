package com.joe.benefits.employee.repository;

import com.joe.benefits.employee.model.Dependent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DependentRepository extends JpaRepository<Dependent, Integer> {
}
