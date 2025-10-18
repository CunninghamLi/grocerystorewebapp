package com.example.paymentsservice.datamapperlayer;

import com.example.paymentsservice.datalayer.Payment;
import com.example.paymentsservice.presentationlayer.PaymentRequestModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PaymentRequestMapperUnitTest {

    private PaymentRequestMapper mapper;

    @BeforeEach
    void setup() {
        mapper = Mappers.getMapper(PaymentRequestMapper.class);
    }

    @Test
    void requestModelToEntity_fullMapping() {
        PaymentRequestModel model = new PaymentRequestModel();
        model.setPaymentId("PAY-100");
        model.setAmount("12.34");
        model.setMethod("CARD");
        model.setCurrency("USD");
        model.setPaymentDate(LocalDate.of(2025, 5, 14));

        Payment entity = mapper.requestModelToEntity(model);

        assertNotNull(entity);
        // id is ignored by the mapping
        assertNull(entity.getId());
        assertEquals("PAY-100", entity.getPaymentId());
        assertEquals("12.34",    entity.getAmount());
        assertEquals("CARD",     entity.getMethod());
        assertEquals("USD",      entity.getCurrency());
        assertEquals(LocalDate.of(2025, 5, 14), entity.getPaymentDate());
    }

    @Test
    void requestModelToEntity_nullReturnsNull() {
        Payment entity = mapper.requestModelToEntity(null);
        assertNull(entity, "Mapping a null model should return null");
    }

    @Test
    void updateEntityFromRequestModel_overwritesAllExceptId() {
        // create an existing entity with an id and some old values
        Payment existing = new Payment();
        existing.setId(42);
        existing.setPaymentId("OLD");
        existing.setAmount("0.00");
        existing.setMethod("NONE");
        existing.setCurrency("AAA");
        existing.setPaymentDate(LocalDate.of(2000, 1, 1));

        // request with new data
        PaymentRequestModel model = new PaymentRequestModel();
        model.setPaymentId("NEW");
        model.setAmount("99.99");
        model.setMethod("PAYPAL");
        model.setCurrency("EUR");
        model.setPaymentDate(LocalDate.of(2025, 12, 31));

        mapper.updateEntityFromRequestModel(model, existing);

        // id must be unchanged
        assertEquals(42, existing.getId());
        // everything else overwritten
        assertEquals("NEW", existing.getPaymentId());
        assertEquals("99.99", existing.getAmount());
        assertEquals("PAYPAL", existing.getMethod());
        assertEquals("EUR", existing.getCurrency());
        assertEquals(LocalDate.of(2025, 12, 31), existing.getPaymentDate());
    }
}
