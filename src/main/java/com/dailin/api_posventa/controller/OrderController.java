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

import com.dailin.api_posventa.dto.request.SaveOrder;
import com.dailin.api_posventa.dto.response.GetOrder;
import com.dailin.api_posventa.service.OrderService;
import com.dailin.api_posventa.utils.OrderState;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<Page<GetOrder>> findAll(
        @RequestParam(required = false) OrderState state,
        @RequestParam(required = false) String date,
        Pageable pageable
    ) {
        Page<GetOrder> orders = orderService.findAll(state, date, pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetOrder> findOneById(@PathVariable Long id){
        GetOrder order = orderService.findOneById(id);
        return ResponseEntity.ok(order);
    }

    @PostMapping
    public ResponseEntity<GetOrder> createOne(
        @RequestBody @Valid SaveOrder saveDto,
        HttpServletRequest request
    ){
        GetOrder orderCreated = orderService.createOne(saveDto);

        String baseUrl = request.getRequestURL().toString();
        URI newLocation = URI.create(baseUrl + "/" + orderCreated.id());

        return ResponseEntity.created(newLocation).body(orderCreated);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetOrder> updatedOneById(
        @PathVariable Long id,
        @RequestBody @Valid SaveOrder saveDto
    ){
        GetOrder orderUpdated = orderService.updatedOneById(saveDto, id);
        return ResponseEntity.ok(orderUpdated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOneById(@PathVariable Long id){
        orderService.deleteOneById(id);
        return ResponseEntity.noContent().build();
    }
}
