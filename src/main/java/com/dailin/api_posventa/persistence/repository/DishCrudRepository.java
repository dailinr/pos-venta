package com.dailin.api_posventa.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.dailin.api_posventa.persistence.entity.Dish;

public interface DishCrudRepository extends JpaRepository<Dish, Long>,
    JpaSpecificationExecutor<Dish> { }
