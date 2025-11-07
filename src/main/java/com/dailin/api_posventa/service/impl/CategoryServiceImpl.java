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

    @Override
    public Category finOneEntityById(Long id) {
        return categoryCrudRepository.findById(id)
            .orElseThrow(() -> new ObjectNotFoundException("[category: " + Long.toString(id) + " ]"));
    }

    @Override
    public GetCategorySimple updtedOneById(Long id, SaveCategory saveDto) {
        
        Category oldCategory = this.finOneEntityById(id);

        // Actualizar campos simples usando el Mapper
        CategoryMapper.updateEntity(oldCategory, saveDto);

        // Lógica de Actualización de Padre
        Long newParentId = saveDto.parentCategoryId();

        // Validación de Ciclo: No puede ser su propia padre.
        if (newParentId != null && id.equals(newParentId)) {    
            throw new IllegalArgumentException("La categoría " + id + " no puede ser su propia categoría padre.");
        }

        // Este método le asignará la categoria padre (si se envia)
        this.assignParentCategory(newParentId, oldCategory);
            
        // Persistir y Mapear
        return CategoryMapper.toGetSimpleDto(categoryCrudRepository.save(oldCategory));
    }

    @Override
    public GetCategorySimple createOne(SaveCategory saveDto) {
        // Convertir DTO a Entidad (datos simples)
        Category newCategory = CategoryMapper.toEntity(saveDto);

        this.assignParentCategory(
            saveDto.parentCategoryId(), newCategory
        );

        // Persistir y Mapear
        Category saveCategory = categoryCrudRepository.save(newCategory);
        return CategoryMapper.toGetSimpleDto(saveCategory);
    }

    @Override
    public void deleteOneById(Long id) {
        
        // mientras tanto
        if(!categoryCrudRepository.existsById(id)){
            throw new ObjectNotFoundException("[category: " +Long.toString(id)+ "]");
        }

        categoryCrudRepository.deleteById(id);
    }

    private void assignParentCategory(Long parentId, Category category) {

        if(parentId != null) {
            Category parent = this.finOneEntityById(parentId);

            if(parent.getType() != category.getType()){
                throw new IllegalArgumentException(
                    "Error de tipo de categoría: La categoría '" + category.getName() + 
                    "' debe tener el mismo tipo (" + parent.getType() + 
                    ") que su categoría padre '" + parent.getName() + "'."
                );
            }
            category.setParentCategory(parent);
        } 
        else {
            // Manejar el caso de que parentId sea null (Categoría raíz)
            category.setParentCategory(null);
        }

    }

}
