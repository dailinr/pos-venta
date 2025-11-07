package com.dailin.api_posventa.mapper;

import java.util.List;

import com.dailin.api_posventa.dto.request.SaveCategory;
import com.dailin.api_posventa.dto.response.GetCategorySimple;
import com.dailin.api_posventa.persistence.entity.Category;

public class CategoryMapper {

    // --- Mapeo de SALIDA ---

    /* 
        Recibe la entidad y devuelve un DTO simple para uso directo (ej: GET /categories).
        El método toGetSimpleDto devuelve GetCategorySimple
    */
    public static GetCategorySimple toGetSimpleDto(Category entity) {

        if(entity == null) return null;

        // Obtenemos el ID del padre (o null si no tiene)
        Long parentId = entity.getParentCategory() != null 
            ? entity.getParentCategory().getId() : null;
        
        String parentTitle = entity.getParentCategory() != null
            ? entity.getParentCategory().getName() != null
                ?  entity.getParentCategory().getName() : null
            : null;


        return new GetCategorySimple(
            entity.getId(),
            entity.getName(), 
            entity.getType(),
            entity.isPriceEnabled(),
            parentId,
            parentTitle
        );
    }

    // Mapper un List<Category> a un List<GetCategorySimple>
    public static List<GetCategorySimple> toGetSimpleDtoList(List<Category> entities) {

        if(entities == null) return null;

        return entities.stream()
            .map(CategoryMapper::toGetSimpleDto)
            .toList();
    } 

    // --- Mapeo de ENTRADA (Persistencia) ---

    // Pasar de un SaveCategory a una entidad Category
    public static Category toEntity(SaveCategory saveDto) {

        if(saveDto == null) return null;

        Category newCategory = new Category();

        newCategory.setName(saveDto.name());
        newCategory.setType(saveDto.type());
        newCategory.setPriceEnabled(saveDto.priceEnabled());
        
        // La asignación del parentCategory (si existe) debe ocurrir en el servicio
        // newCategory.setParentCategory(saveDto.parentCategoryId()); 

        return newCategory;
    }

    // Método para actualizar la entidad (similar a tu MovieMapper)
    public static void updateEntity(Category oldCategory, SaveCategory saveDto) {
        if(oldCategory == null || saveDto == null) return;

        oldCategory.setName(saveDto.name());
        oldCategory.setType(saveDto.type());
        oldCategory.setPriceEnabled(saveDto.priceEnabled());
        
        // NOTA: La actualización del ParentCategory (entidad) se hace en el Servicio.
    }
}