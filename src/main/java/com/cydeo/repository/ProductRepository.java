package com.cydeo.repository;

import com.cydeo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductRepository extends JpaRepository<Product, Long> {

    Product findByIdAndIsDeleted(Long id, boolean deleted);
}
