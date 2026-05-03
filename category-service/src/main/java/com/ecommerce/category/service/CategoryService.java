package com.ecommerce.category.service;

import com.ecommerce.category.dto.request.CategoryRequest;
import com.ecommerce.category.dto.response.CategoryResponse;
import com.ecommerce.category.entity.Category;
import com.ecommerce.category.repository.CategoryRepository;
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
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsBySlug(request.getSlug())) {
            throw new RuntimeException("Slug already exists: " + request.getSlug());
        }

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .slug(request.getSlug())
                .parentId(request.getParentId())
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        category = categoryRepository.save(category);
        log.info("Category created: {}", category.getId());
        return toResponse(category);
    }

    @Cacheable(value = "categories")
    public List<CategoryResponse> getAllCategories() {
        log.info("Fetching all categories from DB");
        return categoryRepository.findByActiveTrue()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "category", key = "#id")
    public CategoryResponse getCategoryById(String id) {
        log.info("Fetching category from DB: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found: " + id));
        return toResponse(category);
    }

    @Cacheable(value = "category-slug", key = "#slug")
    public CategoryResponse getCategoryBySlug(String slug) {
        log.info("Fetching category by slug from DB: {}", slug);
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Category not found: " + slug));
        return toResponse(category);
    }

    @Cacheable(value = "root-categories")
    public List<CategoryResponse> getRootCategories() {
        log.info("Fetching root categories from DB");
        List<Category> rootCategories = categoryRepository.findByParentIdIsNull();
        return rootCategories.stream()
                .map(cat -> {
                    CategoryResponse response = toResponse(cat);
                    List<CategoryResponse> subCategories = categoryRepository
                            .findByParentId(cat.getId())
                            .stream()
                            .map(this::toResponse)
                            .collect(Collectors.toList());
                    response.setSubCategories(subCategories);
                    return response;
                })
                .collect(Collectors.toList());
    }

    @CacheEvict(value = {"categories", "category", "category-slug", "root-categories"}, allEntries = true)
    public CategoryResponse updateCategory(String id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found: " + id));

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setSlug(request.getSlug());
        category.setParentId(request.getParentId());
        category.setUpdatedAt(LocalDateTime.now());

        category = categoryRepository.save(category);
        log.info("Category updated: {}", category.getId());
        return toResponse(category);
    }

    @CacheEvict(value = {"categories", "category", "category-slug", "root-categories"}, allEntries = true)
    public void deleteCategory(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found: " + id));
        category.setActive(false);
        categoryRepository.save(category);
        log.info("Category deleted (soft): {}", id);
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .slug(category.getSlug())
                .parentId(category.getParentId())
                .active(category.isActive())
                .createdAt(category.getCreatedAt())
                .build();
    }
}