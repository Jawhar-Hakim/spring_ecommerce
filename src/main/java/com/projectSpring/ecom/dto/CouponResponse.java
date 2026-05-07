package com.projectSpring.ecom.dto;

import com.projectSpring.ecom.entity.Coupon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponResponse {
    private Long id;
    private String code;
    private Coupon.DiscountType type;
    private Double value;
    private LocalDate expirationDate;
    private Integer maxUsages;
    private Integer currentUsages;
    private Boolean active;
}
