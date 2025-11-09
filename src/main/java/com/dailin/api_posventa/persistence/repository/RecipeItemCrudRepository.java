package com.dailin.api_posventa.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dailin.api_posventa.persistence.entity.RecipeItem;

public interface RecipeItemCrudRepository extends JpaRepository<RecipeItem, Long> {

}
