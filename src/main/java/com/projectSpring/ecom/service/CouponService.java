package com.projectSpring.ecom.service;

import com.projectSpring.ecom.dto.CouponRequest;
import com.projectSpring.ecom.dto.CouponResponse;
import com.projectSpring.ecom.entity.Coupon;
import com.projectSpring.ecom.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    public List<CouponResponse> getAllCoupons() {
        return couponRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CouponResponse createCoupon(CouponRequest request) {
        Coupon coupon = Coupon.builder()
                .code(request.getCode().toUpperCase())
                .type(request.getType())
                .value(request.getValue())
                .expirationDate(request.getExpirationDate())
                .maxUsages(request.getMaxUsages())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();
        return mapToResponse(couponRepository.save(coupon));
    }

    public CouponResponse updateCoupon(Long id, CouponRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));
        
        coupon.setCode(request.getCode().toUpperCase());
        coupon.setType(request.getType());
        coupon.setValue(request.getValue());
        coupon.setExpirationDate(request.getExpirationDate());
        coupon.setMaxUsages(request.getMaxUsages());
        if (request.getActive() != null) {
            coupon.setActive(request.getActive());
        }
        
        return mapToResponse(couponRepository.save(coupon));
    }

    public void deleteCoupon(Long id) {
        couponRepository.deleteById(id);
    }

    public Coupon validateCoupon(String code) {
        Coupon coupon = couponRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Invalid coupon code"));

        if (!coupon.getActive()) {
            throw new RuntimeException("Coupon is inactive");
        }

        if (coupon.getExpirationDate() != null && coupon.getExpirationDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Coupon has expired");
        }

        if (coupon.getMaxUsages() != null && coupon.getCurrentUsages() >= coupon.getMaxUsages()) {
            throw new RuntimeException("Coupon usage limit reached");
        }

        return coupon;
    }

    private CouponResponse mapToResponse(Coupon coupon) {
        if (coupon == null) return null;
        return CouponResponse.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .type(coupon.getType())
                .value(coupon.getValue())
                .expirationDate(coupon.getExpirationDate())
                .maxUsages(coupon.getMaxUsages())
                .currentUsages(coupon.getCurrentUsages())
                .active(coupon.getActive())
                .build();
    }
}
