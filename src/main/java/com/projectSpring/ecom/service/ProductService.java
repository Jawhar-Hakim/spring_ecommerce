package com.projectSpring.ecom.service;

import com.projectSpring.ecom.dto.ProductRequest;
import com.projectSpring.ecom.dto.ProductResponse;
import com.projectSpring.ecom.entity.Category;
import com.projectSpring.ecom.entity.Product;
import com.projectSpring.ecom.entity.User;
import com.projectSpring.ecom.mapper.ProductMapper;
import com.projectSpring.ecom.repository.CategoryRepository;
import com.projectSpring.ecom.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public ProductResponse createProduct(ProductRequest request, User seller) {
        Product product = productMapper.toEntity(request);
        product.setSeller(seller);
        
        if (request.getCategoryIds() != null) {
            List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
            product.setCategories(new HashSet<>(categories));
        }

        return productMapper.toResponse(productRepository.save(product));
    }

    public List<ProductResponse> getProductsBySeller(User seller) {
        return productRepository.findBySeller(seller).stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse updateProduct(Long id, ProductRequest request, User user) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        if (!existing.getSeller().getId().equals(user.getId()) && !user.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("Unauthorized to update this product");
        }

        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setPrice(request.getPrice());
        existing.setPromoPrice(request.getPromoPrice());
        existing.setStock(request.getStock());
        existing.setImages(request.getImages());
        
        if (request.getCategoryIds() != null) {
            List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
            existing.setCategories(new HashSet<>(categories));
        }
        
        return productMapper.toResponse(productRepository.save(existing));
    }

    public void deleteProduct(Long id, User user) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        if (!existing.getSeller().getId().equals(user.getId()) && !user.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("Unauthorized to delete this product");
        }
        
        // Soft delete
        existing.setActive(false);
        productRepository.save(existing);
    }

    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return productMapper.toResponse(product);
    }

    public Page<ProductResponse> searchProducts(String keyword, Long categoryId, Double minPrice, Double maxPrice, Pageable pageable) {
        org.springframework.data.jpa.domain.Specification<Product> spec = org.springframework.data.jpa.domain.Specification.where(com.projectSpring.ecom.repository.ProductSpecification.isActive(true));
        
        if (keyword != null) spec = spec.and(com.projectSpring.ecom.repository.ProductSpecification.hasName(keyword));
        if (categoryId != null) spec = spec.and(com.projectSpring.ecom.repository.ProductSpecification.hasCategory(categoryId));
        if (minPrice != null || maxPrice != null) spec = spec.and(com.projectSpring.ecom.repository.ProductSpecification.hasPriceBetween(minPrice, maxPrice));
        
        return productRepository.findAll(spec, pageable).map(productMapper::toResponse);
    }
}
