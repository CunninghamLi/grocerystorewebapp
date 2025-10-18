package com.example.productservice.businesslayer;

import com.example.productservice.datalayer.Product;
import com.example.productservice.datalayer.ProductRepository;
import com.example.productservice.datamapperlayer.ProductRequestMapper;
import com.example.productservice.datamapperlayer.ProductResponseMapper;
import com.example.productservice.presentationlayer.ProductRequestModel;
import com.example.productservice.presentationlayer.ProductResponseModel;
import com.example.productservice.utils.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductResponseMapper productResponseMapper;
    private final ProductRequestMapper productRequestMapper;

    public ProductServiceImpl(ProductRepository productRepository,
                              ProductResponseMapper productResponseMapper,
                              ProductRequestMapper productRequestMapper) {
        this.productRepository = productRepository;
        this.productResponseMapper = productResponseMapper;
        this.productRequestMapper = productRequestMapper;
    }

    @Override
    public List<ProductResponseModel> getProducts() {
        return productRepository.findAll()
                .stream()
                .map(productResponseMapper::entityToProductResponseModel)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponseModel getProductByProductId(String productId) {
        Product product = productRepository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Product not found: " + productId));
        return productResponseMapper.entityToProductResponseModel(product);
    }

    @Override
    public ProductResponseModel addProduct(ProductRequestModel productRequestModel) {
        Product product = productRequestMapper.requestModelToEntity(productRequestModel);
        product.setProductId("prod-" + System.currentTimeMillis());  // temporary unique ID
        Product saved = productRepository.save(product);
        return productResponseMapper.entityToProductResponseModel(saved);
    }

    @Override
    public ProductResponseModel updateProduct(ProductRequestModel updatedProduct, String productId) {
        Product existing = productRepository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Product not found: " + productId));
        productRequestMapper.updateEntityFromRequestModel(updatedProduct, existing);
        Product saved = productRepository.save(existing);
        return productResponseMapper.entityToProductResponseModel(saved);
    }

    @Override
    public void removeProduct(String productId) {
        Product product = productRepository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Product not found: " + productId));
        productRepository.delete(product);
    }
}
