package com.dailin.api_posventa.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dailin.api_posventa.dto.request.SaveCategory;
import com.dailin.api_posventa.dto.response.GetCategorySimple;
import com.dailin.api_posventa.persistence.entity.Category;

public interface CategoryService {

    Page<GetCategorySimple> findAll(Pageable pageable);

    GetCategorySimple findOneById(Long id);

    Category finOneEntityById(Long id);

    GetCategorySimple updtedOneById(Long id, SaveCategory saveDto);

    GetCategorySimple createOne(SaveCategory saveDto);

    void deleteOneById(Long id);
}