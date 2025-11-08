package com.dailin.api_posventa.mapper;

import java.util.List;

import com.dailin.api_posventa.dto.request.SaveIngredient;
import com.dailin.api_posventa.dto.response.GetDish;
import com.dailin.api_posventa.dto.response.GetIngredientComplete;
import com.dailin.api_posventa.dto.response.GetProduct;
import com.dailin.api_posventa.persistence.entity.Dish;
import com.dailin.api_posventa.persistence.entity.Ingredient;
import com.dailin.api_posventa.persistence.entity.Product;

public class IngredientMapper {

    public static GetIngredientComplete toGetIngredientDto(Ingredient entity) {

        if(entity == null) return null;

        // la entidad no será null si llegamos aquí

        return new GetIngredientComplete(
            entity.getId(), 
            entity.getDish().getId(), 
            entity.getDish().getName(), 
            entity.getProduct().getId(), 
            entity.getProduct().getName(), 
            entity.getQuantity()
        );
    }

    public static GetProduct.GetIngredient toGetProductIngrendientDto(Ingredient entity){

        if(entity == null) return null;

        return new GetProduct.GetIngredient(
            entity.getId(),
            entity.getQuantity(), 
            entity.getDish().getName(), 
            entity.getDish().getId()
        );
    }

    public static GetDish.GetIngredient toGetDishIngrendientDto(Ingredient entity){

        if(entity == null) return null;

        return new GetDish.GetIngredient(
            entity.getId(), 
            entity.getProduct().getId(), 
            entity.getProduct().getName(), 
            entity.getQuantity()
        );
    }

    public static List<GetIngredientComplete> toGetIngredientDtoList(List<Ingredient> entities){

        if(entities == null) return null;

        return entities.stream()
            .map(IngredientMapper::toGetIngredientDto)
            .toList();
    }

    // Este método debe ser llamado solo por el Servicio, una vez que Dish y Product han sido buscados.
    public static Ingredient toEntity(
        SaveIngredient saveDto, Dish dish, Product product
    ) {
        if(saveDto == null) return null;

        Ingredient newIngredient = new Ingredient();

        newIngredient.setDish(dish);
        newIngredient.setProduct(product);
        newIngredient.setQuantity(saveDto.quantity());

        return newIngredient;
    }

    public static void updateEntity(
        Ingredient entity, SaveIngredient dto, 
        Dish dish, Product product
    ) {
        if(entity ==  null || dto == null) return;

        entity.setDish(dish);
        entity.setProduct(product);
        entity.setQuantity(dto.quantity());
    }
}