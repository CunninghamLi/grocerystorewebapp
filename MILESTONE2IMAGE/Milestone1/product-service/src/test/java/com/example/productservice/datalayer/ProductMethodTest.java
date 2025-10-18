package com.example.productservice.datalayer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductMethodTest {

    @Test
    void enumValues_containExpectedConstants() {
        ProductMethod[] methods = ProductMethod.values();
        assertArrayEquals(
                new ProductMethod[]{ProductMethod.IN_STORE, ProductMethod.ONLINE},
                methods
        );
    }

    @Test
    void valueOf_returnsCorrectConstant() {
        assertEquals(ProductMethod.IN_STORE, ProductMethod.valueOf("IN_STORE"));
        assertEquals(ProductMethod.ONLINE,    ProductMethod.valueOf("ONLINE"));
    }

    @Test
    void name_and_toString_areConsistent() {
        for (ProductMethod m : ProductMethod.values()) {
            assertEquals(m.name(), m.toString());
        }
    }
}
