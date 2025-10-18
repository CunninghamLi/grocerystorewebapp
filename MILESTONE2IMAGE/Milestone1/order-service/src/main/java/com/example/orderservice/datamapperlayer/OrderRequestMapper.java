package com.example.orderservice.datamapperlayer;

import com.example.orderservice.datalayer.Order;
import com.example.orderservice.datalayer.Order;
import com.example.orderservice.datalayer.OrderIdentifier;
import com.example.orderservice.presentationlayer.OrderRequestModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
// @Mapper(componentModel = "spring", uses = {OrderIdentifierMapper.class}) // Example if you had a separate mapper
@Mapper(componentModel = "spring")
public interface OrderRequestMapper {
    @Mapping(target = "id", ignore = true) // MongoDB will generate one if null
    @Mapping(target = "orderIdentifier", source = "orderIdentifier")
    // If OrderRequestModel.productIds is now List<String> and Order.productIds is List<String>,
    // this mapping should be fine by default.
    @Mapping(target = "productIds", source = "orderRequestModel.productIds")
    Order requestModelToEntity(OrderRequestModel orderRequestModel, OrderIdentifier orderIdentifier);
}