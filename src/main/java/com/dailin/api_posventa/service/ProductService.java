package com.dailin.api_posventa.service;

import java.util.List;

import com.dailin.api_posventa.dto.request.SaveProduct;
import com.dailin.api_posventa.dto.response.GetProduct;

public interface ProductService {

    List<GetProduct> findAll(Boolean available);

    GetProduct findOneById(Long id);

    GetProduct updtedOneById(Long id, SaveProduct saveDto);

    GetProduct createOne(SaveProduct saveDto);

    void deleteOneById(Long id);

}