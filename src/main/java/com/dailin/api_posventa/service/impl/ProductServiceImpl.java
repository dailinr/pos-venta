package com.dailin.api_posventa.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dailin.api_posventa.dto.request.SaveProduct;
import com.dailin.api_posventa.dto.response.GetItem;
import com.dailin.api_posventa.dto.response.GetProduct;
import com.dailin.api_posventa.exception.ObjectNotFoundException;
import com.dailin.api_posventa.mapper.ItemMapper;
import com.dailin.api_posventa.mapper.ProductMapper;
import com.dailin.api_posventa.persistence.entity.Category;
import com.dailin.api_posventa.persistence.entity.Product;
import com.dailin.api_posventa.persistence.repository.ProductCrudRepository;
import com.dailin.api_posventa.persistence.specification.FindAllProductSpecification;
import com.dailin.api_posventa.service.CategoryService;
import com.dailin.api_posventa.service.ProductService;
import com.dailin.api_posventa.utils.CategoryType;

@Transactional
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductCrudRepository productCrudRepository;

    @Autowired
    private CategoryService categoryService;

    @Override
    public Product save(Product entity) {
        return productCrudRepository.save(entity);
    }

    @Override
    public GetProduct createOne(SaveProduct saveDto) {
        
        Product newProduct = ProductMapper.toEntity(saveDto); // convierte a entidad
        
        // Validar precio y asignar la entidad Category (usa categoryId del saveDto)
        this.assignCategoryAndValidatePrice(
            newProduct, saveDto.categoryId(), saveDto.price()
        );

        // Determinar la disponibilidad basada en la cantidad ingresada
        this.setAvailability(newProduct);

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
    public Page<GetProduct> findAll(Boolean available, String categoryTitle, CategoryType categoryType, Pageable pageable) {
        
        FindAllProductSpecification productSpecification = new FindAllProductSpecification(available, categoryTitle, categoryType);
        Page<Product> entities = productCrudRepository.findAll(productSpecification, pageable); // obtenemos las entidades
        return entities.map(ProductMapper::toGetDto);
    }

    @Transactional(readOnly = true)
    @Override
    public GetProduct findOneById(Long id) {
        // primero buscamos la entidad. la enviamos a toGeDto para que devuelva un getProduct
        return ProductMapper.toGetDto(this.findOneEntityById(id)); // respuesta 
    }

    @Override
    public Product findOneEntityById(Long id) {
        return productCrudRepository.findById(id) // devuelve un optional<Entity>
            .orElseThrow(() -> new ObjectNotFoundException("[product: " + Long.toString(id) + " ]"));
    }

    @Override
    public GetProduct updtedOneById(Long id, SaveProduct saveDto) {
        
        Product oldProduct = this.findOneEntityById(id); // obtenemos la entidad

        // Actualizar los campos simples de la entidad
        ProductMapper.updateEntity(oldProduct, saveDto); // actualiza los valores de la entidad

        // Revalidar precio y reasignar la entidad Category
        this.assignCategoryAndValidatePrice(
            oldProduct, saveDto.categoryId(), saveDto.price()
        );

        // Redeterminar la disponibilidad con la nueva cantidad
        this.setAvailability(oldProduct);

        // persistir y dar respuesta
        return ProductMapper.toGetDto(productCrudRepository.save(oldProduct)); // getproduct de respuesta
    }

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

    private void setAvailability(Product product) {
        // Si la cantidad es mayor que cero, está disponible (true), si es cero o menos, no (false).
        boolean isAvailable = product.getQuantityAvailable() > 0;
        product.setAvailable(isAvailable);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<GetItem> findAllByRootCategory(Long rootCategoryId, Pageable pageable) {
        // obtener la lista de IDs (raiz + hijas)
        List<Long> categoryIds = categoryService.findRootAndSubcategoriesIds(rootCategoryId);

        if(categoryIds.isEmpty()) {
            return Page.empty(pageable);
        }

        FindAllProductSpecification productSpecification = new FindAllProductSpecification(null, null, null);

        Page<GetProduct> getProducts = productCrudRepository.findAll(productSpecification, pageable) // obtenemos las entidades
            .map(ProductMapper::toGetDto); // despues obtenemos los getProducts
        return getProducts.map(ItemMapper::toGetProductItemDto);
    }

    @Override
    public void decreaseStock(Product product, int quantityRequest) {

        int quantityAvailable = product.getQuantityAvailable();

        // validar si la cantidad disponible es >= a la requerida por el cliente
        if(quantityAvailable < quantityRequest){
            throw new IllegalArgumentException(
                "La cantidad disponible para el producto "+ product.getName()+
                " es menor que la cantidad requerida."
            );
        }
        // disminuir la cantidad disponible en el stock y persistimos
        product.setQuantityAvailable(quantityAvailable - quantityRequest);
        this.save(product);
    }
}