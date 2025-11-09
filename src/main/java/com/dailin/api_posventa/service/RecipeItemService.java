package com.dailin.api_posventa.service;

import java.util.List;

import com.dailin.api_posventa.dto.request.SaveRecipeItem;
import com.dailin.api_posventa.dto.response.GetRecipeItemComplete;
import com.dailin.api_posventa.persistence.entity.RecipeItem;

public interface RecipeItemService {

    List<GetRecipeItemComplete> findAll();

    GetRecipeItemComplete findOneById(Long id);

    RecipeItem findOneEntityById(Long id);

    GetRecipeItemComplete updatedOneById(Long id, SaveRecipeItem saveDto);

    GetRecipeItemComplete createOne(SaveRecipeItem saveDto);

    void deleteOneById(Long id);
}