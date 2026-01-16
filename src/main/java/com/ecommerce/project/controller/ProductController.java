package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.dto.product.CreateProductRequest;
import com.ecommerce.project.dto.product.PagedProductResponse;
import com.ecommerce.project.dto.product.ProductResponse;
import com.ecommerce.project.dto.product.UpdateProductRequest;
import com.ecommerce.project.service.CategoryService;
import com.ecommerce.project.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/public/products")
    public ResponseEntity<PagedProductResponse> getAllProducts
            (@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
             @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
             @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORIES_BY, required = false) String sortBy,
             @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {
        PagedProductResponse pagedProductResponse = productService.getAllProducts(pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(pagedProductResponse);

    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<PagedProductResponse> getProductsByCategory(@PathVariable Long id,
                                                                 @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                                 @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                                 @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
                                                                 @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder){
        PagedProductResponse pagedProductResponse = productService.searchByCategory(id, pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(pagedProductResponse, HttpStatus.OK);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<PagedProductResponse> getProductsByKeyword(@PathVariable String keyword,
                                                                @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                                @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                                @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
                                                                @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder){
        PagedProductResponse pagedProductResponse = productService.searchProductByKeyword(keyword, pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(pagedProductResponse);
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
