package com.dailin.api_posventa.mapper;

import java.util.List;

import com.dailin.api_posventa.dto.request.SaveProduct;
import com.dailin.api_posventa.dto.response.GetProduct;
import com.dailin.api_posventa.persistence.entity.Product;

public class ProductMapper {

    // recibe la entidad y devuelve un getproduct - save put
    public static GetProduct toGetDto(Product entity) {

        if(entity == null) return null;

        return new GetProduct(
            entity.getId(),
            entity.isAvailable(),
            entity.getPrice(), 
            entity.getQuantityAvailable(), 
            entity.getDescription(), 
            entity.getName(), 
            entity.getMeasureUnit(), 
            CategoryMapper.toGetSimpleDto(entity.getCategory())
        );

    }

    // mapper un List<Product> a un List<GetProduct>
    public static List<GetProduct> toGetDtoList(List<Product> entities) {

        if(entities == null) return null;

        return entities.stream() // List<Product> -> Stream<Product>
            .map(ProductMapper::toGetDto) // Stream<Product> -> Stream<GetProduct>
            .toList(); // finalmente se convierte otra vez a un List<Product>
    } 

    // pasar de un saveProduct a una entidad Product
    public static Product toEntity(SaveProduct saveDto) {

        if(saveDto == null) return null;

        Product newProduct = new Product();

        newProduct.setAvailable(saveDto.available());
        newProduct.setDescription(saveDto.description());
        newProduct.setMeasureUnit(saveDto.measureUnit());
        newProduct.setName(saveDto.name());
        // newProduct.setPrice(saveDto.price());
        newProduct.setQuantityAvailable(saveDto.quantityAvailable());

        return newProduct;
    }

    // actualiza los valores de la entidad por los nuevo que envia el usuario
    public static void updateEntity(Product oldProduct, SaveProduct saveDto) {
        
        if(oldProduct == null || saveDto == null) return;

        oldProduct.setAvailable(saveDto.available());
        oldProduct.setDescription(saveDto.description());
        oldProduct.setMeasureUnit(saveDto.measureUnit());
        oldProduct.setName(saveDto.name());
        // oldProduct.setPrice(saveDto.price());
        oldProduct.setQuantityAvailable(saveDto.quantityAvailable());
    }
}