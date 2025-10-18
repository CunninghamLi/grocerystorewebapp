package com.example.productservice.datamapperlayer;

import com.example.productservice.datalayer.Product;
import com.example.productservice.presentationlayer.ProductRequestModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.*;

class ProductRequestMapperUnitTest {

    private ProductRequestMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(ProductRequestMapper.class);
    }

    @Test
    void requestModelToEntity_createsEntityWithAllFields() {
        // given
        ProductRequestModel req = new ProductRequestModel();
        req.setName("Gizmo");
        req.setDescription("A useful gizmo");
        req.setPrice(19.99);
        req.setStockQuantity(42);

        // when
        Product entity = mapper.requestModelToEntity(req);

        // then
        // id is ignored by mapper
        assertThat(entity.getId()).isNull();
        assertThat(entity.getName()).isEqualTo("Gizmo");
        assertThat(entity.getDescription()).isEqualTo("A useful gizmo");
        assertThat(entity.getPrice()).isEqualTo(19.99);
        assertThat(entity.getStockQuantity()).isEqualTo(42);
    }

    @Test
    void updateEntityFromRequestModel_overwritesOnlyDataFields() {
        // existing entity
        Product existing = Product.builder()
                .id(100L)
                .productId("prod-123")
                .name("OldName")
                .description("OldDesc")
                .price(5.00)
                .stockQuantity(10)
                .build();

        // new request
        ProductRequestModel req = new ProductRequestModel();
        req.setName("NewName");
        req.setDescription("NewDesc");
        req.setPrice(9.95);
        req.setStockQuantity(77);

        // when
        mapper.updateEntityFromRequestModel(req, existing);

        // then
        // identity fields unchanged
        assertThat(existing.getId()).isEqualTo(100L);
        assertThat(existing.getProductId()).isEqualTo("prod-123");
        // data fields updated
        assertThat(existing.getName()).isEqualTo("NewName");
        assertThat(existing.getDescription()).isEqualTo("NewDesc");
        assertThat(existing.getPrice()).isEqualTo(9.95);
        assertThat(existing.getStockQuantity()).isEqualTo(77);
    }
}
