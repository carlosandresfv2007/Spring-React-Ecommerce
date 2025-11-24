package com.ecommerce.project.service;

import com.ecommerce.project.dto.category.CreateCategoryRequest;
import com.ecommerce.project.dto.category.CategoryResponse;
import com.ecommerce.project.dto.category.PagedCategoryResponse;
import com.ecommerce.project.dto.category.CategoryRequest;
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
        if(categories.isEmpty()) throw new ResourceNotFoundException();
        List<CategoryRequest> categoriesRequest = categories.stream()
                .map(category -> modelMapper.map(category, CategoryResponse.class));
        PagedCategoryResponse pagedCategoryResponse = new PagedCategoryResponse();
        pagedCategoryResponse.setContent(categoriesRequest);
        return pagedCategoryResponse;
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
    public String deleteCategory(long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        categoryRepository.deleteById(categoryId);
        return "Category with ID " + categoryId + " deleted successfully";
    }

    @Override
    public Category updateCategory(CreateCategoryRequest request, Long categoryId) {
        Category existing = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        String newName = request.getName();
        if (!existing.getCategoryName().equals(newName)) {
            Optional<Category> other = categoryRepository.findByCategoryName(newName);
            if (other.isPresent()) throw new DuplicateResourceException("Category", "name", newName);
        }
        existing.setCategoryName(newName);
        return categoryRepository.save(existing);
    }

}
