package com.marketplace.service.impl;

import com.marketplace.dto.CategoryRequest;
import com.marketplace.dto.CategoryResponse;
import com.marketplace.entity.Category;
import com.marketplace.repository.CategoryRepository;
import com.marketplace.service.CategoryService;
import com.marketplace.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        
        category = categoryRepository.save(category);
        return mapToResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        
        category = categoryRepository.save(category);
        return mapToResponse(category);
    }

    @Override
    public CategoryResponse getCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return mapToResponse(category);
    }

    @Override
    public List<CategoryResponse> listCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .productCount(category.getProducts() != null ? (long) category.getProducts().size() : 0L)
                .build();
    }
} 