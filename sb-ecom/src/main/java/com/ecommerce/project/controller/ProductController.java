package com.ecommerce.project.controller;

import com.ecommerce.project.dto.product.CreateProductRequest;
import com.ecommerce.project.dto.product.ProductResponse;
import com.ecommerce.project.dto.product.UpdateProductRequest;
import com.ecommerce.project.service.CategoryService;
import com.ecommerce.project.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ProductController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;

    @PostMapping("/admin/categories/{id}/product")
    public ResponseEntity<ProductResponse> addProduct(@PathVariable Long id, @RequestBody CreateProductRequest request) {
        ProductResponse productResponse = productService.addProduct(id, request);
        return ResponseEntity.ok(productResponse);
    }

    @PutMapping("/admin/products/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody UpdateProductRequest request) {
        ProductResponse productResponse = productService.updateProduct(id, request);
        return ResponseEntity.ok(productResponse);
    }

    @PutMapping("/products/{id}/image")
    public ResponseEntity<ProductResponse> updateProductImage(@PathVariable Long id, @RequestParam("image") MultipartFile image) throws IOException {
        ProductResponse productResponse = productService.updateProductImage(id, image);
        return ResponseEntity.ok(productResponse);

    }


}
