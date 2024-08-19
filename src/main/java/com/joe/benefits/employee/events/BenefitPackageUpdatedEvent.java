package com.joe.benefits.employee.events;

import lombok.Data;

@Data
public class BenefitPackageUpdatedEvent {
    private Integer benefitId;

    public BenefitPackageUpdatedEvent(Integer benefitId) {
        this.benefitId = benefitId;
    }
}
