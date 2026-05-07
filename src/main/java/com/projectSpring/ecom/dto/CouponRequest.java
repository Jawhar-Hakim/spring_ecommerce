package com.projectSpring.ecom.dto;

import com.projectSpring.ecom.entity.Coupon;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponRequest {
    
    @NotBlank(message = "Coupon code is required")
    private String code;

    @NotNull(message = "Discount type is required")
    private Coupon.DiscountType type;

    @NotNull(message = "Value is required")
    @Min(value = 0, message = "Value cannot be negative")
    private Double value;

    private LocalDate expirationDate;

    private Integer maxUsages;

    private Boolean active;
}
