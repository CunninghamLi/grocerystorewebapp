package com.example.apigateway.presentationlayer.product;

import com.example.apigateway.domainclient.ProductServiceClient;
import com.example.apigateway.presentationlayer.product.ProductRequestModel;
import com.example.apigateway.presentationlayer.product.ProductResponseModel;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ProductServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    private ObjectMapper objectMapper; // Use a real ObjectMapper for testing serialization/deserialization

    private ProductServiceClient productServiceClient;

    private final String productServiceHost = "localhost"; // Or read from a test properties file if needed
    private final String productServicePort = "7004"; // Default from your application.yml
    private String baseUrl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Ensure JavaTime types are handled

        baseUrl = "http://" + productServiceHost + ":" + productServicePort + "/api/v1/products";
        productServiceClient = new ProductServiceClient(restTemplate, objectMapper, productServiceHost, productServicePort);
    }

    @Test
    void getAllProducts_shouldReturnListOfProducts() {
        ProductResponseModel product1 = ProductResponseModel.builder().productId("p1").name("Laptop").build();
        ProductResponseModel product2 = ProductResponseModel.builder().productId("p2").name("Mouse").build();
        ProductResponseModel[] productsArray = {product1, product2};

        when(restTemplate.getForEntity(eq(baseUrl), eq(ProductResponseModel[].class)))
                .thenReturn(new ResponseEntity<>(productsArray, HttpStatus.OK));

        List<ProductResponseModel> result = productServiceClient.getAllProducts();

        assertEquals(2, result.size());
        assertEquals("p1", result.get(0).getProductId());
        verify(restTemplate, times(1)).getForEntity(eq(baseUrl), eq(ProductResponseModel[].class));
    }

    @Test
    void getProductById_whenProductExists_shouldReturnProduct() {
        String productId = "p1";
        ProductResponseModel expectedProduct = ProductResponseModel.builder().productId(productId).name("Laptop").build();
        String url = baseUrl + "/" + productId;

        when(restTemplate.getForObject(eq(url), eq(ProductResponseModel.class))).thenReturn(expectedProduct);

        ProductResponseModel actualProduct = productServiceClient.getProductById(productId);

        assertNotNull(actualProduct);
        assertEquals(productId, actualProduct.getProductId());
        verify(restTemplate, times(1)).getForObject(eq(url), eq(ProductResponseModel.class));
    }

    @Test
    void getProductById_whenProductNotFound_shouldThrowNotFoundException() throws JsonProcessingException {
        String productId = "pNonExistent";
        String url = baseUrl + "/" + productId;
        HttpErrorInfo errorInfo = new HttpErrorInfo(HttpStatus.NOT_FOUND, "/api/v1/products/" + productId, "Product not found");
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND,
                "Not Found",
                HttpHeaders.EMPTY,
                objectMapper.writeValueAsBytes(errorInfo),
                null
        );

        when(restTemplate.getForObject(eq(url), eq(ProductResponseModel.class))).thenThrow(ex);

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            productServiceClient.getProductById(productId);
        });

        assertEquals("Product not found", thrown.getMessage());
        verify(restTemplate, times(1)).getForObject(eq(url), eq(ProductResponseModel.class));
    }

    @Test
    void getProductById_whenServiceReturnsUnparseableError_shouldThrowFallbackNotFoundException() {
        String productId = "pError";
        String url = baseUrl + "/" + productId;
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND,
                "Not Found",
                HttpHeaders.EMPTY,
                "Unparseable error body".getBytes(), // Simulate unparseable JSON
                null
        );

        when(restTemplate.getForObject(eq(url), eq(ProductResponseModel.class))).thenThrow(ex);

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            productServiceClient.getProductById(productId);
        });

        // Check that the fallback message is used
        assertEquals("Not found", thrown.getMessage());
        verify(restTemplate, times(1)).getForObject(eq(url), eq(ProductResponseModel.class));
    }


    @Test
    void addProduct_shouldReturnCreatedProduct() {
        ProductRequestModel requestModel = ProductRequestModel.builder().name("New Keyboard").price(75.00).build();
        ProductResponseModel expectedResponse = ProductResponseModel.builder().productId("pNew").name("New Keyboard").price(75.00).build();

        when(restTemplate.postForObject(eq(baseUrl), any(ProductRequestModel.class), eq(ProductResponseModel.class)))
                .thenReturn(expectedResponse);

        ProductResponseModel actualResponse = productServiceClient.addProduct(requestModel);

        assertNotNull(actualResponse);
        assertEquals("pNew", actualResponse.getProductId());
        verify(restTemplate, times(1)).postForObject(eq(baseUrl), any(ProductRequestModel.class), eq(ProductResponseModel.class));
    }

    @Test
    void addProduct_whenServiceReturnsUnprocessableEntity_shouldThrowInvalidInputException() throws JsonProcessingException {
        ProductRequestModel requestModel = ProductRequestModel.builder().name("").build(); // Invalid data
        HttpErrorInfo errorInfo = new HttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY, "/api/v1/products", "Product name cannot be empty");
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Unprocessable Entity",
                HttpHeaders.EMPTY,
                objectMapper.writeValueAsBytes(errorInfo),
                null
        );

        // Note: addProduct in your client doesn't have a try-catch for postForObject.
        // If the expectation is that the controller handles it, or if the client should, adjust this.
        // For this test, assuming postForObject itself throws and it propagates up, or if client catches it.
        // Based on your ProductServiceClient, addProduct doesn't handle exceptions.
        // So the HttpClientErrorException would propagate. The test below assumes a future enhancement
        // where the client's addProduct might handle it or if the exception is directly tested.
        // If it just propagates, then you'd assertThrows(HttpClientErrorException.class, ...)

        when(restTemplate.postForObject(eq(baseUrl), any(ProductRequestModel.class), eq(ProductResponseModel.class)))
                .thenThrow(ex); // Simulating service throwing an error that gets converted to HttpClientErrorException by RestTemplate

        // This assertion depends on if ProductServiceClient.addProduct() catches and re-throws or not.
        // Your current ProductServiceClient.addProduct() does NOT catch.
        // So, the original HttpClientErrorException would be thrown.
        HttpClientErrorException thrownClientEx = assertThrows(HttpClientErrorException.class, () -> {
            productServiceClient.addProduct(requestModel);
        });
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, thrownClientEx.getStatusCode());


        // If ProductServiceClient.addProduct were to catch and wrap, the test would be:
        // InvalidInputException thrown = assertThrows(InvalidInputException.class, () -> {
        //    productServiceClient.addProduct(requestModel);
        // });
        // assertEquals("Product name cannot be empty", thrown.getMessage());

        verify(restTemplate, times(1)).postForObject(eq(baseUrl), any(ProductRequestModel.class), eq(ProductResponseModel.class));
    }


    @Test
    void updateProduct_shouldReturnUpdatedProduct() {
        String productId = "p1";
        ProductRequestModel requestModel = ProductRequestModel.builder().name("Updated Laptop").build();
        ProductResponseModel updatedProduct = ProductResponseModel.builder().productId(productId).name("Updated Laptop").build();
        String url = baseUrl + "/" + productId;

        doNothing().when(restTemplate).put(eq(url), any(ProductRequestModel.class));
        // The client calls getProductById after put
        when(restTemplate.getForObject(eq(url), eq(ProductResponseModel.class))).thenReturn(updatedProduct);


        ProductResponseModel actualResponse = productServiceClient.updateProduct(productId, requestModel);

        assertNotNull(actualResponse);
        assertEquals("Updated Laptop", actualResponse.getName());
        verify(restTemplate, times(1)).put(eq(url), any(ProductRequestModel.class));
        verify(restTemplate, times(1)).getForObject(eq(url), eq(ProductResponseModel.class)); // Verifies the getById call
    }

    @Test
    void updateProduct_whenPutThrowsNotFound_shouldThrowNotFoundException() throws JsonProcessingException {
        String productId = "pNonExistent";
        ProductRequestModel requestModel = ProductRequestModel.builder().name("Non Existent").build();
        String url = baseUrl + "/" + productId;
        HttpErrorInfo errorInfo = new HttpErrorInfo(HttpStatus.NOT_FOUND, "/api/v1/products/" + productId, "Product to update not found");
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND,
                "Not Found",
                HttpHeaders.EMPTY,
                objectMapper.writeValueAsBytes(errorInfo),
                null
        );

        doThrow(ex).when(restTemplate).put(eq(url), any(ProductRequestModel.class));

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            productServiceClient.updateProduct(productId, requestModel);
        });
        assertEquals("Product to update not found", thrown.getMessage());
        verify(restTemplate, times(1)).put(eq(url), any(ProductRequestModel.class));
        verify(restTemplate, never()).getForObject(anyString(), any()); // getProductById should not be called
    }

    @Test
    void updateProduct_whenGetAfterPutThrowsNotFound_shouldThrowNotFoundException() throws JsonProcessingException {
        String productId = "p1";
        ProductRequestModel requestModel = ProductRequestModel.builder().name("Updated Laptop").build();
        String url = baseUrl + "/" + productId;

        doNothing().when(restTemplate).put(eq(url), any(ProductRequestModel.class));

        HttpErrorInfo errorInfo = new HttpErrorInfo(HttpStatus.NOT_FOUND, "/api/v1/products/" + productId, "Product vanished after update");
        HttpClientErrorException exGet = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND,
                "Not Found",
                HttpHeaders.EMPTY,
                objectMapper.writeValueAsBytes(errorInfo),
                null
        );
        when(restTemplate.getForObject(eq(url), eq(ProductResponseModel.class))).thenThrow(exGet);


        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            productServiceClient.updateProduct(productId, requestModel);
        });
        assertEquals("Product vanished after update", thrown.getMessage());
        verify(restTemplate, times(1)).put(eq(url), any(ProductRequestModel.class));
        verify(restTemplate, times(1)).getForObject(eq(url), eq(ProductResponseModel.class));
    }


    @Test
    void deleteProduct_shouldCallDeleteOnRestTemplate() {
        String productId = "p1";
        String url = baseUrl + "/" + productId;

        doNothing().when(restTemplate).delete(eq(url));
        productServiceClient.deleteProduct(productId);
        verify(restTemplate, times(1)).delete(eq(url));
    }

    @Test
    void deleteProduct_whenServiceThrowsNotFound_shouldThrowNotFoundException() throws JsonProcessingException {
        String productId = "pNonExistent";
        String url = baseUrl + "/" + productId;
        HttpErrorInfo errorInfo = new HttpErrorInfo(HttpStatus.NOT_FOUND, "/api/v1/products/" + productId, "Product to delete not found");
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND,
                "Not Found",
                HttpHeaders.EMPTY,
                objectMapper.writeValueAsBytes(errorInfo),
                null
        );

        doThrow(ex).when(restTemplate).delete(eq(url));

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            productServiceClient.deleteProduct(productId);
        });
        assertEquals("Product to delete not found", thrown.getMessage());
        verify(restTemplate, times(1)).delete(eq(url));
    }
}