package com.dailin.api_posventa.persistence.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dailin.api_posventa.persistence.entity.Category;
import com.dailin.api_posventa.utils.CategoryType;
public interface CategoryCrudRepository extends JpaRepository<Category, Long> {

    Page<Category> findByParentCategoryId(Long id, Pageable pageable);
    
    Page<Category> findByParentCategoryIdIsNull(Pageable pageable);

    // Si pudieras añadir esto a tu repositorio, sería lo MÁS eficiente:
    @Query("SELECT c.id FROM Category c WHERE c.parentCategory.id = :parentId")
    List<Long> findIdsByParentCategoryId(Long parentId);

    boolean existsByNameAndType(String name, CategoryType type);
}
