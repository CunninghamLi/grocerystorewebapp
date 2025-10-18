package com.example.paymentsservice.businesslayer;

import com.example.paymentsservice.datalayer.Payment;
import com.example.paymentsservice.datalayer.PaymentRepository;
import com.example.paymentsservice.datamapperlayer.PaymentRequestMapper;
import com.example.paymentsservice.datamapperlayer.PaymentResponseMapper;
import com.example.paymentsservice.presentationlayer.PaymentRequestModel;
import com.example.paymentsservice.presentationlayer.PaymentResponseModel;
import com.example.paymentsservice.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PaymentServiceImplUnitTest {

    private PaymentRepository repository;
    private PaymentRequestMapper requestMapper;
    private PaymentResponseMapper responseMapper;
    private PaymentServiceImpl service;

    @BeforeEach
    void setup() {
        repository = mock(PaymentRepository.class);
        requestMapper = mock(PaymentRequestMapper.class);
        responseMapper = mock(PaymentResponseMapper.class);
        service = new PaymentServiceImpl(repository, requestMapper, responseMapper);
    }

    @Test
    void testGetPaymentById_notFound() {
        when(repository.findByPaymentId("BAD-ID")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getPaymentByPaymentId("BAD-ID"));
    }

    @Test
    void testGetAll_empty() {
        when(repository.findAll()).thenReturn(Collections.emptyList());
        assertTrue(service.getAllPayments().isEmpty());
    }

    @Test
    void testAddPayment_success() {
        PaymentRequestModel model = new PaymentRequestModel();
        Payment payment = new Payment();
        PaymentResponseModel response = new PaymentResponseModel();

        when(requestMapper.requestModelToEntity(model)).thenReturn(payment);
        when(repository.save(payment)).thenReturn(payment);
        when(responseMapper.entityToResponseModel(payment)).thenReturn(response);

        assertEquals(response, service.addPayment(model));
    }
}
