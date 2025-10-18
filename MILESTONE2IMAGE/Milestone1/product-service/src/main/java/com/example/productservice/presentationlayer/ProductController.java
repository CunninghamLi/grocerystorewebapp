package com.example.productservice.presentationlayer;

import com.example.productservice.businesslayer.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductResponseModel> getAllProducts() {
        return productService.getProducts(); // ✅ Corrected
    }

    @GetMapping("/{productId}")
    public ProductResponseModel getProductById(@PathVariable String productId) {
        return productService.getProductByProductId(productId); // ✅ Corrected
    }

    @DeleteMapping("/{productId}")
    public void deleteProduct(@PathVariable String productId) {
        productService.removeProduct(productId); // ✅ Corrected
    }

    @PostMapping
    public ProductResponseModel addProduct(@RequestBody ProductRequestModel request) {
        return productService.addProduct(request);
    }

    @PutMapping("/{productId}")
    public ProductResponseModel updateProduct(@RequestBody ProductRequestModel updatedProduct,
                                              @PathVariable String productId) {
        return productService.updateProduct(updatedProduct, productId);
    }
}
