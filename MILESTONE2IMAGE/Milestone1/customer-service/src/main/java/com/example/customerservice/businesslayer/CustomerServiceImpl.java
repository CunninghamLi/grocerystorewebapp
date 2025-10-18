package com.example.customerservice.businesslayer;

import com.example.customerservice.datalayer.Address;
import com.example.customerservice.datalayer.Customer;
import com.example.customerservice.datalayer.CustomerRepository;
import com.example.customerservice.datamapperlayer.CustomerRequestMapper;
import com.example.customerservice.datamapperlayer.CustomerResponseMapper;
import com.example.customerservice.presentationlayer.CustomerRequestModel;
import com.example.customerservice.presentationlayer.CustomerResponseModel;
import com.example.customerservice.utils.exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerResponseMapper customerResponseMapper;
    private final CustomerRequestMapper customerRequestMapper;

    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerResponseMapper customerResponseMapper, CustomerRequestMapper customerRequestMapper) {
        this.customerRepository = customerRepository;
        this.customerResponseMapper = customerResponseMapper;
        this.customerRequestMapper = customerRequestMapper;
    }

    @Override
    public List<CustomerResponseModel> getCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customerResponseMapper.entityListToResponseModelList(customers);
    }

    @Override
    public CustomerResponseModel getCustomerByCustomerId(Integer customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Unknown customerId: " + customerId));
        return customerResponseMapper.entityToResponseModel(customer);
    }

    @Override
    public CustomerResponseModel addCustomer(CustomerRequestModel customerRequestModel) {
        Address address = new Address(customerRequestModel.getStreetAddress(), customerRequestModel.getCity(),
                customerRequestModel.getProvince(), customerRequestModel.getCountry(), customerRequestModel.getPostalCode());

        Customer customer = customerRequestMapper.requestModelToEntity(customerRequestModel, address);
        customer.setAddress(address);
        return customerResponseMapper.entityToResponseModel(customerRepository.save(customer));
    }

    @Override
    public CustomerResponseModel updateCustomer(CustomerRequestModel customerRequestModel, Integer customerId) {
        Customer existingCustomer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Unknown customerId: " + customerId));

        Address address = new Address(customerRequestModel.getStreetAddress(), customerRequestModel.getCity(),
                customerRequestModel.getProvince(), customerRequestModel.getCountry(), customerRequestModel.getPostalCode());

        Customer updatedCustomer = customerRequestMapper.requestModelToEntity(customerRequestModel, address);
        updatedCustomer.setId(existingCustomer.getId());

        Customer response = customerRepository.save(updatedCustomer);
        return customerResponseMapper.entityToResponseModel(response);
    }

    @Override
    public void removeCustomer(Integer customerId) {
        Customer existingCustomer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Unknown customerId: " + customerId));

        customerRepository.delete(existingCustomer);
    }

    @Override
    public CustomerResponseModel getCustomerByEmail(String email) {
        Customer customer = customerRepository.findByEmailAddress(email);
        if (customer == null) {
            throw new NotFoundException("Customer with email " + email + " not found");
        }
        return customerResponseMapper.entityToResponseModel(customer);
    }

    @Override
    @Transactional
    public void removeCustomerByEmail(String email) {
        Customer customer = customerRepository.findByEmailAddress(email);
        if (customer == null) {
            throw new NotFoundException("Customer with email " + email + " not found");
        }
        customerRepository.deleteByEmailAddress(email);
    }
}
