package com.marketplace.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "stores")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Store {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @OneToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<Product> products;
    
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<Order> orders;
} 