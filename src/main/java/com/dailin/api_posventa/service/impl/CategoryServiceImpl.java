package com.dailin.api_posventa.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dailin.api_posventa.dto.request.SaveCategory;
import com.dailin.api_posventa.dto.response.GetCategorySimple;
import com.dailin.api_posventa.exception.ObjectNotFoundException;
import com.dailin.api_posventa.mapper.CategoryMapper;
import com.dailin.api_posventa.persistence.entity.Category;
import com.dailin.api_posventa.persistence.repository.CategoryCrudRepository;
import com.dailin.api_posventa.service.CategoryService;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    private CategoryCrudRepository categoryCrudRepository;

    @Transactional(readOnly = true)
    @Override
    public List<GetCategorySimple> findAll() {
        
        List<Category> entities = categoryCrudRepository.findAll();
        return CategoryMapper.toGetSimpleDtoList(entities);
    }

    @Transactional(readOnly = true)
    @Override
    public GetCategorySimple findOneById(Long id) {
        return CategoryMapper.toGetSimpleDto(this.finOneEntityById(id));
    }

    private Category finOneEntityById(Long id) {
        return categoryCrudRepository.findById(id)
            .orElseThrow(() -> new ObjectNotFoundException("[category: " + Long.toString(id) + " ]"));
    }

    @Override
    public GetCategorySimple updtedOneById(Long id, SaveCategory saveDto) {
        
        Category oldCategory = this.finOneEntityById(id);

        CategoryMapper.updateEntity(oldCategory, saveDto);
        
        return CategoryMapper.toGetSimpleDto(categoryCrudRepository.save(oldCategory));
    }

    @Override
    public GetCategorySimple createOne(SaveCategory saveDto) {
        Category newCategory = CategoryMapper.toEntity(saveDto);
        Category saveCategory = categoryCrudRepository.save(newCategory);

        return CategoryMapper.toGetSimpleDto(saveCategory);
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
