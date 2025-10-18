package com.example.productservice.presentationlayer;

import com.example.productservice.businesslayer.ProductService;
import com.example.productservice.presentationlayer.ProductRequestModel;
import com.example.productservice.presentationlayer.ProductResponseModel;
import com.example.productservice.utils.exceptions.DuplicateVinException;
import com.example.productservice.utils.exceptions.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ProductService productService;

    private final ObjectMapper om = new ObjectMapper();

    @Test
    void getAllProducts_success() throws Exception {
        var resp = new ProductResponseModel();
        resp.setId(1L);
        resp.setProductId("P1");
        resp.setName("A");
        resp.setDescription("Desc");
        resp.setPrice(5.0);
        resp.setStockQuantity(10);

        when(productService.getProducts()).thenReturn(List.of(resp));

        mvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value("P1"))
                .andExpect(jsonPath("$[0].name").value("A"));
    }

    @Test
    void getById_notFound() throws Exception {
        when(productService.getProductByProductId("99"))
                .thenThrow(new NotFoundException("99"));

        mvc.perform(get("/api/v1/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.httpStatus").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("99"));
    }

    @Test
    void addProduct_duplicate() throws Exception {
        var req = new ProductRequestModel();
        req.setName("X");
        req.setDescription("D");
        req.setPrice(1.0);
        req.setStockQuantity(2);

        when(productService.addProduct(any()))
                .thenThrow(new DuplicateVinException("dup"));

        mvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.httpStatus").value("UNPROCESSABLE_ENTITY"))
                .andExpect(jsonPath("$.message").value("dup"));
    }

    @Test
    void updateProduct_success() throws Exception {
        var req = new ProductRequestModel();
        req.setName("Y");
        req.setDescription("Z");
        req.setPrice(2.5);
        req.setStockQuantity(5);

        var out = new ProductResponseModel();
        out.setId(1L);
        out.setProductId("P1");
        out.setName("Y");
        out.setDescription("Z");
        out.setPrice(2.5);
        out.setStockQuantity(5);

        when(productService.updateProduct(any(), any())).thenReturn(out);

        mvc.perform(put("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Y"));
    }


}
