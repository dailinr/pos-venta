package com.dailin.api_posventa.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dailin.api_posventa.exception.ObjectNotFoundException;
import com.dailin.api_posventa.persistence.entity.Product;
import com.dailin.api_posventa.persistence.repository.ProductCrudRepository;
import com.dailin.api_posventa.service.ProductService;

@Transactional
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductCrudRepository productCrudRepository;

    @Override
    public Product createOne(Product product) {
        return productCrudRepository.save(product);
    }

    @Override
    public void deleteOneById(Long id) {
        
        if(productCrudRepository.existsById(id)){
            productCrudRepository.deleteById(id);
            return;
        }

        throw new ObjectNotFoundException("[product: " +Long.toString(id)+ "]");
    }

    @Transactional(readOnly = true)
    @Override
    public List<Product> findAll() {
        return productCrudRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Product findOneById(Long id) {
        return productCrudRepository.findById(id)
            .orElseThrow(() -> new ObjectNotFoundException("[product: " +Long.toString(id)+ "]"));
    }

    @Override
    public Product updtedOneById(Long id, Product product) {
        
        Product oldProduct = this.findOneById(id);

        oldProduct.setAvailable(product.isAvailable());
        oldProduct.setDescription(product.getDescription());
        oldProduct.setMeasureUnit(product.getMeasureUnit());
        oldProduct.setName(product.getName());
        oldProduct.setPrice(product.getPrice());
        oldProduct.setQuantityAvailable(product.getQuantityAvailable());

        return productCrudRepository.save(oldProduct);
    }
}