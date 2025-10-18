// src/main/java/com/example/apigateway/presentationlayer/customer/CustomerResponseModel.java
package com.example.apigateway.presentationlayer.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CustomerResponseModel extends RepresentationModel<CustomerResponseModel> {
    private String customerId;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String streetAddress;
    private String city;
    private String province;
    private String country;
    private String postalCode;
    private List<PhoneNumber> phoneNumber;
}
