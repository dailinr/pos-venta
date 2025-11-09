package com.dailin.api_posventa.persistence.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    uniqueConstraints = @UniqueConstraint(
        name = "UQ_DISH_ALL_FIELDS", // Nombre de la restricción en la DB
        columnNames = { "name", "category_id" }
    )
)
public class Dish {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean available = true;

    @Column(nullable = false)
    private double price;

    private String description;

    @Column(nullable = false)
    private String name;

    // @Column(name = "category_id")
    // private Long categoryId;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false) // Mapea a la columna FK 'category_id' en la tabla 'Dish'
    private Category category; 

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "dish")
    private List<RecipeItem> RecipeItems;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public boolean isAvailable() {
        return available;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }
}