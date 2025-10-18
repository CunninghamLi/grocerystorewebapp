package com.example.apigateway.presentationlayer.product;

import com.example.apigateway.domainclient.ProductServiceClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductServiceClient productServiceClient;

    public ProductController(ProductServiceClient productServiceClient) {
        this.productServiceClient = productServiceClient;
    }

    @GetMapping
    public List<ProductResponseModel> getAllProducts() {
        return productServiceClient.getAllProducts();
    }

    @GetMapping("/{id}")
    public ProductResponseModel getProductById(@PathVariable String id) {
        return productServiceClient.getProductById(id);
    }

    @PostMapping
    public ProductResponseModel createProduct(@RequestBody ProductRequestModel model) {
        return productServiceClient.addProduct(model);
    }

    @PutMapping("/{id}")
    public ProductResponseModel updateProduct(@PathVariable String id,
                                              @RequestBody ProductRequestModel model) {
        return productServiceClient.updateProduct(id, model);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable String id) {
        productServiceClient.deleteProduct(id);
    }
}
