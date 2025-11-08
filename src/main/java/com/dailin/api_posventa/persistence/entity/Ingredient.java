package com.dailin.api_posventa.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    uniqueConstraints = @UniqueConstraint(
        name = "UQ_DISH_PRODUCT", // Nombre de la restricción en la DB
        columnNames = { "dish_id", "product_id" } // La combinación de plato y producto debe ser única
    )
)
public class Ingredient {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "dish_id", nullable = false)
    private Dish dish;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public Long getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public Dish getDish() {
        return dish;
    }

    public Product getProduct() {
        return product;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

}