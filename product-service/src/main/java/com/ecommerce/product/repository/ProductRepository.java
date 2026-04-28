package com.ecommerce.product.repository;

import com.ecommerce.product.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    List<Product> findByCategory(String category);

    List<Product> findByActiveTrue();

    List<Product> findByCategoryAndActiveTrue(String category);
}