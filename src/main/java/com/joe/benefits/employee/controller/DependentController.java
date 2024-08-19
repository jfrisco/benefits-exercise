package com.joe.benefits.employee.controller;

import com.joe.benefits.employee.exception.NotFoundException;
import com.joe.benefits.employee.model.Dependent;
import com.joe.benefits.employee.repository.DependentRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/dependents")
public class DependentController {

    private final DependentRepository dependentRepository;

    public DependentController(DependentRepository dependentRepository) {
        this.dependentRepository = dependentRepository;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve dependent by id")
    public Dependent getDependentById(@PathVariable int id) {
        return dependentRepository.findById(id).orElseThrow(() -> new NotFoundException("Unable to find dependent with id: " + id));
    }

    @GetMapping()
    @Operation(summary = "Return paginated view of all dependents")
    public Page<Dependent> findAllDependents(@RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
                                           @RequestParam(value = "pageSize", required = false, defaultValue = "50") Integer pageSize,
                                           @RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy){
        return dependentRepository.findAll(PageRequest.of(offset, pageSize, Sort.by(sortBy)));
    }

    @PostMapping
    @Operation(summary = "Create a new dependent")
    public Dependent newDependent(@RequestBody Dependent dependent) {
        return dependentRepository.save(dependent);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing dependent")
    public Dependent updateDependent(@RequestBody Dependent dependent, @PathVariable int id) {
        Optional<Dependent> dependentById = dependentRepository.findById(id);
        if (dependentById.isPresent()) {
            Dependent dependentToUpdate = dependentById.get();
            dependentToUpdate.setFirstName(dependent.getFirstName());
            dependentToUpdate.setLastName(dependent.getLastName());
            return dependentRepository.save(dependent);
        }
        else{
            // throw exception when unable to find dependent to update
            throw new NotFoundException("Unable to find dependent to update with id: " + id);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a dependent")
    public void deleteDependent(@PathVariable int id) {
        dependentRepository.deleteById(id);
    }
}
