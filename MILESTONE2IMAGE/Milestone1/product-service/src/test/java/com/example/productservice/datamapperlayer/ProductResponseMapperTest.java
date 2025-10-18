package com.example.productservice.datamapperlayer;

import com.example.productservice.datalayer.Product;
import com.example.productservice.presentationlayer.ProductResponseModel;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class ProductResponseMapperTest {

    private final ProductResponseMapper mapper =
            Mappers.getMapper(ProductResponseMapper.class);

    @Test
    void mapsEntityToResponse() {
        var ent = new Product();
        ent.setId(5L);
        ent.setProductId("P5");
        ent.setName("NM");
        ent.setDescription("DS");
        ent.setPrice(7.77);
        ent.setStockQuantity(3);

        ProductResponseModel res = mapper.entityToProductResponseModel(ent);

        assertAll(
                () -> assertEquals(5L, res.getId()),
                () -> assertEquals("P5", res.getProductId()),
                () -> assertEquals("NM", res.getName()),
                () -> assertEquals("DS", res.getDescription()),
                () -> assertEquals(7.77, res.getPrice()),
                () -> assertEquals(3, res.getStockQuantity())
        );
    }

    @Test
    void nullEntityGivesNullResponse() {
        assertNull(mapper.entityToProductResponseModel(null));
    }
}
