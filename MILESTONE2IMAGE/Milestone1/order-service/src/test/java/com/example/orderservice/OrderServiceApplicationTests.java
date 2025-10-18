package com.example.orderservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OrderServiceApplicationTests {

    /**
     * Simply loading the Spring context picks up all
     * auto-configured beans (including embedded MongoDB and your clients).
     */
    @Test
    void contextLoads() {
        // if the context fails, this test will fail
    }

    @Test
    void mainStartsWithoutException() {
        // exercise your main() entrypoint for free coverage
        OrderServiceApplication.main(new String[]{});
    }
}
