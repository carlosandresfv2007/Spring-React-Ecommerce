package com.ecommerce.project.dto.product;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductRequest {
    @NotBlank
    @Size(min = 3, max = 100)
    private String name;

    @NotBlank
    @Size(min = 3, max = 100)
    private String description;

    @NotNull
    @Min(0)
    private Integer quantity;

    @NotNull
    @Positive
    private Double price;

    @NotNull
    @Min(0)
    @Max(100)
    private Double discount;
}
