package com.ecommerce.project.service;

import com.ecommerce.project.dto.category.*;
import com.ecommerce.project.exceptions.DuplicateResourceException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements  CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PagedCategoryResponse getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {throw new ResourceNotFoundException();}
        // Map Category → CategoryRequest
        List<CategoryRequest> categoryDtos = categories.stream()
                .map(category -> modelMapper.map(category, CategoryRequest.class))
                .toList();
        // Wrap in PagedCategoryResponse
        PagedCategoryResponse response = new PagedCategoryResponse();
        response.setContent(categoryDtos);
        return response;
    }


    @Override
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        categoryRepository.findByCategoryName(request.getName())
                .ifPresent(c -> {
                    throw new DuplicateResourceException("Category", "name", request.getName());
                });
        Category category = new Category();
        category.setCategoryName(request.getName());
        // 3. Save
        Category saved = categoryRepository.save(category);
        // 4. Map entity → response
        CategoryResponse response = new CategoryResponse();
        response.setId(saved.getCategoryId());
        response.setName(saved.getCategoryName());
        return response;
    }

    @Override
    public void deleteCategory(long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        categoryRepository.delete(category);
    }


    @Override
    public CategoryResponse updateCategory(UpdateCategoryRequest request, Long categoryId) {
        // 1. Find existing category
        Category existing = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        String newName = request.getName();
        // 2. Validate duplicate name only if the name has changed
        if (!existing.getCategoryName().equalsIgnoreCase(newName)) {
            categoryRepository.findByCategoryName(newName)
                    .ifPresent(c -> {
                        throw new DuplicateResourceException("Category", "name", newName);
                    });
        }
        // 3. Update value
        existing.setCategoryName(newName);
        // 4. Save and map to DTO
        Category saved = categoryRepository.save(existing);
        return modelMapper.map(saved, CategoryResponse.class);
    }

}
