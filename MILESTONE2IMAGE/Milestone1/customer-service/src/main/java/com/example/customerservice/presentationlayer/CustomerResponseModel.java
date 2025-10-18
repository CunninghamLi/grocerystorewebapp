package com.example.customerservice.presentationlayer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponseModel extends RepresentationModel<CustomerResponseModel> {

    Integer customerId;
    String firstName;
    String lastName;
    String emailAddress;
    String streetAddress;
    String city;
    String province;
    String country;
    String postalCode;
}
