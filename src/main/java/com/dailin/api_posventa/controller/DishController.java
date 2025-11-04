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

import com.dailin.api_posventa.dto.request.SaveDish;
import com.dailin.api_posventa.dto.response.GetDish;
import com.dailin.api_posventa.exception.ObjectNotFoundException;
import com.dailin.api_posventa.service.DishService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/dishes")
public class DishController {

    @Autowired
    private DishService dishService;

    @GetMapping
    public ResponseEntity<List<GetDish>> findAll() {
        return ResponseEntity.ok(dishService.findAll());
    }
    
    @GetMapping(value = "/{id}")
    public ResponseEntity<GetDish> findOneById(@PathVariable Long id) {
        return ResponseEntity.ok(dishService.findOneById(id));
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

        try {
            GetDish dishUpdated = dishService.updtedOneById(id, saveDto);
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
