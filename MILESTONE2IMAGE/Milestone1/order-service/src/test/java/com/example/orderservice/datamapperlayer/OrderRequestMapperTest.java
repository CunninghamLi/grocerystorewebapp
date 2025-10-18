// src/test/java/com/example/orderservice/datamapperlayer/OrderRequestMapperTest.java
package com.example.orderservice.datamapperlayer;

import com.example.orderservice.datalayer.Order;
import com.example.orderservice.datalayer.OrderIdentifier;
import com.example.orderservice.datalayer.OrderStatus;
import com.example.orderservice.presentationlayer.OrderRequestModel;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class OrderRequestMapperTest {

    private final OrderRequestMapper mapper =
            Mappers.getMapper(OrderRequestMapper.class);

    @Test
    void mapsRequestToEntity() {
        var id = new OrderIdentifier("OIDtest");
        var model = OrderRequestModel.builder()
                .paymentId("30")
                .customerId("15")
                .productIds(Collections.emptyList())
                .status(OrderStatus.RECEIVED)
                .amount(25.0)
                .build();

        Order ent = mapper.requestModelToEntity(model, id);

        assertAll(
                () -> assertEquals(id, ent.getOrderIdentifier()),
                () -> assertEquals("15", ent.getCustomerId()),
                () -> assertEquals("30", ent.getPaymentId()),
                () -> assertNull(ent.getCreatedAt()),
                () -> assertEquals(OrderStatus.RECEIVED, ent.getStatus()),
                () -> assertTrue(
                        ent.getProductIds() == null
                                || ent.getProductIds().isEmpty(),
                        "productIds defaults empty"
                )
        );
    }
}
