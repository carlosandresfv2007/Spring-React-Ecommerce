package com.ecommerce.project.service;

import com.ecommerce.project.dto.product.CreateProductRequest;
import com.ecommerce.project.dto.product.ProductResponse;

public interface ProductService {


    ProductResponse addProduct(Long id, CreateProductRequest request);
}
