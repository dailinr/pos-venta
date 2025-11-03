package com.dailin.api_posventa.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dailin.api_posventa.exception.ObjectNotFoundException;
import com.dailin.api_posventa.persistence.entity.Dish;
import com.dailin.api_posventa.persistence.repository.DishCrudRepository;
import com.dailin.api_posventa.service.DishService;

@Transactional
@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishCrudRepository dishCrudRepository;

    @Transactional(readOnly = true)
    @Override
    public List<Dish> findAll() {
        return dishCrudRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Dish findOneById(Long id) {
        return dishCrudRepository.findById(id)
            .orElseThrow(() -> new ObjectNotFoundException("[dish: " +Long.toString(id)+ "]"));
    }

    @Override
    public Dish updtedOneById(Long id, Dish dish) {
        
        Dish oldDish = this.findOneById(id);
        
        oldDish.setAvailable(dish.isAvailable());
        oldDish.setCategory(dish.getCategory());
        oldDish.setDescription(dish.getDescription());
        oldDish.setName(dish.getName());
        oldDish.setPrice(dish.getPrice());

        return dishCrudRepository.save(oldDish);
    }

    @Override
    public Dish createOne(Dish dish) {
        return dishCrudRepository.save(dish);
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