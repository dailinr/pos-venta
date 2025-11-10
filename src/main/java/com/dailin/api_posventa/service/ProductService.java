package com.dailin.api_posventa.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dailin.api_posventa.dto.request.SaveProduct;
import com.dailin.api_posventa.dto.response.GetProduct;
import com.dailin.api_posventa.persistence.entity.Product;

public interface ProductService {

    Page<GetProduct> findAll(Boolean available, String categoryTitle, String categoryType, Pageable pageable);

    GetProduct findOneById(Long id);

    Product findOneEntityById(Long id);

    GetProduct updtedOneById(Long id, SaveProduct saveDto);

    GetProduct createOne(SaveProduct saveDto);

    void deleteOneById(Long id);

}