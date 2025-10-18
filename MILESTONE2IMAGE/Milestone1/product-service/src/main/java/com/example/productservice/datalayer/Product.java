package com.example.productservice.datalayer;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productId; // This replaces ProductIdentifier

    private String name;
    private String description;
    private Double price;
    private Integer stockQuantity;
}
