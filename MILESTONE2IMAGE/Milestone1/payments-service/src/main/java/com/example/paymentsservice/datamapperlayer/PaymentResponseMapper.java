package com.example.paymentsservice.datamapperlayer;

import com.example.paymentsservice.datalayer.Payment;
import com.example.paymentsservice.presentationlayer.PaymentResponseModel;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentResponseMapper {
    PaymentResponseModel entityToResponseModel(Payment payment);
    List<PaymentResponseModel> entityListToResponseModelList(List<Payment> payments);
}
