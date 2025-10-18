package com.example.productservice.datalayer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class ProductRepositoryIntegrationTest {

    @Autowired
    private ProductRepository repo;

    private Product saved;

    @BeforeEach
    void setUp() {
        repo.deleteAll();

        saved = Product.builder()
                .productId("X1")
                .name("Test Name")
                .description("Test Desc")
                .price(9.99)
                .stockQuantity(100)
                .build();

        saved = repo.save(saved);
    }

    @Test
    @DisplayName("findById – existing ID returns product")
    void whenFindById_existingId_thenReturnProduct() {
        Optional<Product> found = repo.findById(saved.getId());
        assertThat(found)
                .isPresent()
                .get()
                .extracting(Product::getProductId)
                .isEqualTo("X1");
    }

    @Test
    @DisplayName("findById – non‐existent ID returns empty")
    void whenFindById_nonExistingId_thenEmpty() {
        Long bogusId = saved.getId() + 999;
        Optional<Product> found = repo.findById(bogusId);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("findByProductId – existing productId returns product")
    void whenFindByProductId_existing_thenReturnProduct() {
        Optional<Product> found = repo.findByProductId("X1");
        assertThat(found)
                .isPresent()
                .get()
                .extracting(Product::getName)
                .isEqualTo("Test Name");
    }

    @Test
    @DisplayName("findByProductId – non‐existent productId returns empty")
    void whenFindByProductId_nonExisting_thenEmpty() {
        Optional<Product> found = repo.findByProductId("NO_SUCH");
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("findAll – after deleting all returns empty list")
    void whenFindAll_afterDeleteAll_thenEmptyList() {
        repo.deleteAll();
        List<Product> all = repo.findAll();
        assertThat(all).isEmpty();
    }

    @Test
    @DisplayName("delete – deleting existing removes it")
    void whenDelete_existing_thenRemoved() {
        repo.delete(saved);
        assertThat(repo.findById(saved.getId())).isEmpty();
    }

    @Test
    @DisplayName("deleteById – deleting by ID removes it")
    void whenDeleteById_existing_thenRemoved() {
        repo.deleteById(saved.getId());
        assertThat(repo.findById(saved.getId())).isEmpty();
    }
}
