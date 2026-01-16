package com.ecommerce.project.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(min = 5, message = "Category name must contain at least 5 characters")
    private String name;
}

