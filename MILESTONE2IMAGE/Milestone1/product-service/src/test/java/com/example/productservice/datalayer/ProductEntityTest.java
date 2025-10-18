package com.example.productservice.datalayer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductEntityTest {

    @Test
    void noArgsConstructorAndSettersAndGetters() {
        Product p = new Product();
        p.setId(1L);
        p.setProductId("pid1");
        p.setName("Name");
        p.setDescription("Desc");
        p.setPrice(10.5);
        p.setStockQuantity(100);

        assertEquals(1L, p.getId());
        assertEquals("pid1", p.getProductId());
        assertEquals("Name", p.getName());
        assertEquals("Desc", p.getDescription());
        assertEquals(10.5, p.getPrice());
        assertEquals(100, p.getStockQuantity());
    }

    @Test
    void allArgsConstructor() {
        Product p = new Product(
                2L,
                "pid2",
                "Name2",
                "Desc2",
                20.0,
                200
        );

        assertEquals(2L, p.getId());
        assertEquals("pid2", p.getProductId());
        assertEquals("Name2", p.getName());
        assertEquals("Desc2", p.getDescription());
        assertEquals(20.0, p.getPrice());
        assertEquals(200, p.getStockQuantity());
    }

    @Test
    void builderGeneratesCorrectObject() {
        Product p = Product.builder()
                .id(3L)
                .productId("pid3")
                .name("Name3")
                .description("Desc3")
                .price(30.0)
                .stockQuantity(300)
                .build();

        assertEquals(3L, p.getId());
        assertEquals("pid3", p.getProductId());
        assertEquals("Name3", p.getName());
        assertEquals("Desc3", p.getDescription());
        assertEquals(30.0, p.getPrice());
        assertEquals(300, p.getStockQuantity());
    }
}
