package com.example.orderservice.presentationlayer.product;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class ProductRequestModelTest {

    @Test
    void beanProperties_work() {
        var m = new ProductRequestModel();
        m.setName("X");
        m.setDescription("D");
        m.setPrice(4.5);
        m.setStockQuantity(9);

        assertThat(m.getName()).isEqualTo("X");
        assertThat(m.getDescription()).isEqualTo("D");
        assertThat(m.getPrice()).isEqualTo(4.5);
        assertThat(m.getStockQuantity()).isEqualTo(9);
    }
}
