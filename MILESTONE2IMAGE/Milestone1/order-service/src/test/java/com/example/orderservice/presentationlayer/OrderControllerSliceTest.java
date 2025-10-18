package com.example.orderservice.presentationlayer;

import com.example.orderservice.businesslayer.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerSliceTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @MockBean OrderService svc;

    @Test
    void getAll_success() throws Exception {
        when(svc.getOrders()).thenReturn(List.of(new OrderResponseModel()));
        mvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getById_success() throws Exception {
        var model = new OrderResponseModel();
        model.setOrderId("X");
        when(svc.getOrderById("X")).thenReturn(model);

        mvc.perform(get("/api/v1/orders/X"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("X"));
    }

    @Test
    void add_success() throws Exception {
        var req = new OrderRequestModel(); req.setCustomerId("C");
        var res = new OrderResponseModel(); res.setOrderId("Y");
        when(svc.addOrder(req)).thenReturn(res);

        mvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value("Y"));
    }

    @Test
    void update_success() throws Exception {
        var req = new OrderRequestModel(); req.setCustomerId("C");
        var res = new OrderResponseModel(); res.setOrderId("Z");
        when(svc.updateOrder(req,"Z")).thenReturn(res);

        mvc.perform(put("/api/v1/orders/Z")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("Z"));
    }

    @Test
    void delete_success() throws Exception {
        mvc.perform(delete("/api/v1/orders/D"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getMissing_throws404() throws Exception {
        when(svc.getOrderById("X"))
                .thenThrow(new com.example.orderservice.utils.exceptions.NotFoundException("no"));
        mvc.perform(get("/api/v1/orders/X"))
                .andExpect(status().isNotFound());
    }
}
