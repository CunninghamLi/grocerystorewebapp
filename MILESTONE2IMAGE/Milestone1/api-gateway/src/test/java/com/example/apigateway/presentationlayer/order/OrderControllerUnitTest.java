package com.example.apigateway.presentationlayer.order;

import com.example.apigateway.domainclient.OrderServiceClient;
import com.example.apigateway.utils.GlobalControllerExceptionHandler;
import com.example.apigateway.utils.exceptions.InvalidInputException;
import com.example.apigateway.utils.exceptions.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderControllerUnitTest {



    @Mock
    private OrderServiceClient orderServiceClient;

    @InjectMocks
    private OrderController orderController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(orderController)
                .setControllerAdvice(new GlobalControllerExceptionHandler())
                .build();
    }

    @Test
    void getAllOrders_shouldReturnOk() throws Exception {
        OrderResponseModel order = OrderResponseModel.builder().orderId("order1").customerId("cust1").build();
        when(orderServiceClient.getAllOrders()).thenReturn(List.of(order));

        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value("order1"));
        verify(orderServiceClient, times(1)).getAllOrders();
    }

    @Test
    void getOrderById_whenOrderExists_shouldReturnOk() throws Exception {
        OrderResponseModel order = OrderResponseModel.builder().orderId("order1").customerId("cust1").build();
        when(orderServiceClient.getOrderById("order1")).thenReturn(order);

        mockMvc.perform(get("/api/v1/orders/order1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("order1"));
        verify(orderServiceClient, times(1)).getOrderById("order1");
    }

    @Test
    void getOrderById_whenOrderNotFound_shouldReturnNotFound() throws Exception {
        when(orderServiceClient.getOrderById("orderNonExistent"))
                .thenThrow(new NotFoundException("Order not found"));

        mockMvc.perform(get("/api/v1/orders/orderNonExistent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order not found"));
        verify(orderServiceClient, times(1)).getOrderById("orderNonExistent");
    }

    @Test
    void createOrder_withValidData_shouldReturnOk() throws Exception {
        OrderRequestModel requestModel = OrderRequestModel.builder().customerId("cust1").amount(100.0).build();
        OrderResponseModel responseModel = OrderResponseModel.builder().orderId("order2").customerId("cust1").build();
        when(orderServiceClient.addOrder(any(OrderRequestModel.class))).thenReturn(responseModel);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestModel)))
                .andExpect(status().isOk()) // Client returns the created object
                .andExpect(jsonPath("$.orderId").value("order2"));
        verify(orderServiceClient, times(1)).addOrder(any(OrderRequestModel.class));
    }

    @Test
    void createOrder_withInvalidData_shouldReturnUnprocessableEntity() throws Exception {
        OrderRequestModel requestModel = OrderRequestModel.builder().customerId(null).build(); // Invalid: customerId is null
        when(orderServiceClient.addOrder(any(OrderRequestModel.class)))
                .thenThrow(new InvalidInputException("Invalid order data: Customer ID cannot be null"));

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestModel)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Invalid order data: Customer ID cannot be null"));
        verify(orderServiceClient, times(1)).addOrder(any(OrderRequestModel.class));
    }

    @Test
    void updateOrder_whenOrderExistsAndValidData_shouldReturnOk() throws Exception {
        OrderRequestModel requestModel = OrderRequestModel.builder().status(OrderStatus.SHIPPED).build();
        OrderResponseModel responseModel = OrderResponseModel.builder().orderId("order1").status(OrderStatus.SHIPPED).build();
        when(orderServiceClient.updateOrder(eq("order1"), any(OrderRequestModel.class))).thenReturn(responseModel);

        mockMvc.perform(put("/api/v1/orders/order1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestModel)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SHIPPED"));
        verify(orderServiceClient, times(1)).updateOrder(eq("order1"), any(OrderRequestModel.class));
    }

    @Test
    void updateOrder_whenOrderNotFound_shouldReturnNotFound() throws Exception {
        OrderRequestModel requestModel = OrderRequestModel.builder().status(OrderStatus.CANCELLED).build();
        when(orderServiceClient.updateOrder(eq("orderNonExistent"), any(OrderRequestModel.class)))
                .thenThrow(new NotFoundException("Order to update not found"));

        mockMvc.perform(put("/api/v1/orders/orderNonExistent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestModel)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order to update not found"));
        verify(orderServiceClient, times(1)).updateOrder(eq("orderNonExistent"), any(OrderRequestModel.class));
    }

    @Test
    void updateOrder_withInvalidData_shouldReturnUnprocessableEntity() throws Exception {
        OrderRequestModel requestModel = OrderRequestModel.builder().amount(-50.0).build(); // Invalid amount
        when(orderServiceClient.updateOrder(eq("order1"), any(OrderRequestModel.class)))
                .thenThrow(new InvalidInputException("Invalid order data for update: Amount cannot be negative"));

        mockMvc.perform(put("/api/v1/orders/order1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestModel)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Invalid order data for update: Amount cannot be negative"));
        verify(orderServiceClient, times(1)).updateOrder(eq("order1"), any(OrderRequestModel.class));
    }


    @Test
    void deleteOrder_whenOrderExists_shouldReturnOk() throws Exception {
        doNothing().when(orderServiceClient).deleteOrder("order1");

        mockMvc.perform(delete("/api/v1/orders/order1"))
                .andExpect(status().isOk());
        verify(orderServiceClient, times(1)).deleteOrder("order1");
    }

    @Test
    void deleteOrder_whenOrderNotFound_shouldReturnNotFound() throws Exception {
        doThrow(new NotFoundException("Order to delete not found")).when(orderServiceClient).deleteOrder("orderNonExistent");

        mockMvc.perform(delete("/api/v1/orders/orderNonExistent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order to delete not found"));
        verify(orderServiceClient, times(1)).deleteOrder("orderNonExistent");
    }
}