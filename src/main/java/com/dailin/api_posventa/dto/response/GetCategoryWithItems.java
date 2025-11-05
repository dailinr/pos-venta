package com.dailin.api_posventa.dto.response;

import com.dailin.api_posventa.utils.CategoryType;
import java.io.Serializable;
import java.util.List;

public record GetCategoryWithItems(
    Long id,
    String name,
    CategoryType type,
    
    // Si la categoría es hija, incluir la información del padre
    CategorySimple parentCategory, 

    // Incluir listas de DTOs simplificados (no la entidad JPA completa)
    List<ItemSimple> products, 
    List<ItemSimple> dishes 
    
) implements Serializable {

    // DTO interno para representar un Plato o Producto de forma simple
    public static record ItemSimple(
        Long id,
        String name,
        double price
    ) implements Serializable {}

    // DTO simple para el padre, si implementas la jerarquía
    public static record CategorySimple(
        Long id,
        String name,
        CategoryType type
    ) implements Serializable {}
}