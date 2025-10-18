package com.example.productservice.businesslayer;

import com.example.productservice.presentationlayer.ProductRequestModel;
import com.example.productservice.presentationlayer.ProductResponseModel;

import java.util.List;

public interface ProductService {

    List<ProductResponseModel> getProducts();
    ProductResponseModel getProductByProductId(String productId);
    ProductResponseModel addProduct(ProductRequestModel productRequestModel);
    ProductResponseModel updateProduct(ProductRequestModel updatedProduct, String productId);
    void removeProduct(String productId);
}
