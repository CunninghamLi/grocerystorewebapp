package com.example.apigateway.businesslayer;

import com.example.apigateway.presentationlayer.customer.CustomerRequestModel;
import com.example.apigateway.presentationlayer.customer.CustomerResponseModel;

import java.util.List;

public interface CustomerService {
    List<CustomerResponseModel> getCustomers();
    CustomerResponseModel getCustomerById(String customerId);
    CustomerResponseModel addCustomer(CustomerRequestModel customerRequestModel);
    CustomerResponseModel updateCustomer(String customerId, CustomerRequestModel customerRequestModel);
    void deleteCustomer(String customerId);
}
