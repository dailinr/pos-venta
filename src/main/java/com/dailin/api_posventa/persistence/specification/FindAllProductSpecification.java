package com.dailin.api_posventa.persistence.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.dailin.api_posventa.persistence.entity.Category;
import com.dailin.api_posventa.persistence.entity.Product;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class FindAllProductSpecification implements Specification<Product> {

    private Boolean available;
    private String categoryTitle;

    public FindAllProductSpecification(Boolean available, String categoryTitle) {
        this.available = available;
        this.categoryTitle = categoryTitle;
    }

    @Override
    public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        
        List<Predicate> predicates = new ArrayList<>();

        if (this.available != null) {
            Predicate availables = criteriaBuilder
                .equal(root.get("available"), this.available);

            predicates.add(availables);
        }

        if(StringUtils.hasText(this.categoryTitle)){
            
            // Unir a la entidad Category a través del atributo 'category' en la entidad Product.
            Join<Product, Category> categoryJoin = root.join("category");

            Predicate titleLike = criteriaBuilder.like(
                criteriaBuilder.lower(categoryJoin.get("name")),
                "%" + this.categoryTitle.toLowerCase() + "%"
            );

            predicates.add(titleLike);
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
