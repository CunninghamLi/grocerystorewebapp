package com.example.apigateway.presentationlayer.product;

import com.example.apigateway.domainclient.ProductServiceClient;
import com.example.apigateway.utils.GlobalControllerExceptionHandler;
import com.example.apigateway.utils.exceptions.InvalidInputException;
import com.example.apigateway.utils.exceptions.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProductControllerUnitTest {

    @Mock
    private ProductServiceClient productServiceClient;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setControllerAdvice(new GlobalControllerExceptionHandler())
                .build();
    }

    @Test
    void getAllProducts_shouldReturnOk() throws Exception {
        ProductResponseModel product = ProductResponseModel.builder().productId("prod1").name("Laptop").build();
        when(productServiceClient.getAllProducts()).thenReturn(List.of(product));

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value("prod1"));
        verify(productServiceClient, times(1)).getAllProducts();
    }

    @Test
    void getProductById_whenProductExists_shouldReturnOk() throws Exception {
        ProductResponseModel product = ProductResponseModel.builder().productId("prod1").name("Laptop").build();
        when(productServiceClient.getProductById("prod1")).thenReturn(product);

        mockMvc.perform(get("/api/v1/products/prod1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value("prod1"));
        verify(productServiceClient, times(1)).getProductById("prod1");
    }

    @Test
    void getProductById_whenProductNotFound_shouldReturnNotFound() throws Exception {
        when(productServiceClient.getProductById("prodNonExistent"))
                .thenThrow(new NotFoundException("Product not found"));

        mockMvc.perform(get("/api/v1/products/prodNonExistent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found"));
        verify(productServiceClient, times(1)).getProductById("prodNonExistent");
    }

    @Test
    void createProduct_withValidData_shouldReturnCreated() throws Exception {
        ProductRequestModel requestModel = ProductRequestModel.builder().name("New Product").price(100.0).build();
        ProductResponseModel responseModel = ProductResponseModel.builder().productId("prod2").name("New Product").build();
        when(productServiceClient.addProduct(any(ProductRequestModel.class))).thenReturn(responseModel);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestModel)))
                .andExpect(status().isOk()) //The client returns the created object which results in 200 OK
                .andExpect(jsonPath("$.productId").value("prod2"));
        verify(productServiceClient, times(1)).addProduct(any(ProductRequestModel.class));
    }

    @Test
    void createProduct_withInvalidData_shouldReturnUnprocessableEntity() throws Exception {
        ProductRequestModel requestModel = ProductRequestModel.builder().name("").price(100.0).build(); // Invalid name
        when(productServiceClient.addProduct(any(ProductRequestModel.class)))
                .thenThrow(new InvalidInputException("Invalid product data"));

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestModel)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Invalid product data"));
        verify(productServiceClient, times(1)).addProduct(any(ProductRequestModel.class));
    }


    @Test
    void updateProduct_whenProductExistsAndValidData_shouldReturnOk() throws Exception {
        ProductRequestModel requestModel = ProductRequestModel.builder().name("Updated Product").price(120.0).build();
        ProductResponseModel responseModel = ProductResponseModel.builder().productId("prod1").name("Updated Product").build();
        when(productServiceClient.updateProduct(eq("prod1"), any(ProductRequestModel.class))).thenReturn(responseModel);

        mockMvc.perform(put("/api/v1/products/prod1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestModel)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Product"));
        verify(productServiceClient, times(1)).updateProduct(eq("prod1"), any(ProductRequestModel.class));
    }

    @Test
    void updateProduct_whenProductNotFound_shouldReturnNotFound() throws Exception {
        ProductRequestModel requestModel = ProductRequestModel.builder().name("Updated Product").build();
        when(productServiceClient.updateProduct(eq("prodNonExistent"), any(ProductRequestModel.class)))
                .thenThrow(new NotFoundException("Product to update not found"));

        mockMvc.perform(put("/api/v1/products/prodNonExistent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestModel)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product to update not found"));
        verify(productServiceClient, times(1)).updateProduct(eq("prodNonExistent"), any(ProductRequestModel.class));
    }

    @Test
    void updateProduct_withInvalidData_shouldReturnUnprocessableEntity() throws Exception {
        ProductRequestModel requestModel = ProductRequestModel.builder().name("").price(100.0).build(); // Invalid name
        when(productServiceClient.updateProduct(eq("prod1"), any(ProductRequestModel.class)))
                .thenThrow(new InvalidInputException("Invalid product data for update"));

        mockMvc.perform(put("/api/v1/products/prod1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestModel)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Invalid product data for update"));
        verify(productServiceClient, times(1)).updateProduct(eq("prod1"), any(ProductRequestModel.class));
    }

    @Test
    void deleteProduct_whenProductExists_shouldReturnNoContent() throws Exception {
        doNothing().when(productServiceClient).deleteProduct("prod1");

        mockMvc.perform(delete("/api/v1/products/prod1"))
                .andExpect(status().isOk()); //The client does not specify a return type, so 200 OK is expected.
        verify(productServiceClient, times(1)).deleteProduct("prod1");
    }

    @Test
    void deleteProduct_whenProductNotFound_shouldReturnNotFound() throws Exception {
        doThrow(new NotFoundException("Product to delete not found")).when(productServiceClient).deleteProduct("prodNonExistent");

        mockMvc.perform(delete("/api/v1/products/prodNonExistent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product to delete not found"));
        verify(productServiceClient, times(1)).deleteProduct("prodNonExistent");
    }
}