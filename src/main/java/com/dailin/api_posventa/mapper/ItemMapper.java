package com.dailin.api_posventa.mapper;

import com.dailin.api_posventa.dto.response.GetDish;
import com.dailin.api_posventa.dto.response.GetItem;
import com.dailin.api_posventa.dto.response.GetProduct;
import com.dailin.api_posventa.dto.response.ItemProjection;

public class ItemMapper {

    /**
     * Mapea la Proyección de la DB (ItemProjection) al DTO final (GetItem).
     */
    public static GetItem toGetItemDto(ItemProjection projection) {

        if(projection == null) return null;

        return new GetItem(
            projection.getId(), 
            projection.getName(), 
            projection.getPrice() != null ? projection.getPrice() : 0.0, 
            projection.getAvailable() != null ? projection.getAvailable() : false,
            projection.getDescription(), 
            projection.getItemType(),
            projection.getCategoryId(), 
            projection.getCategoryTitle()
        );
    }
    
    public static GetItem toGetDishItemDto(GetDish getDish) {

        if(getDish == null) return null;

        return new GetItem(
            getDish.id(), 
            getDish.name(), 
            getDish.price(), 
            getDish.available(), 
            getDish.description(), 
            "DISH",
            getDish.categoryId(), 
            getDish.categoryTitle()
        );
    }

    public static GetItem toGetProductItemDto(GetProduct getProduct) {

        if(getProduct == null) return null;

        return new GetItem(
            getProduct.id(), 
            getProduct.name(), 
            getProduct.price(), 
            getProduct.available(), 
            getProduct.description(), 
            "PRODUCT",
            getProduct.categoryId(), 
            getProduct.categoryTitle()
        );
    }

}
