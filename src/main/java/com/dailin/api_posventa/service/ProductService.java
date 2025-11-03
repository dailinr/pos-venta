package com.dailin.api_posventa.service;

import java.util.List;

import com.dailin.api_posventa.persistence.entity.Product;

public interface ProductService {

    List<Product> findAll();

    Product findOneById(Long id);

    Product updtedOneById(Long id, Product product);

    Product createOne(Product product);

    void deleteOneById(Long id);

}