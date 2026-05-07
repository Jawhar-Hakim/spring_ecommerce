package com.projectSpring.ecom.mapper;

import com.projectSpring.ecom.dto.ProductRequest;
import com.projectSpring.ecom.dto.ProductResponse;
import com.projectSpring.ecom.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;

    public ProductResponse toResponse(Product product) {
        if (product == null) return null;
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .promoPrice(product.getPromoPrice())
                .stock(product.getStock())
                .active(product.getActive())
                .createdAt(product.getCreatedAt())
                .seller(userMapper.toResponse(product.getSeller()))
                .categories(product.getCategories().stream()
                        .map(categoryMapper::toResponse)
                        .collect(Collectors.toSet()))
                .images(product.getImages())
                .build();
    }

    public Product toEntity(ProductRequest request) {
        if (request == null) return null;
        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .promoPrice(request.getPromoPrice())
                .stock(request.getStock())
                .images(request.getImages())
                .build();
    }
}
