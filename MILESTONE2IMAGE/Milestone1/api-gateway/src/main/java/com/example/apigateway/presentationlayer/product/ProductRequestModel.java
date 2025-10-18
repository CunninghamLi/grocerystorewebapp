// src/main/java/com/example/apigateway/presentationlayer/product/ProductRequestModel.java
package com.example.apigateway.presentationlayer.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestModel {
    private String name;
    private String description;
    private Double price;
    private Integer stockQuantity;
}
