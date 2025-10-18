package com.example.orderservice.presentationlayer.product;

public class ProductResponseModel {
    private Long id;
    private String productId;         // ← new field
    private String name;
    private String description;
    private Double price;
    private Integer stockQuantity;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getProductId() {    // ← getter
        return productId;
    }
    public void setProductId(String productId) {  // ← setter
        this.productId = productId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }
    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}
