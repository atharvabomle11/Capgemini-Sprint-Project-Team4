package com.example.DemoCheck.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.example.DemoCheck.entity.Products;

@RepositoryRestResource(path = "products")
public interface ProductRepository extends JpaRepository<Products, String> {

}
