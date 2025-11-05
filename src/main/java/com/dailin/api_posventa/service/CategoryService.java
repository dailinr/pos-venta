package com.dailin.api_posventa.service;

import java.util.List;

import com.dailin.api_posventa.dto.request.SaveCategory;
import com.dailin.api_posventa.dto.response.GetCategorySimple;

public interface CategoryService {

    List<GetCategorySimple> findAll();

    GetCategorySimple findOneById(Long id);

    GetCategorySimple updtedOneById(Long id, SaveCategory saveDto);

    GetCategorySimple createOne(SaveCategory saveDto);

    void deleteOneById(Long id);
}