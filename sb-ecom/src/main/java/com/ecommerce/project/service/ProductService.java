package com.ecommerce.project.service;

import com.ecommerce.project.dto.product.CreateProductRequest;
import com.ecommerce.project.dto.product.ProductResponse;
import com.ecommerce.project.dto.product.UpdateProductRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {


    ProductResponse addProduct(Long id, CreateProductRequest request);
    ProductResponse updateProduct(Long id, UpdateProductRequest request);

    ProductResponse updateProductImage(Long id, MultipartFile image) throws IOException;
}
