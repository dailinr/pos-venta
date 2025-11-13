package com.dailin.api_posventa.persistence.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.dailin.api_posventa.utils.OrderState;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity 
@Table(name = "customer_order")
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING) 
    private OrderState state;

    @CreationTimestamp // se auto-genera cuando ingresamos una nueva pelicula
    @Column(updatable = false, columnDefinition = "TIMESTAMP DEFAULT NOW()") // no se puede modificar
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private double total;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
    private List<OrderItem> OrderItems;

    @OneToOne
    @JoinColumn(name = "table_id", nullable = false)
    private DiningTable table;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state) {
        this.state = state;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<OrderItem> getOrderItems() {
        return OrderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        OrderItems = orderItems;
    }

    public DiningTable getTable() {
        return table;
    }

    public void setTable(DiningTable table) {
        this.table = table;
    }
    

}