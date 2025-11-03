package com.dailin.api_posventa.mapper;

import java.util.List;

import com.dailin.api_posventa.dto.request.SaveCategory;
import com.dailin.api_posventa.dto.response.GetCategorySimple;
import com.dailin.api_posventa.persistence.entity.Category;

public class CategoryMapper {

    // Recibe la entidad y devuelve un GetCategorySimple
    public static GetCategorySimple toGetSimpleDto(Category entity) {

        if(entity == null) return null;

        return new GetCategorySimple(
            entity.getId(),
            entity.getName(), 
            entity.getType(), 
            entity.isAvailable()
        );
    }

    // Mapper un List<Category> a un List<GetCategorySimple>
    public static List<GetCategorySimple> toGetSimpleDtoList(List<Category> entities) {

        if(entities == null) return null;

        return entities.stream()
            .map(CategoryMapper::toGetSimpleDto)
            .toList();
    } 

    // Pasar de un SaveCategory a una entidad Category
    public static Category toEntity(SaveCategory saveDto) {

        if(saveDto == null) return null;

        Category newCategory = new Category();

        newCategory.setName(saveDto.name());
        newCategory.setType(saveDto.type());
        newCategory.setAvailable(saveDto.available());
        
        // La asignación del parentCategory (si existe) debe ocurrir en el servicio
        // newCategory.setParentCategory(saveDto.parentCategoryId()); 

        return newCategory;
    }
}