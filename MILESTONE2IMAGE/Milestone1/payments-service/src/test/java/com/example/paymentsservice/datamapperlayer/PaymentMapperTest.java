package com.example.paymentsservice.datamapperlayer;

import com.example.paymentsservice.datalayer.Payment;
import com.example.paymentsservice.presentationlayer.PaymentRequestModel;
import com.example.paymentsservice.presentationlayer.PaymentResponseModel;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentMapperTest {

    private final PaymentRequestMapper requestMapper = Mappers.getMapper(PaymentRequestMapper.class);
    private final PaymentResponseMapper responseMapper = Mappers.getMapper(PaymentResponseMapper.class);

    @Test
    void testRequestModelToEntity() {
        PaymentRequestModel model = new PaymentRequestModel();
        model.setPaymentId("PAY-MAP-1");
        model.setAmount("150.00");
        model.setMethod("Credit Card");
        model.setCurrency("USD");
        model.setPaymentDate(LocalDate.of(2025, 5, 1));

        Payment entity = requestMapper.requestModelToEntity(model);
        assertEquals("PAY-MAP-1", entity.getPaymentId());
        assertEquals("150.00", entity.getAmount());
        assertEquals("Credit Card", entity.getMethod());
        assertEquals("USD", entity.getCurrency());
        assertEquals(LocalDate.of(2025, 5, 1), entity.getPaymentDate());
    }

    @Test
    void testEntityToResponseModel() {
        Payment entity = new Payment();
        entity.setId(1);
        entity.setPaymentId("PAY-MAP-2");
        entity.setAmount("99.99");
        entity.setMethod("PayPal");
        entity.setCurrency("CAD");
        entity.setPaymentDate(LocalDate.of(2025, 5, 2));

        PaymentResponseModel response = responseMapper.entityToResponseModel(entity);
        assertEquals(1, response.getId());
        assertEquals("PAY-MAP-2", response.getPaymentId());
        assertEquals("99.99", response.getAmount());
        assertEquals("PayPal", response.getMethod());
        assertEquals("CAD", response.getCurrency());
        assertEquals(LocalDate.of(2025, 5, 2), response.getPaymentDate());
    }
}
