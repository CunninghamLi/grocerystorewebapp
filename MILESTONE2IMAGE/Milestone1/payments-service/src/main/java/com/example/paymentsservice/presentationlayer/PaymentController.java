package com.example.paymentsservice.presentationlayer;

import com.example.paymentsservice.businesslayer.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponseModel>> getAll() {
        return ResponseEntity.ok(service.getAllPayments());
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponseModel> getById(@PathVariable String paymentId) {
        return ResponseEntity.ok(service.getPaymentByPaymentId(paymentId));
    }

    @PostMapping
    public ResponseEntity<PaymentResponseModel> create(@RequestBody PaymentRequestModel model) {
        return ResponseEntity.ok(service.addPayment(model));
    }

    @PutMapping("/{paymentId}")
    public ResponseEntity<PaymentResponseModel> update(@RequestBody PaymentRequestModel model, @PathVariable String paymentId) {
        return ResponseEntity.ok(service.updatePayment(model, paymentId));
    }

    @DeleteMapping("/{paymentId}")
    public ResponseEntity<Void> delete(@PathVariable String paymentId) {
        service.removePayment(paymentId);
        return ResponseEntity.noContent().build();
    }
}
