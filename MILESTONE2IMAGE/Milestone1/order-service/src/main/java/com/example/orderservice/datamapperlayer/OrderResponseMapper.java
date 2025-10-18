package com.example.orderservice.datamapperlayer;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.example.orderservice.datalayer.Order;
import com.example.orderservice.presentationlayer.OrderResponseModel;

@Mapper(componentModel = "spring")
public interface OrderResponseMapper {

    @Mapping(target = "orderId",     source = "orderIdentifier.orderId")
    @Mapping(target = "customerId",  source = "customerId")
    @Mapping(target = "paymentId",   source = "paymentId")
    @Mapping(target = "productIds",  source = "productIds")
    @Mapping(target = "createdAt",   source = "createdAt")
    @Mapping(target = "status",      source = "status")
    @Mapping(target = "amount",      source = "amount")
    OrderResponseModel entityToOrderResponseModel(Order order);
}
