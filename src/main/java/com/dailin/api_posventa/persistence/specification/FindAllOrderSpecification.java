package com.dailin.api_posventa.persistence.specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.dailin.api_posventa.persistence.entity.Order;
import com.dailin.api_posventa.utils.OrderState;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class FindAllOrderSpecification implements Specification<Order> {
    // definimos el formato de fecha valido
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy"); 
    
    private OrderState state;
    private LocalDate date;

    public FindAllOrderSpecification(OrderState state, String dateString){
        this.state = state;

        // Convertir el String "dd/mm/aaaa" a LocalDate si no es nulo/vacio
        if(dateString != null && !dateString.trim().isEmpty()){
            try {
                this.date = LocalDate.parse(dateString, DATE_FORMATTER);
            } catch (Exception e) {
                // Manejar error de formato, o ignorar el filtro de fecha
                this.date = null;
            }
        }
    }

    @Override
    public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder){

        List<Predicate> predicates = new ArrayList<>();

        if(this.state != null){

            Predicate stateEqual = criteriaBuilder.equal(
                root.get("state"),  this.state
            );
            predicates.add(stateEqual);
        }

        // Filtrar por FECHA (si se proporcionó)
        if (this.date != null) {
            
            // Definir el inicio del día (00:00:00) y el fin del día (23:59:59.999...)
            LocalDateTime startOfDay = this.date.atStartOfDay();
            // El fin del día es el inicio del día siguiente
            LocalDateTime endOfNextDay = this.date.plusDays(1).atStartOfDay();

            // Crear la restricción: createdAt >= startOfDay AND createdAt < endOfNextDay
            Predicate dateRange = criteriaBuilder.between(
                root.get("createdAt"), // El atributo de tipo LocalDateTime
                startOfDay,
                endOfNextDay
            );
            
            predicates.add(dateRange);
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

}
