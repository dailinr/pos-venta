package com.dailin.api_posventa.persistence.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.dailin.api_posventa.persistence.entity.Dish;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class FindAllDishSpecification implements Specification<Dish> {

    private Boolean available;

    public FindAllDishSpecification(Boolean available) {
        this.available = available;
    }

    @Override
    public Predicate toPredicate(Root<Dish> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        
        List<Predicate> predicates = new ArrayList<>();

        if(this.available != null){
            Predicate availables = criteriaBuilder
                .equal(root.get("available"), this.available);

            predicates.add(availables);
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    
} 