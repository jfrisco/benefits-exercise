package com.joe.benefits.employee.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.type.YesNoConverter;

@Data
@Entity
@Table(name = "BENEFIT_DISCOUNT")
public class BenefitDiscount {
    @Id
    private Integer id;
    @Enumerated(EnumType.STRING)
    @Column(name = "DISCOUNT_TYPE")
    private DiscountType discountType;

    @Column(name = "target")
    private String target;

    @Column(name = "DISCOUNT_AMOUNT")
    private Double discountAmount;

    @Column(name = "ACTIVE")
    @Convert(converter= YesNoConverter.class)
    private Boolean isActive;
}
