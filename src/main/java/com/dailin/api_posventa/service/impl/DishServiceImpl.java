package com.dailin.api_posventa.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dailin.api_posventa.dto.request.SaveDish;
import com.dailin.api_posventa.dto.response.GetDish;
import com.dailin.api_posventa.exception.ObjectNotFoundException;
import com.dailin.api_posventa.mapper.DishMapper;
import com.dailin.api_posventa.persistence.entity.Category;
import com.dailin.api_posventa.persistence.entity.Dish;
import com.dailin.api_posventa.persistence.repository.DishCrudRepository;
import com.dailin.api_posventa.persistence.specification.FindAllDishSpecification;
import com.dailin.api_posventa.service.CategoryService;
import com.dailin.api_posventa.service.DishService;
import com.dailin.api_posventa.utils.CategoryType;

@Transactional
@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishCrudRepository dishCrudRepository;

    @Autowired
    private CategoryService categoryService;

    @Transactional(readOnly = true)
    @Override
    public Page<GetDish> findAll(Boolean available, Pageable pageable) {

        FindAllDishSpecification dishSpecification = new FindAllDishSpecification(available);
        Page<Dish> entities = dishCrudRepository.findAll(dishSpecification, pageable); // obtenemos las entidades
        return entities.map(DishMapper::toGetDto); 
    }

    @Transactional(readOnly = true)
    @Override
    public GetDish findOneById(Long id) {
        // primero buscamos la entidad. la enviamos a toGeDto para que devuelva un getDish
        return DishMapper.toGetDto(this.findOneEntityById(id)); // respuesta 
    }

    @Override
    public Dish findOneEntityById(Long id) {
        return dishCrudRepository.findById(id) // devuelve un optional<Entity>
            .orElseThrow(() -> new ObjectNotFoundException("[dish: " + Long.toString(id) + "]"));
    }

    @Override
    public GetDish updtedOneById(Long id, SaveDish saveDto) {
        
        Dish oldDish = this.findOneEntityById(id); // obtenemos la entidad

        DishMapper.updateEntity(oldDish, saveDto); // actualiza los valores de la entidad
        this.assignCategory(oldDish, saveDto.categoryId());

        return DishMapper.toGetDto(dishCrudRepository.save(oldDish)); // getdish de respuesta
    }

    @Override
    public GetDish createOne(SaveDish saveDto) {
        
        Dish newDish = DishMapper.toEntity(saveDto); // convierte a entidad

        this.assignCategory(newDish, saveDto.categoryId()); // asignar categoria
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

    private void assignCategory(Dish dish, Long categoryId){
        
        if(categoryId == null){
            throw new IllegalArgumentException("El plato debe tener una categoria asignada.");
        }

        Category category = categoryService.finOneEntityById(categoryId);

        if(category.getType() != CategoryType.MENU) {
            throw new IllegalArgumentException(
                "Los platillos no pueden ser asignados a una categoria '" + category.getName() + 
                "', ya que es del tipo '"+category.getType()+"'."
            );
        }

        dish.setCategory(category);
    }
}