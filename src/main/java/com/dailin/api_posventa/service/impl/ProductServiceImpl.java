package com.dailin.api_posventa.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dailin.api_posventa.dto.request.SaveProduct;
import com.dailin.api_posventa.dto.response.GetProduct;
import com.dailin.api_posventa.exception.ObjectNotFoundException;
import com.dailin.api_posventa.mapper.ProductMapper;
import com.dailin.api_posventa.persistence.entity.Category;
import com.dailin.api_posventa.persistence.entity.Product;
import com.dailin.api_posventa.persistence.repository.ProductCrudRepository;
import com.dailin.api_posventa.persistence.specification.FindAllProductSpecification;
import com.dailin.api_posventa.service.CategoryService;
import com.dailin.api_posventa.service.ProductService;

@Transactional
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductCrudRepository productCrudRepository;

    @Autowired
    private CategoryService categoryService;

    /**
     * 🆕 Valida la regla de precio de la categoría y asigna la entidad Category al Producto.
     * Esto es crucial para la integridad de la FK y la regla de negocio.
     */
    private void assignCategoryAndValidatePrice(Product product, Long categoryId, Double requestedPrice) {
        
        if (categoryId == null) {
            // El producto debe pertenecer a una categoría (Restricción NOT NULL en FK)
            throw new IllegalArgumentException("El producto debe tener una categoría asignada.");
        }
        
        // Obtener la entidad Category
        Category category = categoryService.finOneEntityById(categoryId);
        
        // Si la categoría NO permite precio (priceEnabled=false):
        if (!category.isPriceEnabled() && requestedPrice != null && requestedPrice > 0) { 
            throw new IllegalArgumentException(
                "La categoría '" + category.getName() + "' no permite precio de venta. El precio debe ser 0."
            );
        }

        // Si requestedPrice es null, lo forzamos a 0.0 antes de asignarlo a la entidad.
        if (requestedPrice == null) requestedPrice = 0.0;
        
        product.setPrice(requestedPrice); // Asigna el valor (ya corregido a 0 si era necesario)
        product.setCategory(category); // Asignar la entidad Category al Producto 
    }

    @Override
    public GetProduct createOne(SaveProduct saveDto) {
        if (saveDto.categoryId() == null) {
            // Esto debería capturar si el DTO no deserializa el campo correctamente
            throw new IllegalArgumentException("categoryId en DTO es NULL.");
        }
        
        
        Product newProduct = ProductMapper.toEntity(saveDto); // convierte a entidad
        
        // Validar precio y asignar la entidad Category (usa categoryId del saveDto)
        this.assignCategoryAndValidatePrice(
            newProduct, saveDto.categoryId(), saveDto.price()
        );

        // Persistir y responder
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
    public List<GetProduct> findAll(Boolean available) {
        
        FindAllProductSpecification productSpecification = new FindAllProductSpecification(available);
        List<Product> entities = productCrudRepository.findAll(productSpecification); // obtenemos las entidades
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

        // Actualizar los campos simples de la entidad
        ProductMapper.updateEntity(oldProduct, saveDto); // actualiza los valores de la entidad

        // 2. 🆕 Revalidar precio y reasignar la entidad Category
        this.assignCategoryAndValidatePrice(
            oldProduct, saveDto.categoryId(), saveDto.price()
        );

        // persistir y dar respuesta
        return ProductMapper.toGetDto(productCrudRepository.save(oldProduct)); // getproduct de respuesta
    }
}