package com.ecommerce.project.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String image;
    private String description;
    private double price;
    private double discount;
    private double specialPrice;
    private Integer quantity;

    private Long categoryId;
}
