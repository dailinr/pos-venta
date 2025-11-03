package com.dailin.api_posventa.service;

import java.util.List;

import com.dailin.api_posventa.persistence.entity.Category;

public interface CategoryService {

    List<Category> findAll();

    Category findOneById(Long id);

    Category updtedOneById(Long id, Category category);

    Category createOne(Category category);

    void deleteOneById(Long id);
}