package com.ecommerce.project.repositories;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByCategory_IdAndName(Long categoryId, String name);

    Page<Product> findByCategoryOrderByPriceAsc(Category category, Pageable pageDetails);

    Page<Product> findByNameLikeIgnoreCase(String s, Pageable pageDetails);
}
