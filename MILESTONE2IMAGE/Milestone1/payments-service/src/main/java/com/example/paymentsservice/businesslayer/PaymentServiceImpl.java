package com.example.paymentsservice.businesslayer;

import com.example.paymentsservice.datalayer.Payment;
import com.example.paymentsservice.datalayer.PaymentRepository;
import com.example.paymentsservice.datamapperlayer.PaymentRequestMapper;
import com.example.paymentsservice.datamapperlayer.PaymentResponseMapper;
import com.example.paymentsservice.presentationlayer.PaymentRequestModel;
import com.example.paymentsservice.presentationlayer.PaymentResponseModel;
import com.example.paymentsservice.utils.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;
    private final PaymentRequestMapper requestMapper;
    private final PaymentResponseMapper responseMapper;

    public PaymentServiceImpl(PaymentRepository repository, PaymentRequestMapper requestMapper, PaymentResponseMapper responseMapper) {
        this.repository = repository;
        this.requestMapper = requestMapper;
        this.responseMapper = responseMapper;
    }

    @Override
    public List<PaymentResponseModel> getAllPayments() {
        return repository.findAll()
                .stream()
                .map(responseMapper::entityToResponseModel)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentResponseModel getPaymentByPaymentId(String paymentId) {
        Payment payment = repository.findByPaymentId(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found: " + paymentId));
        return responseMapper.entityToResponseModel(payment);
    }

    @Override
    public PaymentResponseModel addPayment(PaymentRequestModel model) {
        Payment entity = requestMapper.requestModelToEntity(model);
        return responseMapper.entityToResponseModel(repository.save(entity));
    }

    @Override
    public PaymentResponseModel updatePayment(PaymentRequestModel model, String paymentId) {
        Payment existing = repository.findByPaymentId(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found: " + paymentId));
        requestMapper.updateEntityFromRequestModel(model, existing);
        return responseMapper.entityToResponseModel(repository.save(existing));
    }

    @Override
    public void removePayment(String paymentId) {
        Payment existing = repository.findByPaymentId(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found: " + paymentId));
        repository.delete(existing);
    }
}
