package com.example.orderservice.businesslayer;


import com.example.orderservice.presentationlayer.OrderRequestModel;
import com.example.orderservice.presentationlayer.OrderResponseModel;

import java.util.List;

public interface OrderService {

    List<OrderResponseModel> getOrders();
    OrderResponseModel getOrderById(String orderId);
    OrderResponseModel addOrder(OrderRequestModel orderRequestModel);
    OrderResponseModel updateOrder(OrderRequestModel updatedOrder, String orderId);
    void deleteOrder(String orderId);

}