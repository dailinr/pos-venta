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
import com.dailin.api_posventa.dto.response.GetItem;
import com.dailin.api_posventa.service.CategoryService;
import com.dailin.api_posventa.service.DishService;
import com.dailin.api_posventa.service.ItemService;
import com.dailin.api_posventa.service.ProductService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private DishService dishService;

    @Autowired 
    private ItemService itemService;

    @GetMapping
    public ResponseEntity<Page<GetCategorySimple>> findAll(Pageable pageable){

        Page<GetCategorySimple> categories = categoryService.findAll(pageable);
        return ResponseEntity.ok(categories);
    }

    // listar las categorias raiz
    @GetMapping(value = "/root")
    public ResponseEntity<Page<GetCategorySimple>> findAllRootCategories(Pageable pageable) {
        Page<GetCategorySimple> categories = categoryService.findAllRootCategories(pageable);
        return ResponseEntity.ok(categories);
    }

    // obtener platos de la categoria raiz y subcategorias
    @GetMapping(value = "/{id}/dishes")
    public ResponseEntity<Page<GetItem>> findAllDishesByRootCategory(
        @PathVariable @Valid Long id, Pageable pageable
    ){
        Page<GetItem> dishes = dishService.findAllByRootCategory(id, pageable);
        return ResponseEntity.ok(dishes);
    }

    @GetMapping(value = "/{id}/products")
    public ResponseEntity<Page<GetItem>> findAllProductsByRootCategory(
        @PathVariable @Valid Long id, Pageable pageable
    ){
        Page<GetItem> products = productService.findAllByRootCategory(id, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping(value = "/{id}/items")
    public ResponseEntity<Page<GetItem>> findAllCombinedItemsByRootCategory(
        @PathVariable @Valid Long id, Pageable pageable
    ){
        Page<GetItem> combinedItems = itemService.findAllCombinedItemsByRootCategory(id, pageable);
        return ResponseEntity.ok(combinedItems);
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