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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.dailin.api_posventa.dto.request.SaveProduct;
import com.dailin.api_posventa.dto.response.GetProduct;
import com.dailin.api_posventa.service.ProductService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<GetProduct>> findAll(
        @RequestParam(required = false) Boolean available
    ) {
        return ResponseEntity.ok(productService.findAll(available));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<GetProduct> findOneById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findOneById(id));
    }

    @PostMapping
    public ResponseEntity<GetProduct> createOne(
        @RequestBody @Valid SaveProduct saveDto, HttpServletRequest request
    ){
        GetProduct productCreated = productService.createOne(saveDto);
        String baseUrl = request.getRequestURL().toString();

        // localizacion para el producto recien creado 
        URI newLocation = URI.create(baseUrl + "/" + productCreated.id());

        return ResponseEntity.created(newLocation).body(productCreated);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<GetProduct> updatedOneById(
        @PathVariable Long id, @RequestBody @Valid SaveProduct saveDto
    ){
        GetProduct productUpdated = productService.updtedOneById(id, saveDto);
        return ResponseEntity.ok(productUpdated);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteOneById(@PathVariable Long id){

        productService.deleteOneById(id);
        return ResponseEntity.noContent().build();
    }
}
