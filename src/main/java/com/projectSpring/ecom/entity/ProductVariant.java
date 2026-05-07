package com.projectSpring.ecom.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_variants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @ToString.Exclude
    private Product product;

    @Column(nullable = false)
    private String attribute;

    @Column(nullable = false)
    private String value;

    @Column(name = "additional_stock", nullable = false)
    @Builder.Default
    private Integer additionalStock = 0;

    @Column(name = "price_delta")
    @Builder.Default
    private Double priceDelta = 0.0;
}
