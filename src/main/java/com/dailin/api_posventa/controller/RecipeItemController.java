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

import com.dailin.api_posventa.dto.request.SaveRecipeItem;
import com.dailin.api_posventa.dto.response.GetRecipeItemComplete;
import com.dailin.api_posventa.service.RecipeItemService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid; // Asegúrate de tener esta importación para validación

@RestController
@RequestMapping("/recipe-items")
public class RecipeItemController {

    @Autowired
    private RecipeItemService recipeItemService;

    /**
     * POST /api/v1/recipe-items
     * Crea una nueva línea de receta (añade un producto con cantidad a un plato).
     */
    @PostMapping
    public ResponseEntity<GetRecipeItemComplete> createOne(
        @Valid @RequestBody SaveRecipeItem saveDto, HttpServletRequest request
    ) {
        GetRecipeItemComplete createdRecipeItem = recipeItemService.createOne(saveDto);

        String baseUrl = request.getRequestURL().toString();
        URI newLocation = URI.create(baseUrl + "/" + createdRecipeItem.id());

        return ResponseEntity.created(newLocation).body(createdRecipeItem);
    }

    /**
     * GET /api/v1/recipe-items
     * Obtiene todas las líneas de recetas registradas.
     */
    @GetMapping
    public ResponseEntity<Page<GetRecipeItemComplete>> findAll(Pageable pageable) {
        Page<GetRecipeItemComplete> recipeItems = recipeItemService.findAll(pageable);
        return ResponseEntity.ok(recipeItems);
    }

    /**
     * GET /api/v1/recipe-items/{id}
     * Obtiene una línea de receta específica por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<GetRecipeItemComplete> findOneById(@PathVariable Long id) {
        GetRecipeItemComplete recipeItem = recipeItemService.findOneById(id);
        return ResponseEntity.ok(recipeItem);
    }

    /**
     * PUT /api/v1/recipe-items/{id}
     * Actualiza la cantidad de un ingrediente en una línea de receta existente.
     * Nota: Típicamente no se permite cambiar el dishId o productId en un PUT, solo la cantidad.
     */
    @PutMapping("/{id}")
    public ResponseEntity<GetRecipeItemComplete> updatedOneById(
        @PathVariable Long id, @Valid @RequestBody SaveRecipeItem saveDto
    ) {
        GetRecipeItemComplete updatedRecipeItem = recipeItemService.updatedOneById(id, saveDto);
        return ResponseEntity.ok(updatedRecipeItem);
    }

    /**
     * DELETE /api/v1/recipe-items/{id}
     * Elimina una línea de receta.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOneById(@PathVariable Long id) {
        recipeItemService.deleteOneById(id);
        return ResponseEntity.noContent().build(); // 204 No Content para éxito sin cuerpo
    }
}