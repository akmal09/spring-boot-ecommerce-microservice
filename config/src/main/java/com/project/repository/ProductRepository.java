package com.project.repository;

import com.project.modelpsql.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {
    List<Product> findByOperationType(String operationType);

    List<Product> findAll();
}