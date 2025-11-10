package com.dailin.api_posventa.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dailin.api_posventa.persistence.entity.Category;
public interface CategoryCrudRepository extends JpaRepository<Category, Long> {

    Page<Category> findByParentCategoryId(Long id, Pageable pageable);
}
