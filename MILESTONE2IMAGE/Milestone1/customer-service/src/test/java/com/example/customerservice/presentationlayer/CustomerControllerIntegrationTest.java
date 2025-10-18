package com.example.customerservice.presentationlayer;

import com.example.customerservice.datalayer.Address;
import com.example.customerservice.datalayer.Customer;
import com.example.customerservice.datalayer.CustomerRepository;
import com.example.customerservice.presentationlayer.CustomerRequestModel;
import com.example.customerservice.presentationlayer.CustomerResponseModel;
import com.example.customerservice.utils.HttpErrorInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class CustomerControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void setupDb() {
        customerRepository.deleteAll();
    }

    @Test
    void getCustomerById_whenExists_returns200AndBody() {
        Customer saved = customerRepository.save(
                new Customer(
                        "John",
                        "Doe",
                        "john.doe@example.com",
                        new Address("123 Main St", "Anytown", "Anystate", "Country", "12345")
                )
        );

        webTestClient.get()
                .uri("/api/v1/customers/{id}", saved.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerResponseModel.class)
                .value(model -> {
                    assertThat(model.getCustomerId()).isEqualTo(saved.getId());
                    assertThat(model.getFirstName()).isEqualTo("John");
                });
    }

    @Test
    void getCustomerById_whenNotExists_returns404ErrorInfo() {
        webTestClient.get()
                .uri("/api/v1/customers/{id}", 999)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(HttpErrorInfo.class);
    }

    @Test
    void deleteCustomer_whenExists_returns204() {
        Customer saved = customerRepository.save(
                new Customer(
                        "John",
                        "Doe",
                        "john.doe@example.com",
                        new Address("123 Main St", "Anytown", "Anystate", "Country", "12345")
                )
        );

        webTestClient.delete()
                .uri("/api/v1/customers/{id}", saved.getId())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void deleteCustomer_whenNotExists_returns404() {
        webTestClient.delete()
                .uri("/api/v1/customers/{id}", 999)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void addCustomer_returns201AndBody() {
        CustomerRequestModel request = CustomerRequestModel.builder()
                .firstName("Alice")
                .lastName("Smith")
                .emailAddress("alice.smith@example.com")
                .streetAddress("100 Market St")
                .city("Townsville")
                .province("State")
                .country("Country")
                .postalCode("11111")
                .build();

        webTestClient.post()
                .uri("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CustomerResponseModel.class)
                .value(model -> {
                    assertThat(model.getCustomerId()).isNotNull();
                    assertThat(model.getFirstName()).isEqualTo("Alice");
                });
    }
    @Test
    void getAllCustomers_returnsList() {
        // seed two
        customerRepository.save(new Customer("A","A","a@a.com",
                new Address("S","C","P","C","00000")));
        customerRepository.save(new Customer("B","B","b@b.com",
                new Address("S","C","P","C","11111")));

        webTestClient.get().uri("/api/v1/customers")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerResponseModel.class)
                .hasSize(2);
    }

    @Test
    void updateCustomer_whenExists_returns200AndUpdated() {
        Customer saved = customerRepository.save(
                new Customer("X","Y","xy@ex.com",
                        new Address("S","C","P","C","22222"))
        );

        CustomerRequestModel upd = CustomerRequestModel.builder()
                .firstName("X2").lastName("Y2")
                .emailAddress("xy2@ex.com")
                .streetAddress("New")
                .city("City")
                .province("Prov")
                .country("Country")
                .postalCode("33333")
                .build();

        webTestClient.put()
                .uri("/api/v1/customers/{id}", saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(upd)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerResponseModel.class)
                .value(r -> {
                    assertThat(r.getFirstName()).isEqualTo("X2");
                    assertThat(r.getEmailAddress()).isEqualTo("xy2@ex.com");
                });
    }

    @Test
    void updateCustomer_whenNotExists_returns404() {
        CustomerRequestModel upd = CustomerRequestModel.builder()
                .firstName("X2").lastName("Y2")
                .emailAddress("xy2@ex.com")
                .streetAddress("New")
                .city("City")
                .province("Prov")
                .country("Country")
                .postalCode("33333")
                .build();

        webTestClient.put()
                .uri("/api/v1/customers/{id}", 9999)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(upd)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getAndDeleteByEmail_paths() {
        // seed one
        Customer c = new Customer("E","Mail","em@em.com",
                new Address("S","C","P","C","44444"));
        customerRepository.save(c);

        // GET by email
        webTestClient.get()
                .uri("/api/v1/customers/email/{email}", "em@em.com")
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerResponseModel.class)
                .value(r -> assertThat(r.getEmailAddress()).isEqualTo("em@em.com"));

        // DELETE by email
        webTestClient.delete()
                .uri("/api/v1/customers/email/{email}", "em@em.com")
                .exchange()
                .expectStatus().isNoContent();

        // ensure gone
        webTestClient.get()
                .uri("/api/v1/customers/email/{email}", "em@em.com")
                .exchange()
                .expectStatus().isNotFound();
    }

}
