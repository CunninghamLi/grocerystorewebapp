package com.example.customerservice.datamapperlayer;

import com.example.customerservice.datalayer.Address;
import com.example.customerservice.datalayer.Customer;
import com.example.customerservice.presentationlayer.CustomerRequestModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.*;

class CustomerRequestMapperTest {

    private static CustomerRequestMapper REQUEST_MAPPER;

    @BeforeAll
    static void init() {
        REQUEST_MAPPER = Mappers.getMapper(CustomerRequestMapper.class);
    }

    @Test
    void requestModelToEntity_shouldMapAllFields() {
        var req = CustomerRequestModel.builder()
                .firstName("Jane")
                .lastName("Doe")
                .emailAddress("jane@doe.com")
                .streetAddress("1 A St")
                .city("City")
                .province("Prov")
                .country("Country")
                .postalCode("99999")
                .build();

        var addr = new Address(
                req.getStreetAddress(),
                req.getCity(),
                req.getProvince(),
                req.getCountry(),
                req.getPostalCode()
        );

        var entity = REQUEST_MAPPER.requestModelToEntity(req, addr);
        assertThat(entity).isNotNull();
        assertThat(entity.getFirstName()).isEqualTo("Jane");
        assertThat(entity.getEmailAddress()).isEqualTo("jane@doe.com");
        assertThat(entity.getAddress()).isEqualTo(addr);
    }





    @Test
    void nullModelToEntity_shouldReturnNull() {
        assertThat(REQUEST_MAPPER.requestModelToEntity(null, null)).isNull();
    }
}
