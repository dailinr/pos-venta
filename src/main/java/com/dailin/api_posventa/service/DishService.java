package com.dailin.api_posventa.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dailin.api_posventa.dto.request.SaveDish;
import com.dailin.api_posventa.dto.response.GetDish;

public interface DishService {
    
    Page<GetDish> findAll(Boolean available, Pageable pageable);

    GetDish findOneById(Long id);

    GetDish updtedOneById(Long id, SaveDish saveDto);

    GetDish createOne(SaveDish saveDto);

    void deleteOneById(Long id);
}