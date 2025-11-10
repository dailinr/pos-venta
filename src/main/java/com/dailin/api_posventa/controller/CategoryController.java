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
import org.springframework.web.bind.annotation.RestController;

import com.dailin.api_posventa.dto.request.SaveCategory;
import com.dailin.api_posventa.dto.response.GetCategorySimple;
import com.dailin.api_posventa.service.CategoryService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Page<GetCategorySimple>> findAll(Pageable pageable){

        Page<GetCategorySimple> categories = categoryService.findAll(pageable);
        return ResponseEntity.ok(categories);
    }

    @GetMapping(value = "/{id}/subcategories")
    public ResponseEntity<Page<GetCategorySimple>> findAllSubcategoriesByCategoryId(
        @PathVariable @Valid Long id, Pageable pageable
    ){
        return ResponseEntity.ok(categoryService.findAllByParentCategoryId(id, pageable));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<GetCategorySimple> findOneById(@PathVariable Long id){
        return ResponseEntity.ok(categoryService.findOneById(id));
    }

    @PostMapping
    public ResponseEntity<GetCategorySimple> createOne(
        @RequestBody @Valid SaveCategory saveDto, HttpServletRequest request
    ){
        GetCategorySimple categoryCreated = categoryService.createOne(saveDto);
        
        String baseUrl = request.getRequestURL().toString();
        URI newLocation = URI.create(baseUrl + "/" + categoryCreated.id());

        return ResponseEntity.created(newLocation).body(categoryCreated);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<GetCategorySimple> updatedOneById(
        @PathVariable Long id, @RequestBody @Valid SaveCategory saveDto
    ){
        GetCategorySimple categoryUpdated = categoryService.updtedOneById(id, saveDto);
        return ResponseEntity.ok(categoryUpdated);    
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteOneById(@PathVariable Long id){
        categoryService.deleteOneById(id);
        return ResponseEntity.noContent().build();
    }

}