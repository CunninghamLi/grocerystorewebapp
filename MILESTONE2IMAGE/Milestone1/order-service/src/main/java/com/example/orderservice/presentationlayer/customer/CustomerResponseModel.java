package com.example.orderservice.presentationlayer.customer;

import com.example.orderservice.datalayer.PhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponseModel extends RepresentationModel<CustomerResponseModel> {

    String customerId;
    String firstName;
    String lastName;
    String emailAddress;
    String streetAddress;
    String city;
    String province;
    String country;
    String postalCode;
    List<PhoneNumber> phoneNumber;

}
