package com.dailin.api_posventa.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dailin.api_posventa.exception.ObjectNotFoundException;
import com.dailin.api_posventa.persistence.entity.Dish;
import com.dailin.api_posventa.service.DishService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/dishes")
public class DishController {

    @Autowired
    private DishService dishService;

    @GetMapping
    public List<Dish> findAll() {
        return dishService.findAll();
    }
    
    @GetMapping(value = "/{id}")
    public ResponseEntity<Dish> findOneById(@PathVariable Long id) {

        try {
            return ResponseEntity.ok(dishService.findOneById(id));
        } catch (ObjectNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Dish> createOne(
        @RequestBody Dish Dish, HttpServletRequest request
    ){
        Dish dishCreated = dishService.createOne(Dish);
        String baseUrl = request.getRequestURL().toString();

        // localizacion para el Dish recien creado 
        URI newLocation = URI.create(baseUrl + "/" + dishCreated.getId());

        return ResponseEntity.created(newLocation).body(dishCreated);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Dish> updatedOneById(
        @PathVariable Long id, @RequestBody Dish Dish
    ){

        try {
            Dish dishUpdated = dishService.updtedOneById(id, Dish);
            return ResponseEntity.ok(dishUpdated);
        } 
        catch (ObjectNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteOneById(@PathVariable Long id){

        try {
            dishService.deleteOneById(id);
            return ResponseEntity.noContent().build();
        } 
        catch (ObjectNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
