package com.dailin.api_posventa.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.dailin.api_posventa.dto.response.GetItem;
import com.dailin.api_posventa.dto.response.ItemProjection;
import com.dailin.api_posventa.mapper.ItemMapper;
import com.dailin.api_posventa.persistence.repository.ItemCrudRepository;
import com.dailin.api_posventa.service.CategoryService;
import com.dailin.api_posventa.service.ItemService; 
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemCrudRepository itemCrudRepository;

    @Autowired
    private CategoryService categoryService;

    @Override
    public Page<GetItem> findAllCombinedItemsByRootCategory(Long rootCategoryId, Pageable pageable) {
        
        // Obtener la lista de IDs (raíz + hijas)
        List<Long> categoryIds = categoryService.findRootAndSubcategoriesIds(rootCategoryId);
        
        if (categoryIds.isEmpty()) {
            return Page.empty(pageable);
        }

        // Ejecutar la consulta nativa con paginación delegada a la DB y obtener la página de Proyeccion
        Page<ItemProjection> nativeResults = itemCrudRepository.findAllCombinedItemsByRootCategoryNative(
            categoryIds, 
            pageable
        );

        // Mapear de ItemProjection a GetItem
        return nativeResults.map(ItemMapper::toGetItemDto);
    }
}