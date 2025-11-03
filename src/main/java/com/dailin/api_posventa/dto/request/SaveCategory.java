package com.dailin.api_posventa.dto.request;

import com.dailin.api_posventa.utils.CategoryType;
import java.io.Serializable;

public record SaveCategory(
    String name,
    CategoryType type, // Espera un string como "MENU" o "INVENTARIO"
    boolean available,
    Long parentCategoryId // Si implementas la jerarquía
) implements Serializable {}