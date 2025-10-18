package com.example.productservice.presentationlayer;

import com.example.productservice.businesslayer.ProductService;
import com.example.productservice.presentationlayer.ProductRequestModel;
import com.example.productservice.utils.exceptions.DuplicateVinException;
import com.example.productservice.utils.exceptions.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerIT {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ProductService productService;

    private final ObjectMapper om = new ObjectMapper();

    @Test
    void handleNotFoundException() throws Exception {
        when(productService.getProducts())
                .thenThrow(new NotFoundException("nope"));

        mvc.perform(get("/api/v1/products"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.httpStatus").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("nope"));
    }

    @Test
    void handleDuplicateVinException() throws Exception {
        var req = new ProductRequestModel();
        req.setName("A");
        req.setDescription("D");
        req.setPrice(1.0);
        req.setStockQuantity(1);

        when(productService.addProduct(any()))
                .thenThrow(new DuplicateVinException("dup"));

        mvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.httpStatus").value("UNPROCESSABLE_ENTITY"))
                .andExpect(jsonPath("$.message").value("dup"));
    }
}
