package com.example.paymentsservice.datamapperlayer;

import com.example.paymentsservice.datalayer.Payment;
import com.example.paymentsservice.presentationlayer.PaymentRequestModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PaymentRequestMapper {

    @Mapping(target = "id", ignore = true)
    Payment requestModelToEntity(PaymentRequestModel model);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequestModel(PaymentRequestModel model, @MappingTarget Payment entity);
}
