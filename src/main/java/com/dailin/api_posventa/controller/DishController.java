package com.dailin.api_posventa.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dailin.api_posventa.persistence.entity.Dish;
import com.dailin.api_posventa.service.DishService;

@RestController
@RequestMapping("/dishes")
public class DishController {

    @Autowired
    private DishService dishService;

    @GetMapping
    public List<Dish> findAll() {
        System.out.println("----- Entrando al metodo findAll de DishController. -----");
        return dishService.findAll();
    }
}
