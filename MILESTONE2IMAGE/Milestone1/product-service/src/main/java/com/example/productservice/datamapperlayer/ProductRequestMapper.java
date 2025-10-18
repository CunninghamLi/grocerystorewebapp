package com.example.productservice.datamapperlayer;

import com.example.productservice.datalayer.Product;
import com.example.productservice.presentationlayer.ProductRequestModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductRequestMapper {

    @Mapping(target = "id", ignore = true)
    Product requestModelToEntity(ProductRequestModel requestModel);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequestModel(ProductRequestModel requestModel, @MappingTarget Product product);
}
