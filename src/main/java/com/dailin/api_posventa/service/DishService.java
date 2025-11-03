package com.dailin.api_posventa.service;

import java.util.List;

import com.dailin.api_posventa.persistence.entity.Dish;

public interface DishService {
    
    List<Dish> findAll();

    Dish findOneById(Long id);

    Dish updtedOneById(Long id, Dish dish);

    Dish createOne(Dish dish);

    void deleteOneById(Long id);
}