// src/main/java/com/example/apigateway/domainclient/ProductServiceClient.java
package com.example.apigateway.domainclient;

import com.example.apigateway.presentationlayer.product.ProductRequestModel;
import com.example.apigateway.presentationlayer.product.ProductResponseModel;
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
public class ProductServiceClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final String baseUrl;

    public ProductServiceClient(RestTemplate restTemplate,
                                ObjectMapper mapper,
                                @Value("${app.product-service.host}") String host,
                                @Value("${app.product-service.port}") String port) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.baseUrl = "http://" + host + ":" + port + "/api/v1/products";
    }

    public List<ProductResponseModel> getAllProducts() {
        var resp = restTemplate.getForEntity(baseUrl, ProductResponseModel[].class);
        return Arrays.asList(resp.getBody());
    }

    public ProductResponseModel getProductById(String productId) {
        try {
            return restTemplate.getForObject(baseUrl + "/" + productId, ProductResponseModel.class);
        } catch (HttpClientErrorException ex) {
            handleException(ex);
            return null;
        }
    }

    public ProductResponseModel addProduct(ProductRequestModel req) {
        return restTemplate.postForObject(baseUrl, req, ProductResponseModel.class);
    }

    public ProductResponseModel updateProduct(String productId, ProductRequestModel req) {
        try {
            restTemplate.put(baseUrl + "/" + productId, req);
            return getProductById(productId);
        } catch (HttpClientErrorException ex) {
            handleException(ex);
            return null;
        }
    }

    public void deleteProduct(String productId) {
        try {
            restTemplate.delete(baseUrl + "/" + productId);
        } catch (HttpClientErrorException ex) {
            handleException(ex);
        }
    }

    private void handleException(HttpClientErrorException ex) { /* same as above */ }
}
