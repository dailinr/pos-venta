package com.dailin.api_posventa.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dailin.api_posventa.dto.request.SaveRecipeItem;
import com.dailin.api_posventa.dto.response.GetDish;
import com.dailin.api_posventa.dto.response.GetRecipeItemComplete;
import com.dailin.api_posventa.persistence.entity.RecipeItem;

public interface RecipeItemService {

    Page<GetRecipeItemComplete> findAll(Pageable pageable);

    Page<GetDish.GetRecipeItem> findAllByDishId(Long dishId, Pageable pageable);

    GetRecipeItemComplete findOneById(Long id);

    RecipeItem findOneEntityById(Long id);

    GetRecipeItemComplete updatedOneById(Long id, SaveRecipeItem saveDto);

    GetRecipeItemComplete createOne(SaveRecipeItem saveDto);

    void deleteOneById(Long id);
}