package com.example.customerservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CustomerServiceApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void mainStartsWithoutException() {
        // call the generated main method
        CustomerServiceApplication.main(new String[] {});
    }
}
