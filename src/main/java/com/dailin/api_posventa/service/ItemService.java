package com.dailin.api_posventa.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dailin.api_posventa.dto.response.GetItem;

public interface ItemService {
    Page<GetItem> findAllCombinedItemsByRootCategory(Long rootCategoryId, Pageable pageable);
}