package com.example.orderservice.presentationlayer;

import com.example.orderservice.businesslayer.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("api/v1/orders")
public class OrderController {


    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<OrderResponseModel>> getOrders(){
        List<OrderResponseModel> orderResponseModels = orderService.getOrders();
        return ResponseEntity.ok().body(orderResponseModels);
    }
    @GetMapping(value = "{orderId}", produces = "application/json")
    public ResponseEntity<OrderResponseModel> getOrderById(@PathVariable String orderId){
        OrderResponseModel orderResponseModel = orderService.getOrderById(orderId);
        return ResponseEntity.ok().body(orderResponseModel);
    }
    @PostMapping(produces = "application/json",consumes = "application/json")
    public ResponseEntity<OrderResponseModel> addOrder(@RequestBody OrderRequestModel orderRequestModel){
        OrderResponseModel orderResponseModel = orderService.addOrder(orderRequestModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponseModel);
    }

    @PutMapping(value = "{orderId}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<OrderResponseModel> updateOrder(@PathVariable String orderId, @RequestBody OrderRequestModel orderRequestModel){
        OrderResponseModel orderResponseModel = orderService.updateOrder(orderRequestModel,orderId);
        return ResponseEntity.ok().body(orderResponseModel);
    }
    @DeleteMapping(value = "{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable String orderId){
        orderService.deleteOrder(orderId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }



}