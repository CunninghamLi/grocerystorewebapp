package com.example.customerservice.businesslayer;

import com.example.customerservice.datalayer.Address;
import com.example.customerservice.datalayer.Customer;
import com.example.customerservice.datalayer.CustomerRepository;
import com.example.customerservice.datamapperlayer.CustomerRequestMapper;
import com.example.customerservice.datamapperlayer.CustomerResponseMapper;
import com.example.customerservice.presentationlayer.CustomerRequestModel;
import com.example.customerservice.presentationlayer.CustomerResponseModel;
import com.example.customerservice.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplUnitTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerResponseMapper customerResponseMapper;

    @Mock
    private CustomerRequestMapper customerRequestMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private CustomerRequestModel requestModel;
    private CustomerResponseModel responseModel;
    private Customer customerEntity;

    @BeforeEach
    void setUp() {
        // build a sample request
        requestModel = CustomerRequestModel.builder()
                .firstName("Alice")
                .lastName("Wonderland")
                .emailAddress("alice@example.com")
                .streetAddress("1 Rabbit Hole")
                .city("Fiction")
                .province("Imagination")
                .country("Fantasy")
                .postalCode("00001")
                .build();

        // expected response
        responseModel = new CustomerResponseModel();
        responseModel.setCustomerId(42);
        responseModel.setFirstName("Alice");
        responseModel.setLastName("Wonderland");
        responseModel.setEmailAddress("alice@example.com");
        responseModel.setStreetAddress("1 Rabbit Hole");
        responseModel.setCity("Fiction");
        responseModel.setProvince("Imagination");
        responseModel.setCountry("Fantasy");
        responseModel.setPostalCode("00001");

        // entity that service should operate on
        Address addr = new Address(
                requestModel.getStreetAddress(),
                requestModel.getCity(),
                requestModel.getProvince(),
                requestModel.getCountry(),
                requestModel.getPostalCode()
        );
        customerEntity = new Customer(
                requestModel.getFirstName(),
                requestModel.getLastName(),
                requestModel.getEmailAddress(),
                addr
        );
        customerEntity.setId(42);
    }

    @Test
    void getCustomers_returnsMappedList() {
        when(customerRepository.findAll())
                .thenReturn(Collections.singletonList(customerEntity));
        when(customerResponseMapper
                .entityListToResponseModelList(anyList()))
                .thenReturn(Collections.singletonList(responseModel));

        var result = customerService.getCustomers();

        assertEquals(1, result.size());
        assertEquals(42, result.get(0).getCustomerId());
        verify(customerRepository).findAll();
        verify(customerResponseMapper)
                .entityListToResponseModelList(anyList());
    }

    @Test
    void getCustomerByCustomerId_existing_returnsMapped() {
        when(customerRepository.findById(42))
                .thenReturn(Optional.of(customerEntity));
        when(customerResponseMapper
                .entityToResponseModel(customerEntity))
                .thenReturn(responseModel);

        var result = customerService.getCustomerByCustomerId(42);

        assertNotNull(result);
        assertEquals(42, result.getCustomerId());
        verify(customerRepository).findById(42);
        verify(customerResponseMapper).entityToResponseModel(customerEntity);
    }

    @Test
    void getCustomerByCustomerId_notFound_throws() {
        when(customerRepository.findById(99))
                .thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> customerService.getCustomerByCustomerId(99)
        );
        assertTrue(ex.getMessage().contains("Unknown customerId: 99"));
        verify(customerRepository).findById(99);
    }

    @Test
    void addCustomer_savesAndReturnsMapped() {
        when(customerRequestMapper
                .requestModelToEntity(any(), any()))
                .thenReturn(customerEntity);
        when(customerRepository.save(customerEntity))
                .thenReturn(customerEntity);
        when(customerResponseMapper
                .entityToResponseModel(customerEntity))
                .thenReturn(responseModel);

        var result = customerService.addCustomer(requestModel);

        assertEquals(42, result.getCustomerId());
        verify(customerRequestMapper)
                .requestModelToEntity(eq(requestModel), any(Address.class));
        verify(customerRepository).save(customerEntity);
        verify(customerResponseMapper).entityToResponseModel(customerEntity);
    }

    @Test
    void updateCustomer_existing_savesAndReturnsMapped() {
        when(customerRepository.findById(42))
                .thenReturn(Optional.of(customerEntity));
        when(customerRequestMapper
                .requestModelToEntity(any(), any()))
                .thenReturn(customerEntity);
        when(customerRepository.save(customerEntity))
                .thenReturn(customerEntity);
        when(customerResponseMapper
                .entityToResponseModel(customerEntity))
                .thenReturn(responseModel);

        var result = customerService.updateCustomer(requestModel, 42);

        assertEquals(42, result.getCustomerId());
        verify(customerRepository).findById(42);
        verify(customerRequestMapper)
                .requestModelToEntity(eq(requestModel), any(Address.class));
        verify(customerRepository).save(customerEntity);
    }

    @Test
    void updateCustomer_notFound_throws() {
        when(customerRepository.findById(99))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> customerService.updateCustomer(requestModel, 99)
        );
        verify(customerRepository).findById(99);
    }

    @Test
    void removeCustomer_existing_deletes() {
        when(customerRepository.findById(42))
                .thenReturn(Optional.of(customerEntity));

        assertDoesNotThrow(() -> customerService.removeCustomer(42));
        verify(customerRepository).delete(customerEntity);
    }

    @Test
    void removeCustomer_notFound_throws() {
        when(customerRepository.findById(99))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> customerService.removeCustomer(99)
        );
    }

    @Test
    void getCustomerByEmail_existing_returnsMapped() {
        when(customerRepository.findByEmailAddress("alice@example.com"))
                .thenReturn(customerEntity);
        when(customerResponseMapper
                .entityToResponseModel(customerEntity))
                .thenReturn(responseModel);

        var result = customerService.getCustomerByEmail("alice@example.com");
        assertEquals("alice@example.com", result.getEmailAddress());
        verify(customerRepository).findByEmailAddress("alice@example.com");
    }

    @Test
    void getCustomerByEmail_notFound_throws() {
        when(customerRepository.findByEmailAddress("no@example.com"))
                .thenReturn(null);

        assertThrows(
                NotFoundException.class,
                () -> customerService.getCustomerByEmail("no@example.com")
        );
    }

    @Test
    void removeCustomerByEmail_existing_deletes() {
        when(customerRepository.findByEmailAddress("alice@example.com"))
                .thenReturn(customerEntity);
        doNothing().when(customerRepository)
                .deleteByEmailAddress("alice@example.com");

        assertDoesNotThrow(
                () -> customerService.removeCustomerByEmail("alice@example.com")
        );
        verify(customerRepository)
                .deleteByEmailAddress("alice@example.com");
    }

    @Test
    void removeCustomerByEmail_notFound_throws() {
        when(customerRepository.findByEmailAddress("no@example.com"))
                .thenReturn(null);

        assertThrows(
                NotFoundException.class,
                () -> customerService.removeCustomerByEmail("no@example.com")
        );
    }
}

//100% Jacoco