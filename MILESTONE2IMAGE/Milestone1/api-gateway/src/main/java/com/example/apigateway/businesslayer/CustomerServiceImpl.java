package com.example.apigateway.businesslayer;

import com.example.apigateway.domainclient.CustomerServiceClient;
import com.example.apigateway.presentationlayer.customer.CustomerRequestModel;
import com.example.apigateway.presentationlayer.customer.CustomerResponseModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerServiceClient client;

    public CustomerServiceImpl(CustomerServiceClient client) {
        this.client = client;
    }

    @Override
    public List<CustomerResponseModel> getCustomers() {
        return client.getAllCustomers();
    }

    @Override
    public CustomerResponseModel getCustomerById(String customerId) {
        return client.getCustomerById(customerId);
    }

    @Override
    public CustomerResponseModel addCustomer(CustomerRequestModel customerRequestModel) {
        return client.addCustomer(customerRequestModel);
    }

    @Override
    public CustomerResponseModel updateCustomer(String customerId, CustomerRequestModel customerRequestModel) {
        return client.updateCustomer(customerId, customerRequestModel);
    }

    @Override
    public void deleteCustomer(String customerId) {
        // This is the corrected line:
        client.deleteCustomerById(customerId);
    }
}