package com.example.apigateway.presentationlayer.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data; // Consider using @Data for @Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor
import lombok.NoArgsConstructor; // ADD THIS

@Data // Or @Value + @NoArgsConstructor(force = true)
@Builder
@NoArgsConstructor // Added
@AllArgsConstructor
public class CustomerRequestModel {
    String firstName;
    String lastName;
    String emailAddress;
    String streetAddress;
    String city;
    String province;
    String country;
    String postalCode;
}