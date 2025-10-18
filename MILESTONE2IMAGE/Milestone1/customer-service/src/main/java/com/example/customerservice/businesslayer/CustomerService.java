package com.example.customerservice.businesslayer;

import com.example.customerservice.presentationlayer.CustomerRequestModel;
import com.example.customerservice.presentationlayer.CustomerResponseModel;

import java.util.List;

public interface CustomerService {
    List<CustomerResponseModel> getCustomers();
    CustomerResponseModel addCustomer(CustomerRequestModel customerRequestModel);
    CustomerResponseModel updateCustomer(CustomerRequestModel updatedCustomer, Integer customerId);
    void removeCustomer(Integer customerId);
    CustomerResponseModel getCustomerByCustomerId(Integer customerId);
    CustomerResponseModel getCustomerByEmail(String email);
    void removeCustomerByEmail(String email);
}
