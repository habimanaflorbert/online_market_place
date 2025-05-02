package com.marketplace.service;

import com.marketplace.dto.CategoryRequest;
import com.marketplace.dto.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse updateCategory(Long id, CategoryRequest request);
    CategoryResponse getCategory(Long id);
    List<CategoryResponse> listCategories();
    void deleteCategory(Long id);
} 