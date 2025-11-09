package com.dailin.api_posventa.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dailin.api_posventa.persistence.entity.RecipeItem;

public interface RecipeItemCrudRepository extends JpaRepository<RecipeItem, Long> {

    // listar todos los ingredientes de un plato segun su id
    Page<RecipeItem> findByDishId(Long id, Pageable pageable);
}
