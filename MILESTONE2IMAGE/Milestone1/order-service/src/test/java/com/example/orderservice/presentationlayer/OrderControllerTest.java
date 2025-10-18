// src/test/java/com/example/orderservice/presentationlayer/OrderControllerTest.java
package com.example.orderservice.presentationlayer;

import com.example.orderservice.businesslayer.OrderService;
import com.example.orderservice.datalayer.OrderStatus;
import com.example.orderservice.utils.exceptions.DuplicateVinException;
import com.example.orderservice.utils.exceptions.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private OrderService orderService;

    private final ObjectMapper om = new ObjectMapper();

    @Test
    void getAllOrders_success() throws Exception {
        var resp = OrderResponseModel.builder()
                .orderId("OID1")
                .paymentId("20")
                .customerId("10")
                .productIds(List.of("1","2"))
                .createdAt(LocalDateTime.now())
                .status(OrderStatus.RECEIVED)
                .amount(50.0)
                .customerFirstName("F")
                .customerLastName("L")
                .build();

        when(orderService.getOrders()).thenReturn(List.of(resp));

        mvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value("OID1"))
                .andExpect(jsonPath("$[0].customerId").value("10"));
    }

    @Test
    void getById_notFound() throws Exception {
        when(orderService.getOrderById("X"))
                .thenThrow(new NotFoundException("X"));

        mvc.perform(get("/api/v1/orders/X"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.httpStatus").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("X"));
    }

    @Test
    void addOrder_duplicate() throws Exception {
        var req = OrderRequestModel.builder()
                .paymentId("20")
                .customerId("10")
                .status(OrderStatus.RECEIVED)
                .amount(50.0)
                .build();

        when(orderService.addOrder(any()))
                .thenThrow(new DuplicateVinException("dup"));

        mvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.httpStatus").value("UNPROCESSABLE_ENTITY"))
                .andExpect(jsonPath("$.message").value("dup"));
    }

    @Test
    void updateOrder_success() throws Exception {
        var req = OrderRequestModel.builder()
                .paymentId("20")
                .customerId("10")
                .status(OrderStatus.ON_THE_WAY)
                .amount(75.0)
                .build();

        var resp = OrderResponseModel.builder()
                .orderId("OID2")
                .paymentId("20")
                .customerId("10")
                .productIds(List.of("3","4"))
                .createdAt(LocalDateTime.now())
                .status(OrderStatus.ON_THE_WAY)
                .amount(75.0)
                .customerFirstName("F")
                .customerLastName("L")
                .build();

        when(orderService.updateOrder(any(), any()))
                .thenReturn(resp);

        mvc.perform(put("/api/v1/orders/OID2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("OID2"))
                .andExpect(jsonPath("$.status").value("ON_THE_WAY"));
    }

    @Test
    void deleteOrder_success() throws Exception {
        mvc.perform(delete("/api/v1/orders/OID3"))
                .andExpect(status().isNoContent());
    }
}
