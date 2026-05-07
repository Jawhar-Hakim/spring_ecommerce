package com.projectSpring.ecom.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "coupons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType type;

    @Column(nullable = false)
    private Double value;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(name = "max_usages")
    private Integer maxUsages;

    @Column(name = "current_usages")
    @Builder.Default
    private Integer currentUsages = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    public enum DiscountType {
        PERCENT, FIXED
    }
}
