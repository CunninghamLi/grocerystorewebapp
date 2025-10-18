package com.example.customerservice.datamapperlayer;

import com.example.customerservice.datalayer.Customer;
import com.example.customerservice.presentationlayer.CustomerController;
import com.example.customerservice.presentationlayer.CustomerResponseModel;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.hateoas.Link;

import java.util.List;

import static org.springframework.hateoas.server.core.DummyInvocationUtils.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Mapper(componentModel = "spring")
public interface CustomerResponseMapper {

    @Mapping(source = "id", target = "customerId")
    @Mapping(expression = "java(customer.getAddress().getStreetAddress())", target = "streetAddress")
    @Mapping(expression = "java(customer.getAddress().getCity())", target = "city")
    @Mapping(expression = "java(customer.getAddress().getProvince())", target = "province")
    @Mapping(expression = "java(customer.getAddress().getCountry())", target = "country")
    @Mapping(expression = "java(customer.getAddress().getPostalCode())", target = "postalCode")
    CustomerResponseModel entityToResponseModel(Customer customer);

    List<CustomerResponseModel> entityListToResponseModelList(List<Customer> customers);

    @AfterMapping
    default void addLinks(@MappingTarget CustomerResponseModel model, Customer customer) {

        Link selfLink = linkTo(methodOn(CustomerController.class)
                .getCustomerByCustomerId(model.getCustomerId()))
                .withSelfRel();
        model.add(selfLink);

        Link customersLink = linkTo(methodOn(CustomerController.class).getCustomers())
                .withRel("customers");
        model.add(customersLink);
    }

}
