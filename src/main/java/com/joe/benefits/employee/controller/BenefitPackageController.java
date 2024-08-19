package com.joe.benefits.employee.controller;

import com.joe.benefits.employee.exception.NotFoundException;
import com.joe.benefits.employee.model.BenefitPackage;
import com.joe.benefits.employee.model.EmployeePaycheck;
import com.joe.benefits.employee.repository.BenefitPackageRepository;
import com.joe.benefits.employee.service.PayrollService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/benefits")
public class BenefitPackageController {
    private final BenefitPackageRepository benefitPackageRepository;
    private final PayrollService payrollService;

    public BenefitPackageController(BenefitPackageRepository benefitPackageRepository, PayrollService payrollService) {
        this.benefitPackageRepository = benefitPackageRepository;
        this.payrollService = payrollService;
    }

    @GetMapping("/{id}")
    public BenefitPackage getBenefitPackageById(@PathVariable int id) {
        return benefitPackageRepository.findById(id).orElseThrow(() -> new NotFoundException("Unable to find benefit package with id: " + id));
    }

    @GetMapping()
    public Page<BenefitPackage> findAllDependents(@RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
                                             @RequestParam(value = "pageSize", required = false, defaultValue = "50") Integer pageSize,
                                             @RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy){
        return benefitPackageRepository.findAll(PageRequest.of(offset, pageSize, Sort.by(sortBy)));
    }

    @PostMapping
    public BenefitPackage newBenefitPackage(@RequestBody BenefitPackage benefitPackage) {
        return benefitPackageRepository.save(benefitPackage);
    }

    @PutMapping("/{id}")
    public BenefitPackage updateBenefitPackage(@RequestBody BenefitPackage benefitPackage, @PathVariable int id) {
        Optional<BenefitPackage> benefitPackageById = benefitPackageRepository.findById(id);
        if (benefitPackageById.isPresent()) {
            BenefitPackage packageToUpdate = benefitPackageById.get();
            packageToUpdate.setName(benefitPackage.getName());
            packageToUpdate.setEmployeeDeduction(benefitPackage.getEmployeeDeduction());
            packageToUpdate.setDependentDeduction(benefitPackage.getDependentDeduction());
            return benefitPackageRepository.save(packageToUpdate);
        }
        else{
            throw new NotFoundException("Unable to find benefit package with id: " + id);
        }
    }

    @GetMapping("/{id}/{employeeId}/preview")
    public EmployeePaycheck getEmployeePaychecksPreview(@PathVariable Integer benefitId, @PathVariable Integer employeeId){
        return payrollService.getEmployeePayPreview(benefitId, employeeId);
    }

    @DeleteMapping("/{id}")
    public void deleteBenefitPackage(@PathVariable int id) {
        benefitPackageRepository.deleteById(id);
    }
}
