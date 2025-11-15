package com.dailin.api_posventa.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.dailin.api_posventa.persistence.entity.DiningTable;

public interface TableCrudRepository extends JpaRepository<DiningTable, Long>,
    JpaSpecificationExecutor<DiningTable> { }