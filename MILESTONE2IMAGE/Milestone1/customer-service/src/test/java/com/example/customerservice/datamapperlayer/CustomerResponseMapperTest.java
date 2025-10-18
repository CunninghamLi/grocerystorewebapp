package com.example.customerservice.datamapperlayer;

import com.example.customerservice.datalayer.Address;
import com.example.customerservice.datalayer.Customer;
import com.example.customerservice.presentationlayer.CustomerResponseModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class CustomerResponseMapperTest {

    private static CustomerResponseMapper RESPONSE_MAPPER;

    @BeforeAll
    static void init() {
        RESPONSE_MAPPER = Mappers.getMapper(CustomerResponseMapper.class);
    }

    private Customer sampleCustomer() {
        var c = new Customer(
                "A","B","a@b.com",
                new Address("S","C","P","Country","12345")
        );
        c.setId(55);
        return c;
    }

    @Test
    void entityToResponseModel_shouldMapAllFields() {
        var c = sampleCustomer();
        var resp = RESPONSE_MAPPER.entityToResponseModel(c);

        assertThat(resp).isNotNull();
        assertThat(resp.getCustomerId()).isEqualTo(55);
        assertThat(resp.getEmailAddress()).isEqualTo("a@b.com");
        assertThat(resp.getPostalCode()).isEqualTo("12345");
    }

    @Test
    void entityListToResponseModelList_shouldMapList() {
        var list = RESPONSE_MAPPER.entityListToResponseModelList(
                List.of(sampleCustomer(), sampleCustomer())
        );
        assertThat(list).hasSize(2)
                .allSatisfy(r -> assertThat(r.getFirstName()).isEqualTo("A"));
    }

    @Test
    void nullEntityMapping_shouldReturnNullOrEmpty() {
        assertThat(RESPONSE_MAPPER.entityToResponseModel(null)).isNull();
        assertThat(RESPONSE_MAPPER.entityListToResponseModelList(null)).isNull();
        // if you really want to treat empty list specially:
        assertThat(RESPONSE_MAPPER.entityListToResponseModelList(List.of())).isEmpty();
    }

}
