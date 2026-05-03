package com.ecommerce.product.service;

import com.ecommerce.product.client.CategoryClient;
import com.ecommerce.product.dto.request.ProductRequest;
import com.ecommerce.product.dto.response.ProductResponse;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryClient categoryClient;

    public ProductResponse createProduct(ProductRequest request) {
        // CategoryId geçerli mi kontrol et
        CategoryClient.CategoryResponse category = categoryClient.getCategoryById(request.getCategoryId());

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .categoryId(request.getCategoryId())
                .stock(request.getStock())
                .attributes(request.getAttributes())
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Product saved = productRepository.save(product);
        log.info("Product created: {}", saved.getId());
        return toResponse(saved, category);
    }

    @Cacheable(value = "products", key = "#id")
    public ProductResponse getProductById(String id) {
        log.info("Fetching product from DB: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));

        CategoryClient.CategoryResponse category = null;
        try {
            category = categoryClient.getCategoryById(product.getCategoryId());
        } catch (Exception e) {
            log.warn("Could not fetch category for product: {}", id);
        }

        return toResponse(product, category);
    }

    @Cacheable(value = "product-list")
    public List<ProductResponse> getAllProducts() {
        log.info("Fetching all products from DB");
        return productRepository.findByActiveTrue()
                .stream()
                .map(product -> {
                    CategoryClient.CategoryResponse category = null;
                    try {
                        category = categoryClient.getCategoryById(product.getCategoryId());
                    } catch (Exception e) {
                        log.warn("Could not fetch category for product: {}", product.getId());
                    }
                    return toResponse(product, category);
                })
                .collect(Collectors.toList());
    }

    @Cacheable(value = "product-category", key = "#slug")
    public List<ProductResponse> getProductsByCategorySlug(String slug) {
        log.info("Fetching products by category slug: {}", slug);
        CategoryClient.CategoryResponse category = categoryClient.getCategoryBySlug(slug);
        return productRepository.findByCategoryIdAndActiveTrue(category.getId())
                .stream()
                .map(product -> toResponse(product, category))
                .collect(Collectors.toList());
    }

    @CacheEvict(value = {"products", "product-list", "product-category"}, allEntries = true)
    public ProductResponse updateProduct(String id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));

        CategoryClient.CategoryResponse category = categoryClient.getCategoryById(request.getCategoryId());

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategoryId(request.getCategoryId());
        product.setStock(request.getStock());
        product.setAttributes(request.getAttributes());
        product.setUpdatedAt(LocalDateTime.now());

        Product saved = productRepository.save(product);
        log.info("Product updated: {}", saved.getId());
        return toResponse(saved, category);
    }

    @CacheEvict(value = {"products", "product-list", "product-category"}, allEntries = true)
    public void deleteProduct(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
        product.setActive(false);
        productRepository.save(product);
        log.info("Product deleted (soft): {}", id);
    }

    private ProductResponse toResponse(Product product, CategoryClient.CategoryResponse category) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .categoryId(product.getCategoryId())
                .categoryName(category != null ? category.getName() : null)
                .categorySlug(category != null ? category.getSlug() : null)
                .stock(product.getStock())
                .attributes(product.getAttributes())
                .active(product.isActive())
                .createdAt(product.getCreatedAt())
                .build();
    }
}