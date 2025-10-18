package com.example.customerservice.presentationlayer;

import com.example.customerservice.businesslayer.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping()
    public ResponseEntity<List<CustomerResponseModel>> getCustomers() {
        log.info("Fetching all customers");
        List<CustomerResponseModel> customers = customerService.getCustomers();
        return ResponseEntity.ok().body(customers);
    }

    @GetMapping(value ="/{customerId}")
    public ResponseEntity<CustomerResponseModel> getCustomerByCustomerId(@PathVariable Integer customerId) {
        return ResponseEntity.ok().body(customerService.getCustomerByCustomerId(customerId));
    }

    @PostMapping(produces = "application/json", consumes = "application/json")
    public ResponseEntity<CustomerResponseModel> addCustomer(@RequestBody CustomerRequestModel customerRequestModel) {
        log.info("Adding new customer: {}", customerRequestModel);
        CustomerResponseModel createdCustomer = customerService.addCustomer(customerRequestModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
    }

    @PutMapping(value = "/{customerId}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<CustomerResponseModel> updateCustomer(@RequestBody CustomerRequestModel customerRequestModel, @PathVariable Integer customerId) {
        return ResponseEntity.ok().body(customerService.updateCustomer(customerRequestModel, customerId));
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Integer customerId) {
        customerService.removeCustomer(customerId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<CustomerResponseModel> getCustomerByEmail(@PathVariable String email) {
        log.info("Fetching customer with email {}", email);
        CustomerResponseModel customer = customerService.getCustomerByEmail(email);
        return ResponseEntity.ok(customer);
    }

    @DeleteMapping("/email/{email}")
    public ResponseEntity<Void> removeCustomerByEmail(@PathVariable String email) {
        log.info("Deleting customer with email {}", email);
        customerService.removeCustomerByEmail(email);
        return ResponseEntity.noContent().build();
    }
}
