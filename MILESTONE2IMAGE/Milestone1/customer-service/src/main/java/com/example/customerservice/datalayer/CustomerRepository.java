package com.example.customerservice.datalayer;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Customer findByEmailAddress(String emailAddress);

    @Transactional
    void deleteByEmailAddress(String emailAddress);
}
