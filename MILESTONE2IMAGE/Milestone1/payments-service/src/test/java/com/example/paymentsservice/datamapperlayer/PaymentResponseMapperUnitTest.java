package com.example.paymentsservice.datamapperlayer;

import com.example.paymentsservice.datalayer.Payment;
import com.example.paymentsservice.presentationlayer.PaymentResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PaymentResponseMapperUnitTest {

    private PaymentResponseMapper mapper;

    @BeforeEach
    void setup() {
        mapper = Mappers.getMapper(PaymentResponseMapper.class);
    }

    @Test
    void entityToResponseModel_fullMapping() {
        Payment entity = new Payment();
        entity.setId(7);
        entity.setPaymentId("PAY-200");
        entity.setAmount("55.55");
        entity.setMethod("CASH");
        entity.setCurrency("GBP");
        entity.setPaymentDate(LocalDate.of(2025, 6, 1));

        PaymentResponseModel resp = mapper.entityToResponseModel(entity);

        assertNotNull(resp);
        assertEquals(7,    resp.getId());
        assertEquals("PAY-200", resp.getPaymentId());
        assertEquals("55.55",   resp.getAmount());
        assertEquals("CASH",    resp.getMethod());
        assertEquals("GBP",     resp.getCurrency());
        assertEquals(LocalDate.of(2025, 6, 1), resp.getPaymentDate());
    }

    @Test
    void entityToResponseModel_nullReturnsNull() {
        PaymentResponseModel resp = mapper.entityToResponseModel(null);
        assertNull(resp, "Mapping a null entity should return null");
    }

    @Test
    void entityListToResponseModelList_happyPath() {
        Payment e1 = new Payment();
        e1.setId(1);
        e1.setPaymentId("A");
        e1.setAmount("1.00");
        e1.setMethod("M1");
        e1.setCurrency("X");
        e1.setPaymentDate(LocalDate.of(2025,1,1));

        Payment e2 = new Payment();
        e2.setId(2);
        e2.setPaymentId("B");
        e2.setAmount("2.00");
        e2.setMethod("M2");
        e2.setCurrency("Y");
        e2.setPaymentDate(LocalDate.of(2025,2,2));

        List<PaymentResponseModel> list = mapper.entityListToResponseModelList(Arrays.asList(e1, e2));
        assertNotNull(list);
        assertEquals(2, list.size());

        // verify first item
        assertEquals(1, list.get(0).getId());
        assertEquals("A", list.get(0).getPaymentId());

        // verify second item
        assertEquals(2, list.get(1).getId());
        assertEquals("B", list.get(1).getPaymentId());
    }

    @Test
    void entityListToResponseModelList_emptyList() {
        List<PaymentResponseModel> list = mapper.entityListToResponseModelList(Collections.emptyList());
        assertNotNull(list);
        assertTrue(list.isEmpty(), "Mapping an empty list should return an empty list");
    }

    @Test
    void entityListToResponseModelList_nullReturnsNull() {
        List<PaymentResponseModel> list = mapper.entityListToResponseModelList(null);
        assertNull(list, "Mapping a null list should return null");
    }
}
