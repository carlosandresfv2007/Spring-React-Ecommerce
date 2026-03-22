package com.ecommerce.project.service;

import com.ecommerce.project.dto.category.CategoryResponse;
import com.ecommerce.project.dto.category.PagedCategoryResponse;
import com.ecommerce.project.dto.product.CreateProductRequest;
import com.ecommerce.project.dto.product.PagedProductResponse;
import com.ecommerce.project.dto.product.ProductResponse;
import com.ecommerce.project.dto.product.UpdateProductRequest;
import com.ecommerce.project.exceptions.ApiError;
import com.ecommerce.project.exceptions.DuplicateResourceException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.repositories.CategoryRepository;
import com.ecommerce.project.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private FileService fileService;

    @Override
    public ProductResponse addProduct(Long categoryId, CreateProductRequest request) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        // SQL-Based validation (enterprise style)
        boolean exists = productRepository.existsByCategory_IdAndName(categoryId, request.getName());

        if (exists) {throw new DuplicateResourceException("Product", "name", request.getName());}

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setDiscount(request.getDiscount());
        product.setQuantity(request.getQuantity());
        product.setImage("Default.png");
        product.setCategory(category);
        //product.setUser(authUtil.loggedInUser());
        product.setSpecialPrice(calculateSpecialPrice(product.getPrice(), product.getDiscount()));

        Product saved = productRepository.save(product);

        return modelMapper.map(saved, ProductResponse.class);
    }

    @Override
    public PagedProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort.Direction direction = Sort.Direction.fromString(sortOrder.toLowerCase());
        Sort sortByAndOrder =  Sort.by(direction, sortBy.toLowerCase());

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Product> productPage = productRepository.findAll(pageable);

        List<ProductResponse> productResponses = productPage.getContent()
                .stream()
                .map(product -> modelMapper.map(product, ProductResponse.class))
                .toList();

        return new PagedProductResponse(
                productResponses,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast()
        );
    }

    @Override
    public PagedProductResponse searchByCategory(Long id, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category", "categoryId", id));

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> pageProducts = productRepository.findByCategoryOrderByPriceAsc(category, pageDetails);

        List<Product> products = pageProducts.getContent();

        if(products.isEmpty()){
            throw new ResourceNotFoundException("Category", "id", id);
        }

        List<ProductResponse> productResponses = products.stream()
                .map(product -> modelMapper.map(product, ProductResponse.class))
                .toList();

        return new PagedProductResponse(
                productResponses,
                pageDetails.getPageNumber(),
                pageDetails.getPageSize(),
                pageProducts.getTotalElements(),
                pageProducts.getTotalPages(),
                pageProducts.isLast()
        );
    }

    @Override
    public PagedProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> pageProducts = productRepository.findByNameLikeIgnoreCase('%' + keyword + '%', pageDetails);

        List<Product> products = pageProducts.getContent();

        if(products.isEmpty()){
            throw new ResourceNotFoundException("keyword", "", keyword);
        }

        List<ProductResponse> productResponses = products.stream()
                .map(product -> modelMapper.map(product, ProductResponse.class))
                .toList();

        return new PagedProductResponse(
                productResponses,
                pageDetails.getPageNumber(),
                pageDetails.getPageSize(),
                pageProducts.getTotalElements(),
                pageProducts.getTotalPages(),
                pageProducts.isLast()
        );
    }

    @Override
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setDiscount(request.getDiscount());
        product.setQuantity(request.getQuantity());
        product.setSpecialPrice(calculateSpecialPrice(request.getPrice(), request.getDiscount()));

        Product saved = productRepository.save(product);

        return modelMapper.map(saved, ProductResponse.class);
    }

    @Transactional
    public ProductResponse updateProductImage(Long productId, MultipartFile image) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        // Upload new file (validations happen inside FileServiceImpl)
        String filename = fileService.uploadImage(image);

        // Build the public url (or absolute path) to save in entity
        String fileUrl = fileService.buildFileUrl(filename);

        // Remember old image so we can delete it later (do not delete default placeholder)
        String oldImage = product.getImage();

        product.setImage(fileUrl);
        Product saved = productRepository.save(product);

        // Best effort delete old image (log on failure) — don't break the transaction
        if (oldImage != null && !oldImage.contains("default.png")) {
            try {
                fileService.deleteImage(oldImage);
            } catch (Exception ex) {
                // log and continue — we don't want to fail the update because delete failed
                log.warn("Failed to delete old image {} for product {}: {}", oldImage, productId, ex.getMessage());
            }
        }

        return modelMapper.map(saved, ProductResponse.class);
    }



    private double calculateSpecialPrice(double price, double discount) {
        return price - ((discount / 100.0) * price);
    }
}
