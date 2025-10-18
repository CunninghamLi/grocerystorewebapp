package com.example.apigateway.presentationlayer.customer;

import com.example.apigateway.ApiGatewayApplication;
import com.example.apigateway.domainclient.CustomerServiceClient;
import com.example.apigateway.utils.GlobalControllerExceptionHandler;
import com.example.apigateway.utils.exceptions.InvalidInputException;
import com.example.apigateway.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ApiGatewayApplication.class)
@AutoConfigureMockMvc
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerServiceClient client;

    @Test
    void getAll_ShouldReturnList() throws Exception {
        CustomerResponseModel model = CustomerResponseModel.builder().customerId("42").build();
        when(client.getAllCustomers()).thenReturn(List.of(model));

        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value("42"));
    }

    @Test
    void getById_NotFound_ShouldReturn404() throws Exception {
        when(client.getCustomerById("99")).thenThrow(new NotFoundException("Not found"));

        mockMvc.perform(get("/api/v1/customers/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void post_ShouldReturnCreated() throws Exception {
        CustomerResponseModel res = CustomerResponseModel.builder().customerId("7").build();
        when(client.addCustomer(any())).thenReturn(res);

        String json = "{\"firstName\":\"Anna\",\"lastName\":\"Smith\"}";
        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value("7"));
    }

    @Test
    void put_InvalidInput_ShouldReturn422() throws Exception {
        when(client.updateCustomer(eq("42"), any()))
                .thenThrow(new InvalidInputException("Bad data"));

        String json = "{\"firstName\":\"\"}";
        mockMvc.perform(put("/api/v1/customers/42")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deleteByEmail_ShouldReturnNoContent() throws Exception {
        doNothing().when(client).deleteCustomerByEmail("a@b.com");

        mockMvc.perform(delete("/api/v1/customers/email/a@b.com"))
                .andExpect(status().isNoContent());
    }
}
