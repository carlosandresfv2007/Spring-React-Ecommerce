package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.DuplicateResourceException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements  CategoryService {
    //private List<Category> categories = new ArrayList<>();

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public void createCategory(Category category) {
        Category existing = categoryRepository.findByCategoryName(category.getCategoryName());
        if (existing != null) {
            throw new DuplicateResourceException("Category", "name", category.getCategoryName());
        }
        categoryRepository.save(category);
    }


    @Override
    public String deleteCategory(long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        categoryRepository.deleteById(categoryId);
        return "Category with ID " + categoryId + " deleted successfully";
    }

    @Override
    public Category updateCategory(Category category, Long categoryId) {
        Category existing = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        String newName = category.getCategoryName();
        if (!existing.getCategoryName().equals(newName)) {
            Category other = categoryRepository.findByCategoryName(newName);
            if (other != null) {
                throw new DuplicateResourceException(
                        "Category",
                        "name",
                        newName
                );
            }
        }
        existing.setCategoryName(newName);
        return categoryRepository.save(existing);
    }

}
