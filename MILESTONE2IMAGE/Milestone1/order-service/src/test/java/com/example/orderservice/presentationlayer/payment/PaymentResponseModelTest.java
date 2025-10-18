package com.example.orderservice.presentationlayer.payment;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;

class PaymentResponseModelTest {

    @Test
    void beanProperties_work() {
        var m = new PaymentResponseModel();
        m.setId(99);
        m.setPaymentId("X");
        m.setAmount("9");
        m.setMethod("M");
        m.setCurrency("C");
        m.setPaymentDate(LocalDate.of(2025,5,14));

        assertThat(m.getId()).isEqualTo(99);
        assertThat(m.getPaymentId()).isEqualTo("X");
        assertThat(m.getAmount()).isEqualTo("9");
        assertThat(m.getMethod()).isEqualTo("M");
        assertThat(m.getCurrency()).isEqualTo("C");
        assertThat(m.getPaymentDate()).isEqualTo(LocalDate.of(2025,5,14));
    }
}
