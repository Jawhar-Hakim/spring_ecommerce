package com.projectSpring.ecom.controller;

import com.projectSpring.ecom.entity.Cart;
import com.projectSpring.ecom.entity.User;
import com.projectSpring.ecom.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<Cart> getCart(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(cartService.getCartForUser(user.getId()));
    }

    @PostMapping("/items")
    public ResponseEntity<Cart> addItem(@AuthenticationPrincipal User user, @RequestBody AddItemRequest request) {
        return ResponseEntity.ok(cartService.addItemToCart(user.getId(), request.productId(), request.quantity()));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Cart> removeItem(@AuthenticationPrincipal User user, @PathVariable Long cartItemId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(user.getId(), cartItemId));
    }

    @PostMapping("/coupon")
    public ResponseEntity<Cart> applyCoupon(@AuthenticationPrincipal User user, @RequestParam String couponCode) {
        return ResponseEntity.ok(cartService.applyCoupon(user.getId(), couponCode));
    }

    @DeleteMapping("/coupon")
    public ResponseEntity<Cart> removeCoupon(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(cartService.removeCoupon(user.getId()));
    }

    public record AddItemRequest(Long productId, int quantity) {}
}
