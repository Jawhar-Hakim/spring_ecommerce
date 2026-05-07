package com.projectSpring.ecom.service;

import com.projectSpring.ecom.dto.ReviewRequest;
import com.projectSpring.ecom.dto.ReviewResponse;
import com.projectSpring.ecom.entity.Product;
import com.projectSpring.ecom.entity.Review;
import com.projectSpring.ecom.entity.User;
import com.projectSpring.ecom.repository.OrderRepository;
import com.projectSpring.ecom.repository.ProductRepository;
import com.projectSpring.ecom.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public ReviewResponse createReview(ReviewRequest request, User customer) {
        if (!orderRepository.hasPurchasedProduct(customer.getId(), request.getProductId())) {
            throw new RuntimeException("You can only review products you have purchased");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Review review = Review.builder()
                .customer(customer)
                .product(product)
                .rating(request.getRating())
                .comment(request.getComment())
                .approved(false) // Needs Admin approval as per PDF
                .build();

        return mapToResponse(reviewRepository.save(review));
    }

    public List<ReviewResponse> getProductReviews(Long productId) {
        return reviewRepository.findByProductIdAndApprovedTrue(productId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ReviewResponse approveReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        review.setApproved(true);
        return mapToResponse(reviewRepository.save(review));
    }

    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .productName(review.getProduct().getName())
                .customerName(review.getCustomer().getFirstName() + " " + review.getCustomer().getLastName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .approved(review.getApproved())
                .build();
    }
}
