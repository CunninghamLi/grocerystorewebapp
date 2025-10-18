package com.example.apigateway.presentationlayer.customer;

import com.example.apigateway.domainclient.CustomerServiceClient;
import com.example.apigateway.presentationlayer.customer.CustomerRequestModel;

import com.example.apigateway.presentationlayer.customer.CustomerResponseModel;
import com.example.apigateway.utils.HttpErrorInfo;
import com.example.apigateway.utils.exceptions.InvalidInputException;
import com.example.apigateway.utils.exceptions.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CustomerServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    private ObjectMapper objectMapper;

    private CustomerServiceClient customerServiceClient;

    private final String customerServiceHost = "localhost"; // Default from application.yml
    private final String customerServicePort = "7000"; // Default from application.yml
    private String baseUrl;
    private String emailUrlBase;
    private String byPhoneUrlBase;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        baseUrl = "http://" + customerServiceHost + ":" + customerServicePort + "/api/v1/customers";
        emailUrlBase = baseUrl + "/email";
        byPhoneUrlBase = baseUrl + "/by-phone";
        customerServiceClient = new CustomerServiceClient(restTemplate, objectMapper, customerServiceHost, customerServicePort);
    }

    @Test
    void getAllCustomers_shouldReturnListOfCustomers() {
        CustomerResponseModel cust1 = CustomerResponseModel.builder().customerId("c1").firstName("John").build();
        CustomerResponseModel cust2 = CustomerResponseModel.builder().customerId("c2").firstName("Jane").build();
        CustomerResponseModel[] customersArray = {cust1, cust2};

        when(restTemplate.getForEntity(eq(baseUrl), eq(CustomerResponseModel[].class)))
                .thenReturn(new ResponseEntity<>(customersArray, HttpStatus.OK));

        List<CustomerResponseModel> result = customerServiceClient.getAllCustomers();

        assertEquals(2, result.size());
        assertEquals("c1", result.get(0).getCustomerId());
        verify(restTemplate, times(1)).getForEntity(eq(baseUrl), eq(CustomerResponseModel[].class));
    }

    @Test
    void getCustomerById_whenCustomerExists_shouldReturnCustomer() {
        String customerId = "c1";
        CustomerResponseModel expectedCustomer = CustomerResponseModel.builder().customerId(customerId).firstName("John").build();
        String url = baseUrl + "/" + customerId;

        when(restTemplate.getForObject(eq(url), eq(CustomerResponseModel.class))).thenReturn(expectedCustomer);

        CustomerResponseModel actualCustomer = customerServiceClient.getCustomerById(customerId);

        assertNotNull(actualCustomer);
        assertEquals(customerId, actualCustomer.getCustomerId());
    }

    @Test
    void getCustomerById_whenNotFound_shouldThrowNotFoundException() throws JsonProcessingException {
        String customerId = "cNonExistent";
        String url = baseUrl + "/" + customerId;
        HttpErrorInfo errorInfo = new HttpErrorInfo(HttpStatus.NOT_FOUND, "/api/v1/customers/" + customerId, "Customer not found");
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY, objectMapper.writeValueAsBytes(errorInfo), null);

        when(restTemplate.getForObject(eq(url), eq(CustomerResponseModel.class))).thenThrow(ex);

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> customerServiceClient.getCustomerById(customerId));
        assertEquals("Customer not found", thrown.getMessage());
    }

    @Test
    void addCustomer_shouldReturnCreatedCustomer() {
        CustomerRequestModel requestModel = CustomerRequestModel.builder().firstName("Peter").lastName("Pan").build();
        CustomerResponseModel expectedResponse = CustomerResponseModel.builder().customerId("cNew").firstName("Peter").build();

        when(restTemplate.postForObject(eq(baseUrl), any(CustomerRequestModel.class), eq(CustomerResponseModel.class)))
                .thenReturn(expectedResponse);

        CustomerResponseModel actualResponse = customerServiceClient.addCustomer(requestModel);

        assertNotNull(actualResponse);
        assertEquals("cNew", actualResponse.getCustomerId());
    }

    // Example: Test addCustomer when microservice returns 422
    @Test
    void addCustomer_whenServiceReturnsUnprocessableEntity_shouldPropagateHttpClientErrorException() throws JsonProcessingException {
        CustomerRequestModel requestModel = CustomerRequestModel.builder().emailAddress("invalid").build(); // Invalid email
        HttpErrorInfo errorInfo = new HttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY, "/api/v1/customers", "Invalid email format");
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity", HttpHeaders.EMPTY, objectMapper.writeValueAsBytes(errorInfo), null);

        when(restTemplate.postForObject(eq(baseUrl), any(CustomerRequestModel.class), eq(CustomerResponseModel.class)))
                .thenThrow(ex);

        // CustomerServiceClient.addCustomer does not catch, so HttpClientErrorException propagates
        HttpClientErrorException thrown = assertThrows(HttpClientErrorException.class, () -> {
            customerServiceClient.addCustomer(requestModel);
        });
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, thrown.getStatusCode());
        assertTrue(thrown.getResponseBodyAsString().contains("Invalid email format"));
    }


    @Test
    void updateCustomer_shouldReturnUpdatedCustomer() {
        String customerId = "c1";
        CustomerRequestModel requestModel = CustomerRequestModel.builder().firstName("Johnathan").build();
        CustomerResponseModel updatedCustomer = CustomerResponseModel.builder().customerId(customerId).firstName("Johnathan").build();
        String url = baseUrl + "/" + customerId;

        doNothing().when(restTemplate).put(eq(url), any(CustomerRequestModel.class));
        when(restTemplate.getForObject(eq(url), eq(CustomerResponseModel.class))).thenReturn(updatedCustomer); // For the getCustomerById call

        CustomerResponseModel actualResponse = customerServiceClient.updateCustomer(customerId, requestModel);

        assertNotNull(actualResponse);
        assertEquals("Johnathan", actualResponse.getFirstName());
    }

    @Test
    void updateCustomer_whenPutThrowsNotFound_shouldThrowNotFoundException() throws JsonProcessingException {
        String customerId = "cNonExistent";
        CustomerRequestModel requestModel = CustomerRequestModel.builder().firstName("Ghost").build();
        String url = baseUrl + "/" + customerId;
        HttpErrorInfo errorInfo = new HttpErrorInfo(HttpStatus.NOT_FOUND, "/api/v1/customers/" + customerId, "Update failed: Customer not found");
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY, objectMapper.writeValueAsBytes(errorInfo), null);

        doThrow(ex).when(restTemplate).put(eq(url), any(CustomerRequestModel.class));

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> customerServiceClient.updateCustomer(customerId, requestModel));
        assertEquals("Update failed: Customer not found", thrown.getMessage());
        verify(restTemplate, never()).getForObject(anyString(), any()); // getCustomerById should not be called
    }

    @Test
    void deleteCustomerById_shouldCallDelete() {
        String customerId = "c1";
        String url = baseUrl + "/" + customerId;
        doNothing().when(restTemplate).delete(eq(url));
        customerServiceClient.deleteCustomerById(customerId);
        verify(restTemplate, times(1)).delete(eq(url));
    }

    @Test
    void deleteCustomerById_whenNotFound_shouldThrowNotFoundException() throws JsonProcessingException {
        String customerId = "cNonExistent";
        String url = baseUrl + "/" + customerId;
        HttpErrorInfo errorInfo = new HttpErrorInfo(HttpStatus.NOT_FOUND, "/api/v1/customers/" + customerId, "Delete failed: Customer not found");
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY, objectMapper.writeValueAsBytes(errorInfo), null);

        doThrow(ex).when(restTemplate).delete(eq(url));

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> customerServiceClient.deleteCustomerById(customerId));
        assertEquals("Delete failed: Customer not found", thrown.getMessage());
    }

    @Test
    void getCustomerByEmail_whenCustomerExists_shouldReturnCustomer() {
        String email = "test@example.com";
        CustomerResponseModel expectedCustomer = CustomerResponseModel.builder().emailAddress(email).build();
        String url = emailUrlBase + "/" + email;
        when(restTemplate.getForObject(eq(url), eq(CustomerResponseModel.class))).thenReturn(expectedCustomer);

        CustomerResponseModel actualCustomer = customerServiceClient.getCustomerByEmail(email);
        assertNotNull(actualCustomer);
        assertEquals(email, actualCustomer.getEmailAddress());
    }

    @Test
    void getCustomerByEmail_whenNotFound_shouldThrowNotFoundException() throws JsonProcessingException {
        String email = "notfound@example.com";
        String url = emailUrlBase + "/" + email;
        HttpErrorInfo errorInfo = new HttpErrorInfo(HttpStatus.NOT_FOUND, "/api/v1/customers/email/" + email, "Email not found");
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY, objectMapper.writeValueAsBytes(errorInfo), null);

        when(restTemplate.getForObject(eq(url), eq(CustomerResponseModel.class))).thenThrow(ex);

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> customerServiceClient.getCustomerByEmail(email));
        assertEquals("Email not found", thrown.getMessage());
    }

    @Test
    void deleteCustomerByEmail_shouldCallDelete() {
        String email = "test@example.com";
        String url = emailUrlBase + "/" + email;
        doNothing().when(restTemplate).delete(eq(url));
        customerServiceClient.deleteCustomerByEmail(email);
        verify(restTemplate, times(1)).delete(eq(url));
    }

    @Test
    void getCustomersByPhone_shouldReturnListOfCustomers() {
        String country = "CA";
        String[] phoneNumbers = {"123", "456"};
        String expectedUrl = byPhoneUrlBase + "?country=CA&phone=123&phone=456"; // Based on UriComponentsBuilder logic
        CustomerResponseModel cust1 = CustomerResponseModel.builder().customerId("cp1").build();
        CustomerResponseModel[] customersArray = {cust1};

        when(restTemplate.getForObject(eq(expectedUrl), eq(CustomerResponseModel[].class)))
                .thenReturn(customersArray);

        List<CustomerResponseModel> result = customerServiceClient.getCustomersByPhone(country, phoneNumbers);
        assertEquals(1, result.size());
        assertEquals("cp1", result.get(0).getCustomerId());
    }

    @Test
    void getCustomersByPhone_whenNoPhones_shouldReturnEmptyListFromClientLogic() {
        String country = "CA";
        // The UriComponentsBuilder might produce just "?country=CA"
        String expectedUrl = byPhoneUrlBase + "?country=CA";
        CustomerResponseModel[] customersArray = {}; // Or service might return empty for no specific phones

        when(restTemplate.getForObject(eq(expectedUrl), eq(CustomerResponseModel[].class)))
                .thenReturn(customersArray);

        List<CustomerResponseModel> result = customerServiceClient.getCustomersByPhone(country); // No phone numbers
        assertTrue(result.isEmpty());
    }

    @Test
    void getCustomersByPhone_whenServiceReturnsNotFound_shouldReturnEmptyList() throws JsonProcessingException {
        String country = "US";
        String[] phoneNumbers = {"999"};
        String expectedUrl = byPhoneUrlBase + "?country=US&phone=999";
        HttpErrorInfo errorInfo = new HttpErrorInfo(HttpStatus.NOT_FOUND, "/api/v1/customers/by-phone?country=US&phone=999", "No customers by phone");
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY, objectMapper.writeValueAsBytes(errorInfo), null);

        when(restTemplate.getForObject(eq(expectedUrl), eq(CustomerResponseModel[].class)))
                .thenThrow(ex);

        // The client's handleException for NotFoundException will be triggered,
        // but getCustomersByPhone itself catches this and returns List.of()
        List<CustomerResponseModel> result = customerServiceClient.getCustomersByPhone(country, phoneNumbers);
        assertNotNull(result);
        assertTrue(result.isEmpty()); // Client returns empty list on error
    }
}