package com.dailin.api_posventa.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dailin.api_posventa.dto.request.SaveDish;
import com.dailin.api_posventa.dto.response.GetDish;
import com.dailin.api_posventa.service.DishService;
import com.dailin.api_posventa.service.RecipeItemService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/dishes")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RecipeItemService recipeItemService;

    @GetMapping
    public ResponseEntity<Page<GetDish>> findAll(
        @RequestParam(required = false) Boolean available, Pageable pageable
    ) {
        Page<GetDish> dishes = dishService.findAll(available, pageable);
        return ResponseEntity.ok(dishes);
    }
    
    @GetMapping(value = "/{id}")
    public ResponseEntity<GetDish> findOneById(@PathVariable Long id) {
        return ResponseEntity.ok(dishService.findOneById(id));
    }

    @GetMapping(value = "/{id}/ingredients")
    public ResponseEntity<Page<GetDish.GetRecipeItem>> findAllIngredientsForDishById(
        @PathVariable @Valid Long id, Pageable pageable
    ){
        return ResponseEntity.ok(recipeItemService.findAllByDishId(id, pageable));
    }

    @PostMapping
    public ResponseEntity<GetDish> createOne(
        @RequestBody @Valid SaveDish saveDto, HttpServletRequest request
    ){
        GetDish dishCreated = dishService.createOne(saveDto);
        String baseUrl = request.getRequestURL().toString();

        // localizacion para el Dish recien creado 
        URI newLocation = URI.create(baseUrl + "/" + dishCreated.id());

        return ResponseEntity.created(newLocation).body(dishCreated);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<GetDish> updatedOneById(
        @PathVariable Long id, @RequestBody @Valid SaveDish saveDto
    ){
        GetDish dishUpdated = dishService.updtedOneById(id, saveDto);
        return ResponseEntity.ok(dishUpdated);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteOneById(@PathVariable Long id){

        dishService.deleteOneById(id);
        return ResponseEntity.noContent().build();
    }
}
