package com.ecommerce.project.service;

import com.ecommerce.project.dto.product.CreateProductRequest;
import com.ecommerce.project.dto.product.ProductResponse;
import com.ecommerce.project.exceptions.DuplicateResourceException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.repositories.CategoryRepository;
import com.ecommerce.project.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper modelMapper;

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
        product.setImage(request.getImage());
        product.setCategory(category);
        //product.setUser(authUtil.loggedInUser());
        double specialPrice = product.getPrice() -
                ((product.getDiscount() / 100) * product.getPrice());
        product.setSpecialPrice(specialPrice);

        Product saved = productRepository.save(product);

        return modelMapper.map(saved, ProductResponse.class);
    }


}
