package com.example.orderservice.presentationlayer.product;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class ProductResponseModelTest {

    @Test
    void beanProperties_work() {
        var m = new ProductResponseModel();
        m.setId(123L);
        m.setProductId("P");
        m.setName("N");
        m.setDescription("D");
        m.setPrice(7.8);
        m.setStockQuantity(5);

        assertThat(m.getId()).isEqualTo(123L);
        assertThat(m.getProductId()).isEqualTo("P");
        assertThat(m.getName()).isEqualTo("N");
        assertThat(m.getDescription()).isEqualTo("D");
        assertThat(m.getPrice()).isEqualTo(7.8);
        assertThat(m.getStockQuantity()).isEqualTo(5);
    }
}
