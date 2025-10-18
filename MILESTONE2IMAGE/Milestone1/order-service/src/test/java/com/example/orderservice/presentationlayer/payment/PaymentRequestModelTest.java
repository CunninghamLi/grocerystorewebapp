package com.example.orderservice.presentationlayer.payment;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;

class PaymentRequestModelTest {

    @Test
    void beanProperties_work() {
        var d = LocalDate.of(2025,5,14);
        var m = PaymentRequestModel.builder()
                .paymentId("X")
                .amount("9")
                .method("M")
                .currency("C")
                .paymentDate(d)
                .build();

        assertThat(m.getPaymentId()).isEqualTo("X");
        assertThat(m.getAmount()).isEqualTo("9");
        assertThat(m.getMethod()).isEqualTo("M");
        assertThat(m.getCurrency()).isEqualTo("C");
        assertThat(m.getPaymentDate()).isEqualTo(d);
    }
}
