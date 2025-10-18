// src/main/java/com/example/apigateway/presentationlayer/customer/CustomerController.java
package com.example.apigateway.presentationlayer.customer;

import com.example.apigateway.domainclient.CustomerServiceClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerServiceClient client;

    public CustomerController(CustomerServiceClient client) {
        this.client = client;
    }

    @GetMapping
    public List<CustomerResponseModel> getAll() {
        return client.getAllCustomers();
    }

    @GetMapping("/{customerId}")
    public CustomerResponseModel getById(@PathVariable String customerId) {
        return client.getCustomerById(customerId);
    }

    @PostMapping
    public CustomerResponseModel create(@RequestBody CustomerRequestModel model) {
        return client.addCustomer(model);
    }

    @PutMapping("/{customerId}")
    public CustomerResponseModel update(@PathVariable String customerId,
                                        @RequestBody CustomerRequestModel model) {
        return client.updateCustomer(customerId, model);
    }

    @DeleteMapping("/{customerId}")
    public void deleteById(@PathVariable String customerId) {
        client.deleteCustomerById(customerId);
    }

    @GetMapping("/email/{email}")
    public CustomerResponseModel getByEmail(@PathVariable String email) {
        return client.getCustomerByEmail(email);
    }

    @DeleteMapping("/email/{email}")
    public void deleteByEmail(@PathVariable String email) {
        client.deleteCustomerByEmail(email);
    }

    @GetMapping("/by-phone")
    public List<CustomerResponseModel> getByPhone(
            @RequestParam String country,
            @RequestParam Map<String, String> allParams) {
        // pass through map entries as "key=value" strings
        return client.getCustomersByPhone(country, allParams.values().toArray(new String[0]));
    }
}
