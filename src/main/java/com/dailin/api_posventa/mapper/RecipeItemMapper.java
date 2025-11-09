package com.dailin.api_posventa.mapper;

import java.util.List;

import com.dailin.api_posventa.dto.request.SaveRecipeItem;
import com.dailin.api_posventa.dto.response.GetDish;
import com.dailin.api_posventa.dto.response.GetRecipeItemComplete;
import com.dailin.api_posventa.dto.response.GetProduct;
import com.dailin.api_posventa.persistence.entity.Dish;
import com.dailin.api_posventa.persistence.entity.RecipeItem;
import com.dailin.api_posventa.persistence.entity.Product;

public class RecipeItemMapper {

    public static GetRecipeItemComplete toGetRecipeItemDto(RecipeItem entity) {

        if(entity == null) return null;

        // la entidad no será null si llegamos aquí

        return new GetRecipeItemComplete(
            entity.getId(), 
            entity.getDish().getId(), 
            entity.getDish().getName(), 
            entity.getProduct().getId(), 
            entity.getProduct().getName(), 
            entity.getQuantity()
        );
    }

    public static GetProduct.GetRecipeItem toGetProductIngrendientDto(RecipeItem entity){

        if(entity == null) return null;

        return new GetProduct.GetRecipeItem(
            entity.getId(),
            entity.getQuantity(), 
            entity.getDish().getName(), 
            entity.getDish().getId()
        );
    }

    public static GetDish.GetRecipeItem toGetDishIngrendientDto(RecipeItem entity){

        if(entity == null) return null;

        return new GetDish.GetRecipeItem(
            entity.getId(), 
            entity.getProduct().getId(), 
            entity.getProduct().getName(), 
            entity.getQuantity()
        );
    }

    public static List<GetRecipeItemComplete> toGetRecipeItemDtoList(List<RecipeItem> entities){

        if(entities == null) return null;

        return entities.stream()
            .map(RecipeItemMapper::toGetRecipeItemDto)
            .toList();
    }

    // Este método debe ser llamado solo por el Servicio, una vez que Dish y Product han sido buscados.
    public static RecipeItem toEntity(
        SaveRecipeItem saveDto, Dish dish, Product product
    ) {
        if(saveDto == null) return null;

        RecipeItem newRecipeItem = new RecipeItem();

        newRecipeItem.setDish(dish);
        newRecipeItem.setProduct(product);
        newRecipeItem.setQuantity(saveDto.quantity());

        return newRecipeItem;
    }

    public static void updateEntity(
        RecipeItem entity, SaveRecipeItem dto, 
        Dish dish, Product product
    ) {
        if(entity ==  null || dto == null) return;

        entity.setDish(dish);
        entity.setProduct(product);
        entity.setQuantity(dto.quantity());
    }
}