// src/test/java/com/example/orderservice/datamapperlayer/OrderResponseMapperTest.java
package com.example.orderservice.datamapperlayer;

import com.example.orderservice.datalayer.Order;
import com.example.orderservice.datalayer.OrderIdentifier;
import com.example.orderservice.datalayer.OrderStatus;
import com.example.orderservice.presentationlayer.OrderResponseModel;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderResponseMapperTest {

    private final OrderResponseMapper mapper =
            Mappers.getMapper(OrderResponseMapper.class);

    @Test
    void mapsEntityToResponseAndAddsLinks() {
        var id = new OrderIdentifier("OIDmap");
        var order = Order.builder()
                .orderIdentifier(id)
                .customerId("5")
                .paymentId("7")
                .productIds(List.of("9","8"))
                .createdAt(LocalDateTime.of(2025,2,3,14,0))
                .status(OrderStatus.ON_THE_WAY)
                .amount(123.45)
                .build();

        var res = mapper.entityToOrderResponseModel(order);

        assertAll(
                () -> assertEquals("OIDmap", res.getOrderId()),
                () -> assertEquals("7", res.getPaymentId()),
                () -> assertEquals("5", res.getCustomerId()),
                () -> assertEquals(List.of("9","8"), res.getProductIds()),
                () -> assertEquals(
                        LocalDateTime.of(2025,2,3,14,0),
                        res.getCreatedAt()
                ),
                () -> assertEquals(
                        OrderStatus.ON_THE_WAY,
                        res.getStatus()
                ),
                () -> assertEquals(123.45, res.getAmount())
        );

    }
}
