
package com.example.paymentsservice.businesslayer;

import com.example.paymentsservice.presentationlayer.PaymentRequestModel;
import com.example.paymentsservice.presentationlayer.PaymentResponseModel;

import java.util.List;

public interface PaymentService {
    List<PaymentResponseModel> getAllPayments();
    PaymentResponseModel getPaymentByPaymentId(String paymentId);
    PaymentResponseModel addPayment(PaymentRequestModel paymentRequestModel);
    PaymentResponseModel updatePayment(PaymentRequestModel paymentRequestModel, String paymentId);
    void removePayment(String paymentId);
}
