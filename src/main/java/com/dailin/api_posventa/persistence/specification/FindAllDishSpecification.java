package com.dailin.api_posventa.persistence.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.dailin.api_posventa.persistence.entity.Category;
import com.dailin.api_posventa.persistence.entity.Dish;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class FindAllDishSpecification implements Specification<Dish> {

    private Boolean available;
    private String categoryTitle;

    public FindAllDishSpecification(Boolean available, String categoryTitle) {
        this.available = available;
        this.categoryTitle = categoryTitle;
    }

    @Override
    public Predicate toPredicate(Root<Dish> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        
        List<Predicate> predicates = new ArrayList<>();
        Join<Dish, Category> categoryJoin = root.join("category"); 

        if(this.available != null){
            Predicate availables = criteriaBuilder
                .equal(root.get("available"), this.available);

            predicates.add(availables);
        }

        // 2. Si se proporciona categoryTitle y NO se usó la lista de IDs, aplicar el filtro de título
        if(StringUtils.hasText(this.categoryTitle)){
        
            Predicate titleLike = criteriaBuilder.like(
                criteriaBuilder.lower(categoryJoin.get("name")),
                "%" + this.categoryTitle.toLowerCase() + "%"
            );

            predicates.add(titleLike);
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    
} 