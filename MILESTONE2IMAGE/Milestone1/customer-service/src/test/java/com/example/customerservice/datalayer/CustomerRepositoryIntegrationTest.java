package com.example.customerservice.datalayer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CustomerRepositoryIntegrationTest {

    @Autowired
    private CustomerRepository customerRepository;

    private Customer john;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
        Address address = new Address(
                "123 Main St", "Anytown", "Anystate", "Country", "12345"
        );
        john = new Customer("John", "Doe", "john.doe@example.com", address);
        // save via JPA
        john = customerRepository.save(john);
    }

    @Test
    void whenFindAll_thenSingleCustomerReturned() {
        List<Customer> all = customerRepository.findAll();
        assertEquals(1, all.size(), "there should be exactly one saved customer");
        assertEquals("john.doe@example.com", all.get(0).getEmailAddress());
    }

    @Test
    void whenFindById_existingId_thenReturnOptionalWithCustomer() {
        Optional<Customer> maybe = customerRepository.findById(john.getId());
        assertTrue(maybe.isPresent(), "should find by existing ID");
        assertEquals("John", maybe.get().getFirstName());
    }

    @Test
    void whenFindById_nonExistingId_thenReturnEmptyOptional() {
        Optional<Customer> maybe = customerRepository.findById(9999);
        assertTrue(maybe.isEmpty(), "non-existent ID should yield empty Optional");
    }

    @Test
    void whenSave_newCustomer_thenPersisted() {
        Address addr2 = new Address("456 Elm St", "Othertown", "State", "Country", "54321");
        Customer jane = new Customer("Jane", "Smith", "jane.smith@example.com", addr2);
        Customer saved = customerRepository.save(jane);

        assertNotNull(saved.getId(), "saved customer should have generated ID");
        assertEquals("jane.smith@example.com", customerRepository.findByEmailAddress("jane.smith@example.com").getEmailAddress());
    }

    @Test
    void whenDeleteEntity_existing_thenRemoved() {
        // delete the one we saved
        customerRepository.delete(john);
        List<Customer> all = customerRepository.findAll();
        assertTrue(all.isEmpty(), "repository should be empty after delete(entity)");
    }

    @Test
    void whenFindByEmail_existingEmail_thenReturnCustomer() {
        Customer found = customerRepository.findByEmailAddress("john.doe@example.com");
        assertNotNull(found);
        assertEquals("john.doe@example.com", found.getEmailAddress());
    }

    @Test
    void whenFindByEmail_nonExistingEmail_thenReturnNull() {
        assertNull(customerRepository.findByEmailAddress("nobody@example.com"));
    }

    @Test
    void whenDeleteByEmail_existingEmail_thenDeletesCustomer() {
        customerRepository.deleteByEmailAddress("john.doe@example.com");
        assertNull(customerRepository.findByEmailAddress("john.doe@example.com"));
    }

    @Test
    void whenDeleteByEmail_nonExistingEmail_thenNoException() {
        assertDoesNotThrow(() ->
                customerRepository.deleteByEmailAddress("noone@example.com")
        );
    }
}
