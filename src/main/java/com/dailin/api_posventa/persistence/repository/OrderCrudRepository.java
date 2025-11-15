package com.dailin.api_posventa.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.dailin.api_posventa.persistence.entity.Order;

public interface OrderCrudRepository extends JpaRepository<Order, Long>,
    JpaSpecificationExecutor<Order>
{

    Order findByTableId(Long id);
}
