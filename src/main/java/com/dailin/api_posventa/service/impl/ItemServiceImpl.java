package com.dailin.api_posventa.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.dailin.api_posventa.dto.response.GetItem;
import com.dailin.api_posventa.service.DishService;
import com.dailin.api_posventa.service.ItemService; 
import com.dailin.api_posventa.service.ProductService;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    @Autowired
    private DishService dishService;
    
    @Autowired
    private ProductService productService;

    @Override
    public Page<GetItem> findAllCombinedItemsByRootCategory(Long rootCategoryId, Pageable pageable) {
        
        // El problema principal al combinar es la paginación a nivel de aplicación.
        // Spring Data JPA no puede paginar dos consultas separadas en una sola.
        // La solución es:
        // 1. Obtener TODOS los resultados (Dishes + Products) sin paginar (Pageable.unpaged()).
        // 2. Combinarlos en una lista.
        // 3. Aplicar la paginación manualmente.
        // NOTA: Esto solo es viable si el número total de ítems no es masivo.
        
        // Obtener Platos (TODOS)
        Page<GetItem> allDishesPage = dishService.findAllByRootCategory(
            rootCategoryId, 
            Pageable.unpaged() // Pedir todos sin paginar
        );
        
        // 2. Obtener Productos (TODOS)
        Page<GetItem> allProductsPage = productService.findAllByRootCategory(
            rootCategoryId, 
            Pageable.unpaged() // Pedir todos sin paginar
        );
        
        // 3. Combinar las listas
        List<GetItem> combinedList = new ArrayList<>();
        combinedList.addAll(allDishesPage.getContent());
        combinedList.addAll(allProductsPage.getContent());
        
        // 4. Aplicar paginación manual
        int totalItems = combinedList.size();
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        List<GetItem> pageContent;
        
        if (totalItems < startItem) {
            pageContent = List.of();
        } else {
            int toIndex = Math.min(startItem + pageSize, totalItems);
            pageContent = combinedList.subList(startItem, toIndex);
        }
        
        return new PageImpl<>(pageContent, pageable, totalItems);
    }
}