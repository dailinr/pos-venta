package com.dailin.api_posventa.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.dailin.api_posventa.persistence.entity.Product;

public interface ProductCrudRepository extends JpaRepository<Product, Long>, 
    JpaSpecificationExecutor<Product>{

    // hasta el momento creo que ya todos los metodos los trae CrudRepository por defecto

}