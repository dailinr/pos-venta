package com.dailin.api_posventa.mapper;

import com.dailin.api_posventa.dto.response.GetDish;
import com.dailin.api_posventa.dto.response.GetItem;
import com.dailin.api_posventa.dto.response.GetProduct;

public class ItemMapper {

    public static GetItem toGetDishItemDto(GetDish getDish) {

        if(getDish == null) return null;

        return new GetItem(
            getDish.id(), 
            getDish.name(), 
            getDish.price(), 
            getDish.available(), 
            getDish.description(), 
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
            getProduct.categoryId(), 
            getProduct.categoryTitle()
        );
    }

}
