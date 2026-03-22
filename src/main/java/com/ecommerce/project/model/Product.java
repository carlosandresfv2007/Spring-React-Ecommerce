package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    @Positive
    private Double specialPrice;

    @NotNull
    @Min(0)
    @Max(100)
    private Double discount;

    private String image;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

}
