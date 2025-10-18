package com.example.apigateway.presentationlayer.customer;

import com.example.apigateway.domainclient.CustomerServiceClient;
import com.example.apigateway.utils.GlobalControllerExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.AdditionalMatchers.aryEq;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CustomerControllerUnitTest {
    @Mock
    private CustomerServiceClient client;

    @InjectMocks
    private CustomerController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalControllerExceptionHandler())
                .build();
    }

    @Test
    void getAll_ShouldReturnList() throws Exception {
        CustomerResponseModel model = CustomerResponseModel.builder()
                .customerId("42").firstName("John").lastName("Doe").build();
        when(client.getAllCustomers()).thenReturn(List.of(model));

        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value("42"));

        verify(client).getAllCustomers();
    }

    @Test
    void getById_WhenFound_ShouldReturn() throws Exception {
        CustomerResponseModel model = CustomerResponseModel.builder()
                .customerId("42").build();
        when(client.getCustomerById("42")).thenReturn(model);

        mockMvc.perform(get("/api/v1/customers/42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value("42"));
    }

    @Test
    void getById_WhenNotFound_ShouldReturn404() throws Exception {
        when(client.getCustomerById("99")).thenThrow(new com.example.apigateway.utils.exceptions.NotFoundException("Not found"));

        mockMvc.perform(get("/api/v1/customers/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_ShouldReturnCreated() throws Exception {
        CustomerRequestModel req = CustomerRequestModel.builder()
                .firstName("Jane").lastName("Doe").build();
        CustomerResponseModel res = CustomerResponseModel.builder().customerId("7").build();
        when(client.addCustomer(any())).thenReturn(res);

        String json = "{\"firstName\":\"Jane\",\"lastName\":\"Doe\"}";
        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value("7"));
    }

    @Test
    void update_ShouldReturnUpdated() throws Exception {
        CustomerRequestModel req = CustomerRequestModel.builder().firstName("Jim").build();
        CustomerResponseModel res = CustomerResponseModel.builder().customerId("42").firstName("Jim").build();
        when(client.updateCustomer(eq("42"), any())).thenReturn(res);

        String json = "{\"firstName\":\"Jim\"}";
        mockMvc.perform(put("/api/v1/customers/42")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jim"));
    }

    @Test
    void deleteById_ShouldReturnNoContent() throws Exception {
        doNothing().when(client).deleteCustomerById("42");
        mockMvc.perform(delete("/api/v1/customers/42"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getByEmail_ShouldReturn() throws Exception {
        CustomerResponseModel model = CustomerResponseModel.builder().emailAddress("a@b.com").build();
        when(client.getCustomerByEmail("a@b.com")).thenReturn(model);

        mockMvc.perform(get("/api/v1/customers/email/a@b.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emailAddress").value("a@b.com"));
    }

    @Test
    void deleteByEmail_ShouldReturnNoContent() throws Exception {
        doNothing().when(client).deleteCustomerByEmail("a@b.com");

        mockMvc.perform(delete("/api/v1/customers/email/a@b.com"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getByPhone_ShouldReturnList() throws Exception {
        CustomerResponseModel model = CustomerResponseModel.builder().customerId("5").build();
        when(client.getCustomersByPhone(eq("CA"), eq(new String[]{"123", "456"})))
                .thenReturn(List.of(model));

        mockMvc.perform(get("/api/v1/customers/by-phone?country=CA&phone=123&phone=456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value("5"));
    }// Inside CustomerControllerUnitTest class

    @Test
    void getByPhone_whenNoPhoneNumbersProvided_shouldStillCallClient() throws Exception {
        CustomerResponseModel model = CustomerResponseModel.builder().customerId("5").build();
        // Assuming the client handles empty phone numbers array gracefully or service defines behavior
        when(client.getCustomersByPhone(eq("CA"), aryEq(new String[]{})))
                .thenReturn(List.of(model));

        mockMvc.perform(get("/api/v1/customers/by-phone?country=CA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value("5"));

        verify(client).getCustomersByPhone(eq("CA"), aryEq(new String[]{}));
    }

    @Test
    void getByPhone_whenClientReturnsEmptyList_shouldReturnOkWithEmptyList() throws Exception {
        when(client.getCustomersByPhone(eq("CA"), aryEq(new String[]{"000", "111"})))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/customers/by-phone?country=CA&phone=000&phone=111"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
        verify(client).getCustomersByPhone(eq("CA"), aryEq(new String[]{"000", "111"}));
    }

    @Test
    void getByEmail_whenNotFound_shouldReturn404() throws Exception {
        when(client.getCustomerByEmail("nonexistent@example.com"))
                .thenThrow(new com.example.apigateway.utils.exceptions.NotFoundException("Customer with email nonexistent@example.com not found"));

        mockMvc.perform(get("/api/v1/customers/email/nonexistent@example.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer with email nonexistent@example.com not found"));
        verify(client).getCustomerByEmail("nonexistent@example.com");
    }

    @Test
    void create_withInvalidInputExceptionFromClient_shouldReturnUnprocessableEntity() throws Exception {
        CustomerRequestModel req = CustomerRequestModel.builder().firstName("").build(); // Invalid
        when(client.addCustomer(any(CustomerRequestModel.class)))
                .thenThrow(new com.example.apigateway.utils.exceptions.InvalidInputException("First name cannot be empty"));

        String json = "{\"firstName\":\"\"}"; // Simulate invalid request
        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("First name cannot be empty"));
        verify(client).addCustomer(any(CustomerRequestModel.class));
    }

    @Test
    void update_withNotFoundExceptionFromClient_shouldReturnNotFound() throws Exception {
        CustomerRequestModel req = CustomerRequestModel.builder().firstName("Jim").build();
        when(client.updateCustomer(eq("nonExistentId"), any(CustomerRequestModel.class)))
                .thenThrow(new com.example.apigateway.utils.exceptions.NotFoundException("Customer with id nonExistentId not found for update"));

        String json = "{\"firstName\":\"Jim\"}";
        mockMvc.perform(put("/api/v1/customers/nonExistentId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer with id nonExistentId not found for update"));
        verify(client).updateCustomer(eq("nonExistentId"), any(CustomerRequestModel.class));
    }

    @Test
    void deleteById_whenNotFound_shouldReturnNotFound() throws Exception {
        doThrow(new com.example.apigateway.utils.exceptions.NotFoundException("Customer with id nonExistentId not found for deletion"))
                .when(client).deleteCustomerById("nonExistentId");

        mockMvc.perform(delete("/api/v1/customers/nonExistentId"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer with id nonExistentId not found for deletion"));
        verify(client).deleteCustomerById("nonExistentId");
    }

    @Test
    void deleteByEmail_whenNotFound_shouldReturnNotFound() throws Exception {
        doThrow(new com.example.apigateway.utils.exceptions.NotFoundException("Customer with email nonexistent@example.com not found for deletion"))
                .when(client).deleteCustomerByEmail("nonexistent@example.com");

        mockMvc.perform(delete("/api/v1/customers/email/nonexistent@example.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer with email nonexistent@example.com not found for deletion"));
        verify(client).deleteCustomerByEmail("nonexistent@example.com");
    }

}
