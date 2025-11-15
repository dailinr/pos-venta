package com.dailin.api_posventa.persistence.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.dailin.api_posventa.persistence.entity.Category;
import com.dailin.api_posventa.persistence.entity.Product;
import com.dailin.api_posventa.utils.CategoryType;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class FindAllProductSpecification implements Specification<Product> {

    private Boolean available;
    private String categoryTitle;
    private CategoryType categoryType;

    public FindAllProductSpecification(Boolean available, String categoryTitle, CategoryType categoryType) {
        this.available = available;
        this.categoryTitle = categoryTitle;
        this.categoryType = categoryType;
    }

    @Override
    public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        
        List<Predicate> predicates = new ArrayList<>();
        Join<Product, Category> categoryJoin = root.join("category");

        // Filtrar productos por su disponibilidad
        if (this.available != null) {
            Predicate availables = criteriaBuilder
                .equal(root.get("available"), this.available);

            predicates.add(availables);
        }

        // Filtrar productos por su categoria
        if(StringUtils.hasText(this.categoryTitle)){
          
            Predicate titleLike = criteriaBuilder.like(
                criteriaBuilder.lower(categoryJoin.get("name")),
                "%" + this.categoryTitle.toLowerCase() + "%"
            );

            predicates.add(titleLike);
        }

        // Filtrar los productos según el type de su categoria 
        if(this.categoryType != null){

            Predicate typeEqual = criteriaBuilder.equal(
                categoryJoin.get("type"), 
                this.categoryType
            );

            predicates.add(typeEqual);
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
