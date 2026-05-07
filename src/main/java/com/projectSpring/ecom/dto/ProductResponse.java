package com.projectSpring.ecom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Double promoPrice;
    private Integer stock;
    private Boolean active;
    private Long createdAt;
    private UserResponse seller;
    private Set<CategoryResponse> categories;
    private List<String> images;
}
