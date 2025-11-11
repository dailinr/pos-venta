package com.dailin.api_posventa.persistence.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dailin.api_posventa.dto.response.ItemProjection;
import com.dailin.api_posventa.persistence.entity.Dish;

// puede ser cualquier entidad (Dish o Product)
@Repository
public interface ItemCrudRepository extends JpaRepository<Dish, Long> {
    
    String COMBINED_ITEMS_QUERY_BASE = """
        (
            SELECT 
                d.id as id, d.name as name, d.price as price, 
                d.available as available, d.description as description, 'DISH' as item_type, 
                d.category_id as category_id, c.name as category_title, c.type as category_type
            FROM dish d
            JOIN category c ON d.category_id = c.id
        )
        UNION ALL
        (
            SELECT 
                p.id as id, p.name as name, p.price as price, 
                p.available as available, p.description as description, 'PRODUCT' as item_type, 
                p.category_id as category_id, c.name as category_title, c.type as category_type 
            FROM product p
            JOIN category c ON p.category_id = c.id
        )
    """;

    // El método ahora acepta una clausula WHERE dinamica como String
    @Query(
        value = "SELECT * FROM (" + COMBINED_ITEMS_QUERY_BASE + ") AS combined_items WHERE category_id IN (:categoryIds) AND 1=1 " + 
                "AND (:available IS NULL OR available = :available) " +
                "AND (:categoryTitle IS NULL OR LOWER(category_title) LIKE :categoryTitle) " +
                "AND (:categoryType IS NULL OR LOWER(category_type) LIKE :categoryType) ",
        
        countQuery = "SELECT COUNT(*) FROM (" + COMBINED_ITEMS_QUERY_BASE + ") AS combined_items WHERE category_id IN (:categoryIds) AND 1=1 " + 
                     "AND (:available IS NULL OR available = :available) " +
                     "AND (:categoryTitle IS NULL OR LOWER(category_title) LIKE :categoryTitle) " +
                     "AND (:categoryType IS NULL OR LOWER(category_type) LIKE :categoryType) ",

        nativeQuery = true
    )
    
    Page<ItemProjection> findAllCombinedItemsByRootCategoryNative(
        @Param("categoryIds") List<Long> categoryIds, 
        @Param("available") Boolean available,
        @Param("categoryTitle") String categoryTitle,
        @Param("categoryType") String categoryType,
        Pageable pageable
    );
}
