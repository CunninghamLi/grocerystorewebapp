package com.example.paymentsservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PaymentsServiceApplicationTests {

    /**
     * Simply loading the Spring context and invoking the main method
     * allows us to cover the auto-configuration entry point.
     */
    @Test
    void contextLoads() {
        // just ensure the context starts without errors
    }

    @Test
    void mainStartsWithoutException() {
        // call the generated main method
        PaymentsServiceApplication.main(new String[] {});
    }
}
