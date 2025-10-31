package com.dailin.api_posventa.persistence.entity;

import com.dailin.api_posventa.utils.MeasureUnit;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Product {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private double price;

    @Enumerated(EnumType.STRING) 
    private MeasureUnit measureUnit;

    private String description;

    private int quantityAvailable;

    private boolean available;

    private int categoryId;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public MeasureUnit getMeasureUnit() {
        return measureUnit;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantityAvailable() {
        return quantityAvailable;
    }

    public boolean isAvailable() {
        return available;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setMeasureUnit(MeasureUnit measureUnit) {
        this.measureUnit = measureUnit;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setQuantityAvailable(int quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    
}