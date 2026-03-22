package com.ecommerce.project.service;

import com.ecommerce.project.dto.category.CreateCategoryRequest;
import com.ecommerce.project.dto.category.CategoryResponse;
import com.ecommerce.project.dto.category.PagedCategoryResponse;
import com.ecommerce.project.dto.category.UpdateCategoryRequest;
import com.ecommerce.project.model.Category;

public interface CategoryService {
    PagedCategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    CategoryResponse createCategory(CreateCategoryRequest request);

    void deleteCategory(long id);

    CategoryResponse updateCategory(UpdateCategoryRequest category, Long id);
}