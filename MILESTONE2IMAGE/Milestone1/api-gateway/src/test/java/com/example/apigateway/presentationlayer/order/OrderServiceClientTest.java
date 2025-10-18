package com.example.apigateway.presentationlayer.order;

import com.example.apigateway.domainclient.OrderServiceClient;
import com.example.apigateway.presentationlayer.order.OrderRequestModel;
import com.example.apigateway.presentationlayer.order.OrderResponseModel;
import com.example.apigateway.presentationlayer.order.OrderStatus;
import com.example.apigateway.utils.HttpErrorInfo;
import com.example.apigateway.utils.exceptions.InvalidInputException;
import com.example.apigateway.utils.exceptions.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class OrderServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    private ObjectMapper objectMapper;

    private OrderServiceClient orderServiceClient;

    private final String orderServiceHost = "localhost";
    private final String orderServicePort = "7001"; // From default profile in application.yml
    private String baseUrl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        baseUrl = "http://" + orderServiceHost + ":" + orderServicePort + "/api/v1/orders";
        // Assuming OrderServiceClient's handleException is similar to ProductServiceClient's
        // If not, these tests would need adjustment for that client's specific exception handling.
        orderServiceClient = new OrderServiceClient(restTemplate, objectMapper, orderServiceHost, orderServicePort);
    }

    @Test
    void getAllOrders_shouldReturnListOfOrders() {
        OrderResponseModel order1 = OrderResponseModel.builder().orderId("o1").status(OrderStatus.PENDING).build();
        OrderResponseModel order2 = OrderResponseModel.builder().orderId("o2").status(OrderStatus.SHIPPED).build();
        OrderResponseModel[] ordersArray = {order1, order2};

        when(restTemplate.getForEntity(eq(baseUrl), eq(OrderResponseModel[].class)))
                .thenReturn(new ResponseEntity<>(ordersArray, HttpStatus.OK));

        List<OrderResponseModel> result = orderServiceClient.getAllOrders();

        assertEquals(2, result.size());
        assertEquals("o1", result.get(0).getOrderId());
        verify(restTemplate, times(1)).getForEntity(eq(baseUrl), eq(OrderResponseModel[].class));
    }

    @Test
    void getAllOrders_whenServiceReturnsEmpty_shouldReturnEmptyList() {
        OrderResponseModel[] emptyArray = {};
        when(restTemplate.getForEntity(eq(baseUrl), eq(OrderResponseModel[].class)))
                .thenReturn(new ResponseEntity<>(emptyArray, HttpStatus.OK));

        List<OrderResponseModel> result = orderServiceClient.getAllOrders();
        assertTrue(result.isEmpty());
    }


    @Test
    void getOrderById_whenOrderExists_shouldReturnOrder() {
        String orderId = "o1";
        OrderResponseModel expectedOrder = OrderResponseModel.builder().orderId(orderId).status(OrderStatus.DELIVERED).build();
        String url = baseUrl + "/" + orderId;

        when(restTemplate.getForObject(eq(url), eq(OrderResponseModel.class))).thenReturn(expectedOrder);

        OrderResponseModel actualOrder = orderServiceClient.getOrderById(orderId);

        assertNotNull(actualOrder);
        assertEquals(orderId, actualOrder.getOrderId());
    }

    @Test
    void getOrderById_whenOrderNotFound_shouldThrowNotFoundException() throws JsonProcessingException {
        String orderId = "oNonExistent";
        String url = baseUrl + "/" + orderId;
        // Assuming OrderServiceClient.handleException is implemented like ProductServiceClient's
        HttpErrorInfo errorInfo = new HttpErrorInfo(HttpStatus.NOT_FOUND, "/api/v1/orders/" + orderId, "Order not found by ID");
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY, objectMapper.writeValueAsBytes(errorInfo), null);

        when(restTemplate.getForObject(eq(url), eq(OrderResponseModel.class))).thenThrow(ex);

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> orderServiceClient.getOrderById(orderId));
        assertEquals("Order not found by ID", thrown.getMessage());
    }

    @Test
    void addOrder_shouldReturnCreatedOrder() {
        OrderRequestModel requestModel = OrderRequestModel.builder().customerId("c1").amount(100.0).build();
        OrderResponseModel expectedResponse = OrderResponseModel.builder().orderId("oNew").customerId("c1").build();

        when(restTemplate.postForObject(eq(baseUrl), any(OrderRequestModel.class), eq(OrderResponseModel.class)))
                .thenReturn(expectedResponse);

        OrderResponseModel actualResponse = orderServiceClient.addOrder(requestModel);

        assertNotNull(actualResponse);
        assertEquals("oNew", actualResponse.getOrderId());
    }

    // OrderServiceClient.addOrder does not catch exceptions.
    @Test
    void addOrder_whenServiceError_shouldPropagateHttpClientErrorException() throws JsonProcessingException {
        OrderRequestModel requestModel = OrderRequestModel.builder().customerId("").build(); // Invalid
        HttpErrorInfo errorInfo = new HttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY, "/api/v1/orders" , "Customer ID is required");
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity", HttpHeaders.EMPTY, objectMapper.writeValueAsBytes(errorInfo), null);

        when(restTemplate.postForObject(eq(baseUrl), any(OrderRequestModel.class), eq(OrderResponseModel.class)))
                .thenThrow(ex);

        HttpClientErrorException thrown = assertThrows(HttpClientErrorException.class, () -> {
            orderServiceClient.addOrder(requestModel);
        });
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, thrown.getStatusCode());
        assertTrue(thrown.getResponseBodyAsString().contains("Customer ID is required"));
    }

    @Test
    void updateOrder_shouldReturnUpdatedOrder() {
        String orderId = "o1";
        OrderRequestModel requestModel = OrderRequestModel.builder().status(OrderStatus.COMPLETED).build();
        OrderResponseModel updatedOrder = OrderResponseModel.builder().orderId(orderId).status(OrderStatus.COMPLETED).build();
        String url = baseUrl + "/" + orderId;

        doNothing().when(restTemplate).put(eq(url), any(OrderRequestModel.class));
        when(restTemplate.getForObject(eq(url), eq(OrderResponseModel.class))).thenReturn(updatedOrder);

        OrderResponseModel actualResponse = orderServiceClient.updateOrder(orderId, requestModel);

        assertNotNull(actualResponse);
        assertEquals(OrderStatus.COMPLETED, actualResponse.getStatus());
    }

    @Test
    void updateOrder_whenPutThrowsNotFound_shouldThrowNotFoundException() throws JsonProcessingException {
        String orderId = "oNonExistent";
        OrderRequestModel requestModel = OrderRequestModel.builder().status(OrderStatus.CANCELLED).build();
        String url = baseUrl + "/" + orderId;
        HttpErrorInfo errorInfo = new HttpErrorInfo(HttpStatus.NOT_FOUND, "/api/v1/orders/" + orderId, "Order for update not found");
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY, objectMapper.writeValueAsBytes(errorInfo), null);


        doThrow(ex).when(restTemplate).put(eq(url), any(OrderRequestModel.class));

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> orderServiceClient.updateOrder(orderId, requestModel));
        assertEquals("Order for update not found", thrown.getMessage());
    }

    @Test
    void deleteOrder_shouldCallDeleteOnRestTemplate() {
        String orderId = "o1";
        String url = baseUrl + "/" + orderId;

        doNothing().when(restTemplate).delete(eq(url));
        orderServiceClient.deleteOrder(orderId);
        verify(restTemplate, times(1)).delete(eq(url));
    }

    @Test
    void deleteOrder_whenServiceThrowsNotFound_shouldThrowNotFoundException() throws JsonProcessingException {
        String orderId = "oNonExistent";
        String url = baseUrl + "/" + orderId;
        HttpErrorInfo errorInfo = new HttpErrorInfo(HttpStatus.NOT_FOUND, "/api/v1/orders/" + orderId, "Order for deletion not found");
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY, objectMapper.writeValueAsBytes(errorInfo), null);

        doThrow(ex).when(restTemplate).delete(eq(url));

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> orderServiceClient.deleteOrder(orderId));
        assertEquals("Order for deletion not found", thrown.getMessage());
    }
}