package com.example.productservice.datamapperlayer;

import com.example.productservice.datalayer.Product;
import com.example.productservice.presentationlayer.ProductRequestModel;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class ProductRequestMapperTest {

    private final ProductRequestMapper mapper =
            Mappers.getMapper(ProductRequestMapper.class);

    @Test
    void mapsRequestToEntity() {
        var req = new ProductRequestModel();
        req.setName("N");
        req.setDescription("D");
        req.setPrice(3.14);
        req.setStockQuantity(7);

        Product ent = mapper.requestModelToEntity(req);

        assertAll(
                () -> assertEquals("N", ent.getName()),
                () -> assertEquals("D", ent.getDescription()),
                () -> assertEquals(3.14, ent.getPrice()),
                () -> assertEquals(7, ent.getStockQuantity())
        );
    }

    @Test
    void updateEntityFromRequestModel() {
        var existing = new Product();
        existing.setId(42L);
        existing.setName("Old");
        existing.setDescription("OldD");
        existing.setPrice(1.0);
        existing.setStockQuantity(1);

        var req = new ProductRequestModel();
        req.setName("New");
        req.setDescription("NewD");
        req.setPrice(9.99);
        req.setStockQuantity(99);

        mapper.updateEntityFromRequestModel(req, existing);

        assertAll(
                () -> assertEquals(42L, existing.getId()),
                () -> assertEquals("New", existing.getName()),
                () -> assertEquals("NewD", existing.getDescription()),
                () -> assertEquals(9.99, existing.getPrice()),
                () -> assertEquals(99, existing.getStockQuantity())
        );
    }

    @Test
    void nullRequestGivesNullEntity() {
        assertNull(mapper.requestModelToEntity(null));
    }
}
