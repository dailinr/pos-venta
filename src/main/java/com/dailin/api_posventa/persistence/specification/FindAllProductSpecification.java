package com.dailin.api_posventa.persistence.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.dailin.api_posventa.persistence.entity.Category;
import com.dailin.api_posventa.persistence.entity.Product;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class FindAllProductSpecification implements Specification<Product> {

    private Boolean available;
    private String categoryTitle;
    private String categoryType;
    private List<Long> categoryIds;

    public FindAllProductSpecification(Boolean available, String categoryTitle, String categoryType, List<Long> categoryIds) {
        this.available = available;
        this.categoryTitle = categoryTitle;
        this.categoryType = categoryType;
        this.categoryIds = categoryIds;
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

        // filtrar por la lista de IDs de categoria
        if(this.categoryIds != null && !this.categoryIds.isEmpty()) {
            // crea una clausula 'IN' para la lista de IDs
            In<Long> inClause = criteriaBuilder.in(categoryJoin.get("id"));
            this.categoryIds.forEach(inClause::value);
            predicates.add(inClause);
        }

        // Filtrar productos por su categoria
        if(StringUtils.hasText(this.categoryTitle) && (this.categoryIds == null || this.categoryIds.isEmpty())){
          
            Predicate titleLike = criteriaBuilder.like(
                criteriaBuilder.lower(categoryJoin.get("name")),
                "%" + this.categoryTitle.toLowerCase() + "%"
            );

            predicates.add(titleLike);
        }

        // Filtrar los productos según el type de su categoria 
        if(StringUtils.hasText(categoryType)){

            Predicate typeLike = criteriaBuilder.like(
                criteriaBuilder.lower(categoryJoin.get("type")), 
                "%" + this.categoryType.toLowerCase() + "%"
            );

            predicates.add(typeLike);
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
