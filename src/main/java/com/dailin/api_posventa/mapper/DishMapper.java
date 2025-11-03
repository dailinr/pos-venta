package com.dailin.api_posventa.mapper;

import java.util.List;

import com.dailin.api_posventa.dto.request.SaveDish;
import com.dailin.api_posventa.dto.response.GetDish;
import com.dailin.api_posventa.persistence.entity.Dish;

public class DishMapper {

    // El DTO GetCategory es una estructura interna en GetDish, la reutilizaremos.
    
    // Recibe la entidad y devuelve un GetDish
    public static GetDish toGetDto(Dish entity) {

        if(entity == null) return null;

        // NOTA: Dejamos el campo 'category' en null por tu solicitud.
        // La implementación futura debería mapear entity.getCategory() a un GetDish.GetCategory DTO.

        return new GetDish(
            entity.isAvailable(),
            entity.getPrice(), 
            entity.getDescription(), 
            entity.getName(), 
            null // <--- Dejado en null, como se solicitó, para la futura implementación de la categoría
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
        // newDish.setCategoryId(saveDto.categoryId()); // Si usas el ID primitivo

        return newDish;
    }
}