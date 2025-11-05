package com.dailin.api_posventa.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dailin.api_posventa.dto.request.SaveDish;
import com.dailin.api_posventa.dto.response.GetDish;
import com.dailin.api_posventa.exception.ObjectNotFoundException;
import com.dailin.api_posventa.mapper.DishMapper;
import com.dailin.api_posventa.persistence.entity.Dish;
import com.dailin.api_posventa.persistence.repository.DishCrudRepository;
import com.dailin.api_posventa.persistence.specification.FindAllDishSpecification;
import com.dailin.api_posventa.service.DishService;

@Transactional
@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishCrudRepository dishCrudRepository;

    @Transactional(readOnly = true)
    @Override
    public List<GetDish> findAll(Boolean available) {

        FindAllDishSpecification dishSpecification = new FindAllDishSpecification(available);
        List<Dish> entities = dishCrudRepository.findAll(dishSpecification); // obtenemos las entidades
        return DishMapper.toGetDtoList(entities); 
    }

    @Transactional(readOnly = true)
    @Override
    public GetDish findOneById(Long id) {
        // primero buscamos la entidad. la enviamos a toGeDto para que devuelva un getDish
        return DishMapper.toGetDto(this.findOneEntityById(id)); // respuesta 
    }

    private Dish findOneEntityById(Long id) {
        return dishCrudRepository.findById(id) // devuelve un optional<Entity>
            .orElseThrow(() -> new ObjectNotFoundException("[dish: " + Long.toString(id) + "]"));
    }

    @Override
    public GetDish updtedOneById(Long id, SaveDish saveDto) {
        
        Dish oldDish = this.findOneEntityById(id); // obtenemos la entidad

        DishMapper.updateEntity(oldDish, saveDto); // actualiza los valores de la entidad

        return DishMapper.toGetDto(dishCrudRepository.save(oldDish)); // getdish de respuesta
    }

    @Override
    public GetDish createOne(SaveDish saveDto) {
        Dish newDish = DishMapper.toEntity(saveDto); // convierte a entidad
        Dish savedDish = dishCrudRepository.save(newDish);  // devuelve la entidad con el ID autogenerado.
        
        return DishMapper.toGetDto(savedDish); // respuesta
    }

    @Override
    public void deleteOneById(Long id) {
        
        // mientras tanto
        if(dishCrudRepository.existsById(id)){
            dishCrudRepository.deleteById(id);
            return;
        }

        throw new ObjectNotFoundException("[dish: " +Long.toString(id)+ "]");
    }
}