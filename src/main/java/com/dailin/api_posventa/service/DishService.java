package com.dailin.api_posventa.service;

import java.util.List;

import com.dailin.api_posventa.dto.request.SaveDish;
import com.dailin.api_posventa.dto.response.GetDish;

public interface DishService {
    
    List<GetDish> findAll(Boolean available);

    GetDish findOneById(Long id);

    GetDish updtedOneById(Long id, SaveDish saveDto);

    GetDish createOne(SaveDish saveDto);

    void deleteOneById(Long id);
}