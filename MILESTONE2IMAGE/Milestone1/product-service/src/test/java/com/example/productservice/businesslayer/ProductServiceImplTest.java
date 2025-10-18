package com.example.productservice.businesslayer;

import com.example.productservice.datalayer.Product;
import com.example.productservice.datalayer.ProductRepository;
import com.example.productservice.datamapperlayer.ProductRequestMapper;
import com.example.productservice.datamapperlayer.ProductResponseMapper;
import com.example.productservice.presentationlayer.ProductRequestModel;
import com.example.productservice.presentationlayer.ProductResponseModel;
import com.example.productservice.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock private ProductRepository productRepository;
    @Mock private ProductRequestMapper productRequestMapper;
    @Mock private ProductResponseMapper productResponseMapper;

    @InjectMocks private ProductServiceImpl productService;

    private Product entity;
    private ProductRequestModel requestModel;
    private ProductResponseModel responseModel;

    @BeforeEach
    void setUp() {
        // prepare a dummy Product entity
        entity = new Product();
        entity.setId(1L);
        entity.setProductId("pid-1");
        entity.setName("MyProduct");
        // …set description, price, stockQuantity if needed…

        // prepare matching request & response models
        requestModel = new ProductRequestModel();
        requestModel.setName("MyProduct");
        // …set other fields…

        responseModel = new ProductResponseModel();
        responseModel.setId(1L);
        responseModel.setProductId("pid-1");
        responseModel.setName("MyProduct");
        // …set other fields…
    }

    @Test
    void whenGetProducts_thenReturnList() {
        when(productRepository.findAll()).thenReturn(List.of(entity));
        when(productResponseMapper.entityToProductResponseModel(entity)).thenReturn(responseModel);

        var list = productService.getProducts();

        assertThat(list).hasSize(1)
                .extracting(ProductResponseModel::getName)
                .containsExactly("MyProduct");
        verify(productRepository).findAll();
    }

    @Test
    void whenGetByProductIdExists_thenReturn() {
        when(productRepository.findByProductId("pid-1"))
                .thenReturn(Optional.of(entity));
        when(productResponseMapper.entityToProductResponseModel(entity))
                .thenReturn(responseModel);

        var out = productService.getProductByProductId("pid-1");

        assertThat(out.getId()).isEqualTo(1L);
        verify(productRepository).findByProductId("pid-1");
    }

    @Test
    void whenGetByProductIdMissing_thenThrow() {
        when(productRepository.findByProductId("missing"))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> productService.getProductByProductId("missing"));
    }



    @Test
    void whenUpdateProductExists_thenSaveAndReturn() {
        when(productRepository.findByProductId("pid-1"))
                .thenReturn(Optional.of(entity));
        // map new fields into existing entity
        doNothing().when(productRequestMapper)
                .updateEntityFromRequestModel(requestModel, entity);
        when(productRepository.save(entity)).thenReturn(entity);
        when(productResponseMapper.entityToProductResponseModel(entity))
                .thenReturn(responseModel);

        var out = productService.updateProduct(requestModel, "pid-1");

        assertThat(out.getId()).isEqualTo(1L);
        verify(productRepository).findByProductId("pid-1");
        verify(productRequestMapper).updateEntityFromRequestModel(requestModel, entity);
        verify(productRepository).save(entity);
    }

    @Test
    void whenUpdateProductMissing_thenThrow() {
        when(productRepository.findByProductId("nope"))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> productService.updateProduct(requestModel, "nope"));
    }

    @Test
    void whenRemoveProductExists_thenDelete() {
        when(productRepository.findByProductId("pid-1"))
                .thenReturn(Optional.of(entity));

        productService.removeProduct("pid-1");

        verify(productRepository).delete(entity);
    }

    @Test
    void whenRemoveProductMissing_thenThrow() {
        when(productRepository.findByProductId("nope"))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> productService.removeProduct("nope"));
    }
}
