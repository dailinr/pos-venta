package com.dailin.api_posventa.persistence.entity;

import java.util.List;

import com.dailin.api_posventa.utils.CategoryType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Category {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CategoryType type;

    @Column(nullable = false)
    private boolean available;

    // Una Categoría clasifica a muchos Productos
    @OneToMany(mappedBy = "category", fetch = FetchType.EAGER) // 'category' es el nombre del atributo en la clase Product
    private List<Product> products;

    // Una Categoría clasifica a muchos Platos
    @OneToMany(mappedBy = "category", fetch = FetchType.EAGER) // 'category' es el nombre del atributo en la clase Dish
    private List<Dish> dishes;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CategoryType getType() {
        return type;
    }

    public boolean isAvailable() {
        return available;
    }

    public List<Product> getProducts() {
        return products;
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(CategoryType type) {
        this.type = type;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }

}