package com.dailin.api_posventa.persistence.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.dailin.api_posventa.persistence.entity.DiningTable;
import com.dailin.api_posventa.utils.ServiceType;
import com.dailin.api_posventa.utils.TableState;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class FindAllTableSpecification implements Specification<DiningTable> {

    private ServiceType serviceType;
    private TableState state;

    public FindAllTableSpecification(ServiceType serviceType, TableState state){
        this.serviceType = serviceType;
        this.state = state;
    }

    @Override
    public Predicate toPredicate(Root<DiningTable> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder){

        List<Predicate> predicates = new ArrayList<>();

        // Filtrar por ServiceType (si no es null)
        if(this.serviceType != null) {
            // Usar EQUAL ya que ServiceType es un estado exacto (ENUM)
            Predicate serviceEqual = criteriaBuilder.equal(
                root.get("serviceType"), // nombre del atributo
                this.serviceType
            );
            predicates.add(serviceEqual);
        }
        
        // Filtrar por TableState (si no es null)
        if(this.state != null) {
            // Usar EQUAL ya que TableState es un estado exacto
            Predicate stateEqual = criteriaBuilder.equal(
                root.get("state"), // nombre del atributo
                this.state
            );
            predicates.add(stateEqual);
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

}