package com.dailin.api_posventa.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dailin.api_posventa.exception.ObjectNotFoundException;
import com.dailin.api_posventa.persistence.entity.Category;
import com.dailin.api_posventa.persistence.repository.CategoryCrudRepository;
import com.dailin.api_posventa.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    private CategoryCrudRepository categoryCrudRepository;

    @Override
    public List<Category> findAll() {
        return categoryCrudRepository.findAll();
    }

    @Override
    public Category findOneById(Long id) {
        return categoryCrudRepository.findById(id)
            .orElseThrow(() -> new ObjectNotFoundException("[category: " +Long.toString(id)+ "]"));
    }

    @Override
    public Category updtedOneById(Long id, Category category) {
        
        Category oldCategory = this.findOneById(id);

        oldCategory.setAvailable(category.isAvailable());
        oldCategory.setName(category.getName());
        oldCategory.setType(category.getType());

        return categoryCrudRepository.save(oldCategory);
    }

    @Override
    public Category createOne(Category category) {
        return categoryCrudRepository.save(category);
    }

    @Override
    public void deleteOneById(Long id) {
        
        // mientras tanto
        if(categoryCrudRepository.existsById(id)){
            categoryCrudRepository.deleteById(id);
            return;
        }

        throw new ObjectNotFoundException("[category: " +Long.toString(id)+ "]");
    }
}
