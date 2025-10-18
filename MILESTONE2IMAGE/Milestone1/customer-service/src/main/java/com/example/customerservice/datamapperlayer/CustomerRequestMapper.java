package com.example.customerservice.datamapperlayer;

import com.example.customerservice.datalayer.Address;
import com.example.customerservice.datalayer.Customer;
import com.example.customerservice.presentationlayer.CustomerRequestModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CustomerRequestMapper {

    Customer requestModelToEntity(CustomerRequestModel model, Address address);

    @Mapping(target = "id", ignore = true)
    void updateModelToEntity(CustomerRequestModel model, @MappingTarget Customer customer);
}
