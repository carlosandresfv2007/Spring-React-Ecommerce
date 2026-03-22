package com.ecommerce.project.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @Positive
    private double price;

    @PositiveOrZero
    private double discount;

    @PositiveOrZero
    private Integer quantity;
}

