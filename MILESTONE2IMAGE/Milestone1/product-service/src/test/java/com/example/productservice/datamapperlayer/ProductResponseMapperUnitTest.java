package com.example.productservice.datamapperlayer;

import com.example.productservice.datalayer.Product;
import com.example.productservice.presentationlayer.ProductResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ProductResponseMapperUnitTest {

    private ProductResponseMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(ProductResponseMapper.class);
    }

    @Test
    void entityToProductResponseModel_mapsAllFields() {
        // given
        Product entity = Product.builder()
                .id(55L)
                .productId("prod-555")
                .name("Widget")
                .description("A fine widget")
                .price(3.50)
                .stockQuantity(5)
                .build();

        // when
        ProductResponseModel out = mapper.entityToProductResponseModel(entity);

        // then
        assertThat(out.getId()).isEqualTo(55L);
        assertThat(out.getProductId()).isEqualTo("prod-555");
        assertThat(out.getName()).isEqualTo("Widget");
        assertThat(out.getDescription()).isEqualTo("A fine widget");
        assertThat(out.getPrice()).isEqualTo(3.50);
        assertThat(out.getStockQuantity()).isEqualTo(5);
    }

    @Test
    void entityListToProductResponseModelList_convertsAll() {
        // given
        Product p1 = Product.builder()
                .id(1L).productId("p1").name("A").description("DescA").price(1.0).stockQuantity(1).build();
        Product p2 = Product.builder()
                .id(2L).productId("p2").name("B").description("DescB").price(2.0).stockQuantity(2).build();

        // when
        List<ProductResponseModel> list = mapper.entityListToProductResponseModelList(List.of(p1, p2));

        // then
        assertThat(list).hasSize(2);
        assertThat(list.get(0).getProductId()).isEqualTo("p1");
        assertThat(list.get(1).getProductId()).isEqualTo("p2");
        // spot‑check one more field
        assertThat(list.get(1).getName()).isEqualTo("B");
    }
}
