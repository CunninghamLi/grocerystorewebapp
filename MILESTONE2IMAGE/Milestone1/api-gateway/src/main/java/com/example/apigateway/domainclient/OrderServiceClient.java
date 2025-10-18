// src/main/java/com/example/apigateway/domainclient/OrderServiceClient.java
package com.example.apigateway.domainclient;

import com.example.apigateway.presentationlayer.order.OrderRequestModel;
import com.example.apigateway.presentationlayer.order.OrderResponseModel;
import com.example.apigateway.utils.HttpErrorInfo;
import com.example.apigateway.utils.exceptions.InvalidInputException;
import com.example.apigateway.utils.exceptions.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class OrderServiceClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final String baseUrl;

    public OrderServiceClient(RestTemplate restTemplate,
                              ObjectMapper mapper,
                              @Value("${app.order-service.host}") String host,
                              @Value("${app.order-service.port}") String port) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.baseUrl = "http://" + host + ":" + port + "/api/v1/orders";
    }

    public List<OrderResponseModel> getAllOrders() {
        var resp = restTemplate.getForEntity(baseUrl, OrderResponseModel[].class);
        return Arrays.asList(resp.getBody());
    }

    public OrderResponseModel getOrderById(String orderId) {
        try {
            return restTemplate.getForObject(baseUrl + "/" + orderId, OrderResponseModel.class);
        } catch (HttpClientErrorException ex) {
            handleException(ex);
            return null;
        }
    }

    public OrderResponseModel addOrder(OrderRequestModel req) {
        return restTemplate.postForObject(baseUrl, req, OrderResponseModel.class);
    }

    public OrderResponseModel updateOrder(String orderId, OrderRequestModel req) {
        try {
            restTemplate.put(baseUrl + "/" + orderId, req);
            return getOrderById(orderId);
        } catch (HttpClientErrorException ex) {
            handleException(ex);
            return null;
        }
    }

    public void deleteOrder(String orderId) {
        try {
            restTemplate.delete(baseUrl + "/" + orderId);
        } catch (HttpClientErrorException ex) {
            handleException(ex);
        }
    }

    private void handleException(HttpClientErrorException ex) { /* as above */ }
}
