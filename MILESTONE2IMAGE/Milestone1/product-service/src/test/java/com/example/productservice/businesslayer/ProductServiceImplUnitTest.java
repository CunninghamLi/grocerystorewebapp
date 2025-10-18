package com.example.productservice.businesslayer;

import com.example.productservice.datalayer.Product;
import com.example.productservice.datalayer.ProductRepository;
import com.example.productservice.datamapperlayer.ProductResponseMapper;
import com.example.productservice.presentationlayer.ProductResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplUnitTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductResponseMapper productResponseMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductResponseModel responseModel;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .productId("P123")
                .name("Mock Product")
                .description("Mock Description")
                .price(19.99)
                .stockQuantity(5)
                .build();

        responseModel = new ProductResponseModel();
        responseModel.setId(1L);
        responseModel.setName("Mock Product");
        responseModel.setDescription("Mock Description");
        responseModel.setPrice(19.99);
        responseModel.setStockQuantity(5);
    }

    @Test
    void whenGetProducts_thenReturnProductResponseList() {
        when(productRepository.findAll()).thenReturn(Collections.singletonList(product));
        when(productResponseMapper.entityToProductResponseModel(product)).thenReturn(responseModel);

        List<ProductResponseModel> result = productService.getProducts();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Mock Product", result.get(0).getName());

        verify(productRepository, times(1)).findAll();
        verify(productResponseMapper, times(1)).entityToProductResponseModel(product);
    }
}
