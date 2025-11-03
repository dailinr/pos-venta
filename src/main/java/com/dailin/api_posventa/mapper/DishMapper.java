package com.dailin.api_posventa.mapper;

import java.util.List;

import com.dailin.api_posventa.dto.request.SaveDish;
import com.dailin.api_posventa.dto.response.GetDish;
import com.dailin.api_posventa.persistence.entity.Dish;

public class DishMapper {

    // Recibe la entidad y devuelve un GetDish
    public static GetDish toGetDto(Dish entity) {

        if(entity == null) return null;
        // La implementación futura debería mapear entity.getCategory() a un GetDish.GetCategory DTO.

        return new GetDish(
            entity.getId(),
            entity.isAvailable(),
            entity.getPrice(), 
            entity.getDescription(), 
            entity.getName(), 
            CategoryMapper.toGetSimpleDto(entity.getCategory())
        );
    }

    // Mapper un List<Dish> a un List<GetDish>
    public static List<GetDish> toGetDtoList(List<Dish> entities) {

        if(entities == null) return null;

        return entities.stream()
            .map(DishMapper::toGetDto)
            .toList();
    } 

    // Pasar de un SaveDish a una entidad Dish
    public static Dish toEntity(SaveDish saveDto) {

        if(saveDto == null) return null;

        Dish newDish = new Dish();

        newDish.setAvailable(saveDto.available());
        newDish.setDescription(saveDto.description());
        newDish.setName(saveDto.name());
        newDish.setPrice(saveDto.price());
        // La asignación de la Category debe ocurrir en el servicio
        // newDish.setsetCategoryId(saveDto.categoryId()); // Si usas el ID primitivo

        return newDish;
    }

    public static void updateEntity(Dish oldDish, SaveDish saveDto) {
        
        if(oldDish == null || saveDto == null) return;

        oldDish.setAvailable(saveDto.available());
        oldDish.setDescription(saveDto.description());
        oldDish.setName(saveDto.name());
        oldDish.setPrice(saveDto.price());
    }
}