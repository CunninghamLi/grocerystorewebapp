// src/main/java/com/example/apigateway/presentationlayer/order/OrderController.java
package com.example.apigateway.presentationlayer.order;

import com.example.apigateway.domainclient.OrderServiceClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderServiceClient client;

    public OrderController(OrderServiceClient client) {
        this.client = client;
    }

    @GetMapping
    public List<OrderResponseModel> getAll() {
        return client.getAllOrders();
    }

    @GetMapping("/{orderId}")
    public OrderResponseModel getById(@PathVariable String orderId) {
        return client.getOrderById(orderId);
    }

    @PostMapping
    public OrderResponseModel create(@RequestBody OrderRequestModel model) {
        return client.addOrder(model);
    }

    @PutMapping("/{orderId}")
    public OrderResponseModel update(@PathVariable String orderId,
                                     @RequestBody OrderRequestModel model) {
        return client.updateOrder(orderId, model);
    }

    @DeleteMapping("/{orderId}")
    public void delete(@PathVariable String orderId) {
        client.deleteOrder(orderId);
    }
}
