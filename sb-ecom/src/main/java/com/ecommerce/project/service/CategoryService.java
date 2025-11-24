package com.ecommerce.project.service;

import com.ecommerce.project.dto.category.CreateCategoryRequest;
import com.ecommerce.project.dto.category.CategoryResponse;
import com.ecommerce.project.dto.category.PagedCategoryResponse;
import com.ecommerce.project.model.Category;

public interface CategoryService {
    PagedCategoryResponse getAllCategories();

    CategoryResponse createCategory(CreateCategoryRequest request);


    String deleteCategory(long categoryId);

    Category updateCategory(CreateCategoryRequest category, Long categoryId);
}