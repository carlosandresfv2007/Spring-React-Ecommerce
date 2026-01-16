package com.ecommerce.project.service;

import com.ecommerce.project.dto.category.*;
import com.ecommerce.project.exceptions.DuplicateResourceException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements  CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PagedCategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort.Direction direction = Sort.Direction.fromString(sortOrder);
        Sort sortByAndOrder = Sort.by(direction, sortBy);

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);

        List<CategoryResponse> categoryDtos = categoryPage.getContent()
                .stream()
                .map(category -> modelMapper.map(category, CategoryResponse.class))
                .toList();
        return new PagedCategoryResponse(
                categoryDtos,
                pageDetails.getPageNumber(),
                pageDetails.getPageSize(),
                categoryPage.getTotalElements(),
                categoryPage.getTotalPages(),
                categoryPage.isLast()
        );
    }



    @Override
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        categoryRepository.findByName(request.getName())
                .ifPresent(c -> {
                    throw new DuplicateResourceException("Category", "name", request.getName());
                });
        Category category = new Category();
        category.setName(request.getName());
        // 3. Save
        Category saved = categoryRepository.save(category);
        // 4. Map entity → response
        CategoryResponse response = new CategoryResponse();
        response.setId(saved.getId());
        response.setName(saved.getName());
        return response;
    }

    @Override
    public void deleteCategory(long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        categoryRepository.delete(category);
    }


    @Override
    public CategoryResponse updateCategory(UpdateCategoryRequest request, Long id) {
        // 1. Find existing category
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        String newName = request.getName();
        // 2. Validate duplicate name only if the name has changed
        if (!existing.getName().equalsIgnoreCase(newName)) {
            categoryRepository.findByName(newName)
                    .ifPresent(c -> {
                        throw new DuplicateResourceException("Category", "name", newName);
                    });
        }
        // 3. Update value
        existing.setName(newName);
        // 4. Save and map to DTO
        Category saved = categoryRepository.save(existing);
        return modelMapper.map(saved, CategoryResponse.class);
    }

}
