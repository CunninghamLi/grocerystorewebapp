package com.example.productservice.datamapperlayer;

import com.example.productservice.datalayer.Product;
import com.example.productservice.presentationlayer.ProductResponseModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductResponseMapper {

    @Mapping(source = "productId", target = "productId")  // ← explicitly map
    @Mapping(source = "id",        target = "id")
    ProductResponseModel entityToProductResponseModel(Product product);

    List<ProductResponseModel> entityListToProductResponseModelList(List<Product> products);
}
