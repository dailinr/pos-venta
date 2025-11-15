package com.dailin.api_posventa.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dailin.api_posventa.dto.request.SaveTable;
import com.dailin.api_posventa.dto.response.GetOrder;
import com.dailin.api_posventa.dto.response.GetTable;
import com.dailin.api_posventa.service.OrderService;
import com.dailin.api_posventa.service.TableService;
import com.dailin.api_posventa.utils.ServiceType;
import com.dailin.api_posventa.utils.TableState;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/tables")
public class TableController {

    @Autowired
    private TableService tableService;

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<Page<GetTable>> findAll(
        @RequestParam(required = false) ServiceType serviceType, 
        @RequestParam(required = false) TableState state,
        Pageable pageable
    ) {
        Page<GetTable> tables = tableService.findAll(serviceType, state, pageable);
        return ResponseEntity.ok(tables);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<GetTable> findOneById(@PathVariable Long id){
        return ResponseEntity.ok(tableService.findOneById(id));
    }

    @GetMapping("/{id}/order-details")
    public ResponseEntity<GetOrder> findOrderByTableId(
        @PathVariable @Valid Long id
    ){
        GetOrder order = orderService.findOrderByTableId(id);
        return ResponseEntity.ok(order);
    }

    @PostMapping
    public ResponseEntity<GetTable> createOne(
        @RequestBody @Valid SaveTable saveDto, HttpServletRequest request
    ){
        GetTable tableCreated = tableService.createOne(saveDto);

        String baseUrl = request.getRequestURL().toString();
        URI newLocation = URI.create(baseUrl + "/" + tableCreated.id());
        
        return ResponseEntity.created(newLocation).body(tableCreated);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<GetTable> updatedOneById(
        @PathVariable Long id, @RequestBody @Valid SaveTable saveDto
    ){
        GetTable tableUpdated = tableService.updatedById(id, saveDto);
        return ResponseEntity.ok(tableUpdated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOneById(@PathVariable Long id){
        tableService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}