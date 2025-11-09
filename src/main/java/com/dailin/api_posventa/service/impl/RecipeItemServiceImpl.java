package com.dailin.api_posventa.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dailin.api_posventa.dto.request.SaveRecipeItem;
import com.dailin.api_posventa.dto.response.GetDish;
import com.dailin.api_posventa.dto.response.GetRecipeItemComplete;
import com.dailin.api_posventa.exception.ObjectNotFoundException;
import com.dailin.api_posventa.mapper.RecipeItemMapper;
import com.dailin.api_posventa.persistence.entity.Dish;
import com.dailin.api_posventa.persistence.entity.RecipeItem;
import com.dailin.api_posventa.persistence.entity.Product;
import com.dailin.api_posventa.persistence.repository.RecipeItemCrudRepository;
import com.dailin.api_posventa.service.DishService;
import com.dailin.api_posventa.service.RecipeItemService;
import com.dailin.api_posventa.service.ProductService;

@Service
@Transactional
public class RecipeItemServiceImpl implements RecipeItemService  {

    @Autowired
    private RecipeItemCrudRepository recipeItemCrudRepository;

    @Autowired
    private DishService dishService;

    @Autowired
    private ProductService productService;

    @Override
    @Transactional(readOnly = true)
    public Page<GetRecipeItemComplete> findAll(Pageable pageable) {
        return recipeItemCrudRepository.findAll(pageable)   
            .map(RecipeItemMapper::toGetRecipeItemDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GetDish.GetRecipeItem> findAllByDishId(Long dishId, Pageable pageable) {
        return recipeItemCrudRepository.findByDishId(dishId, pageable)
            .map(RecipeItemMapper::toGetDishIngrendientDto);
    }

    @Transactional(readOnly = true)
    @Override
    public GetRecipeItemComplete findOneById(Long id) {
        return RecipeItemMapper.toGetRecipeItemDto(this.findOneEntityById(id));
    }

    @Override
    public RecipeItem findOneEntityById(Long id) {
        return recipeItemCrudRepository.findById(id)   
            .orElseThrow(() -> new ObjectNotFoundException("[RecipeItem: " + Long.toString(id) + " ]"));
    }

    @Override
    public GetRecipeItemComplete updatedOneById(Long id, SaveRecipeItem saveDto) {
        // Buscar la entidad de Receta existente
        RecipeItem oldRecipeItem = this.findOneEntityById(id);

        // Buscar las nuevas entidades (Dish y Product)
        Dish newDish = dishService.findOneEntityById(saveDto.dishId());
        Product newProduct = productService.findOneEntityById(saveDto.productId());

        // El mapper actualizará los campos quantity, dish y product.
        // NOTA: Si el mapper tiene lógica para actualizar solo la cantidad, mejor
        RecipeItemMapper.updateEntity(oldRecipeItem, saveDto, newDish, newProduct);
        
        // Opcional: Validar unicidad. La restricción de unicidad (@UniqueConstraint) a nivel de DB 
        // ya debería manejar la violación, pero si el (dishId, productId) cambia a uno ya existente, 
        // la DB lanzará un DataIntegrityViolationException (que tu GlobalExceptionHandler capturará).

        // Persistir los cambios y Mapear a DTO de respuesta
        RecipeItem updatedRecipeItem = recipeItemCrudRepository.save(oldRecipeItem);
        return RecipeItemMapper.toGetRecipeItemDto(updatedRecipeItem);
    }

    @Override
    public GetRecipeItemComplete createOne(SaveRecipeItem saveDto) {
        
        // buscar las entidades necesarias
        Dish dish = dishService.findOneEntityById(saveDto.dishId());
        Product product = productService.findOneEntityById(saveDto.productId());

        // mapear y asignar los objects Product y Dish
        RecipeItem newRecipeItem = RecipeItemMapper.toEntity(saveDto, dish, product);

        // persistir y dar respuesta para el cliente
        RecipeItem saveRecipeItem = recipeItemCrudRepository.save(newRecipeItem);        
        return RecipeItemMapper.toGetRecipeItemDto(saveRecipeItem);
    }

    @Override
    public void deleteOneById(Long id) {
        
        if(!recipeItemCrudRepository.existsById(id)){
            throw new ObjectNotFoundException("[RecipeItem: " + Long.toString(id) + " ]");
        }

        recipeItemCrudRepository.deleteById(id);
    }

}
