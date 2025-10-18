// src/main/java/com/example/apigateway/presentationlayer/product/ProductResponseModel.java
package com.example.apigateway.presentationlayer.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseModel {
    private Long id;
    private String productId;
    private String name;
    private String description;
    private Double price;
    private Integer stockQuantity;
}
