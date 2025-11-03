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
import com.dailin.api_posventa.persistence.entity.Product;
import com.dailin.api_posventa.service.ProductService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> findAll() {
        return productService.findAll();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Product> findOneById(@PathVariable Long id) {

        try {
            return ResponseEntity.ok(productService.findOneById(id));
        } catch (ObjectNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Product> createOne(
        @RequestBody Product product, HttpServletRequest request
    ){
        Product productCreated = productService.createOne(product);
        String baseUrl = request.getRequestURL().toString();

        // localizacion para el producto recien creado 
        URI newLocation = URI.create(baseUrl + "/" + productCreated.getId());

        return ResponseEntity.created(newLocation).body(productCreated);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Product> updatedOneById(
        @PathVariable Long id, @RequestBody Product product
    ){

        try {
            Product productUpdated = productService.updtedOneById(id, product);
            return ResponseEntity.ok(productUpdated);
        } 
        catch (ObjectNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteOneById(@PathVariable Long id){

        try {
            productService.deleteOneById(id);
            return ResponseEntity.noContent().build();
        } 
        catch (ObjectNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
