package com.dailin.api_posventa.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dailin.api_posventa.dto.request.SaveProduct;
import com.dailin.api_posventa.dto.response.GetItem;
import com.dailin.api_posventa.dto.response.GetProduct;
import com.dailin.api_posventa.persistence.entity.Product;
import com.dailin.api_posventa.utils.CategoryType;

public interface ProductService {

    Page<GetProduct> findAll(Boolean available, String categoryTitle, CategoryType categoryType, Pageable pageable);

    Page<GetItem> findAllByRootCategory(Long rootCategoryId, Pageable pageable);

    GetProduct findOneById(Long id);

    Product findOneEntityById(Long id);

    GetProduct updtedOneById(Long id, SaveProduct saveDto);

    Product save(Product entity);

    GetProduct createOne(SaveProduct saveDto);

    void decreaseStock(Product product, int quantityRequest, String errorMessage);

    void deleteOneById(Long id);

}