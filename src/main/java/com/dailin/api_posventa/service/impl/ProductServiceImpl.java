package com.dailin.api_posventa.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dailin.api_posventa.dto.request.SaveProduct;
import com.dailin.api_posventa.dto.response.GetProduct;
import com.dailin.api_posventa.exception.ObjectNotFoundException;
import com.dailin.api_posventa.mapper.ProductMapper;
import com.dailin.api_posventa.persistence.entity.Product;
import com.dailin.api_posventa.persistence.repository.ProductCrudRepository;
import com.dailin.api_posventa.service.ProductService;

@Transactional
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductCrudRepository productCrudRepository;

    @Override
    public GetProduct createOne(SaveProduct saveDto) {
        Product newProduct = ProductMapper.toEntity(saveDto); // convierte a entidad
        Product saveProduct = productCrudRepository.save(newProduct);

        return ProductMapper.toGetDto(saveProduct); // respuesta
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
    public List<GetProduct> findAll() {
        List<Product> entities = productCrudRepository.findAll(); // obtenemos las entidades
        return ProductMapper.toGetDtoList(entities);
    }

    @Transactional(readOnly = true)
    @Override
    public GetProduct findOneById(Long id) {
        // primero buscamos la entidad. la enviamos a toGeDto para que devuelva un getProduct
        return ProductMapper.toGetDto(this.findOneEntityById(id)); // respuesta 
    }

    private Product findOneEntityById(Long id) {
        return productCrudRepository.findById(id) // devuelve un optional<Entity>
            .orElseThrow(() -> new ObjectNotFoundException("[product: " + Long.toString(id) + " ]"));
    }

    @Override
    public GetProduct updtedOneById(Long id, SaveProduct saveDto) {
        
        Product oldProduct = this.findOneEntityById(id); // obtenemos la entidad

        ProductMapper.updateEntity(oldProduct, saveDto); // actualiza los valores de la entidad

        return ProductMapper.toGetDto(productCrudRepository.save(oldProduct)); // getproduct de respuesta
    }
}