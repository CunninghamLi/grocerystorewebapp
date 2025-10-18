// src/main/java/com/example/orderservice/businesslayer/OrderServiceImpl.java
package com.example.orderservice.businesslayer;

import com.example.orderservice.datalayer.Order;
import com.example.orderservice.datalayer.OrderIdentifier;
import com.example.orderservice.datalayer.OrderRepository;
import com.example.orderservice.datalayer.OrderStatus;
import com.example.orderservice.datamapperlayer.OrderRequestMapper;
import com.example.orderservice.datamapperlayer.OrderResponseMapper;
import com.example.orderservice.domainclient.CustomerServiceClient;
import com.example.orderservice.domainclient.PaymentServiceClient;
import com.example.orderservice.domainclient.ProductServiceClient;
import com.example.orderservice.presentationlayer.OrderRequestModel;
import com.example.orderservice.presentationlayer.OrderResponseModel;
import com.example.orderservice.presentationlayer.customer.CustomerResponseModel;
import com.example.orderservice.presentationlayer.payment.PaymentResponseModel;
import com.example.orderservice.presentationlayer.product.ProductResponseModel;
import com.example.orderservice.utils.exceptions.NotFoundException;
import com.example.orderservice.utils.exceptions.OrderPriceMismatchException;  // ← import your custom exception

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderResponseMapper orderResponseMapper;
    private final OrderRequestMapper orderRequestMapper;
    private final CustomerServiceClient customerServiceClient;
    private final PaymentServiceClient paymentServiceClient;
    private final ProductServiceClient productServiceClient;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderResponseMapper orderResponseMapper,
                            OrderRequestMapper orderRequestMapper,
                            CustomerServiceClient customerServiceClient,
                            PaymentServiceClient paymentServiceClient,
                            ProductServiceClient productServiceClient) {
        this.orderRepository = orderRepository;
        this.orderResponseMapper = orderResponseMapper;
        this.orderRequestMapper = orderRequestMapper;
        this.customerServiceClient = customerServiceClient;
        this.paymentServiceClient = paymentServiceClient;
        this.productServiceClient = productServiceClient;
    }

    @Override
    public OrderResponseModel addOrder(OrderRequestModel orderRequestModel) {
        OrderIdentifier newOrderIdentifier = new OrderIdentifier(UUID.randomUUID().toString());
        Order order = orderRequestMapper.requestModelToEntity(orderRequestModel, newOrderIdentifier);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(orderRequestModel.getStatus() != null
                ? orderRequestModel.getStatus()
                : OrderStatus.PROCESSING);

        order.validateBasicInvariant();

        double total = order.getProductIds().stream()
                .mapToDouble(pid -> {
                    ProductResponseModel product = productServiceClient.getProductByProductId(pid);
                    if (product == null) {
                        throw new NotFoundException("Product not found with id " + pid);
                    }
                    return product.getPrice();
                })
                .sum();

        // Use your custom exception here
        if (Math.abs(total - order.getAmount()) > 0.01) {
            throw new OrderPriceMismatchException(
                    BigDecimal.valueOf(total),
                    BigDecimal.valueOf(order.getAmount())
            );
        }

        // NOTE: Stock adjustments should be handled by the ProductServiceClient if supported.
        Order saved = orderRepository.save(order);
        return enrichOrderResponse(saved);
    }

    @Override
    public List<OrderResponseModel> getOrders() {
        log.info("Fetching all orders");
        return orderRepository.findAll().stream()
                .map(this::enrichOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponseModel getOrderById(String orderIdString) {
        log.info("Fetching order by orderId: {}", orderIdString);
        Order order = orderRepository.findByOrderIdentifierOrderId(orderIdString);
        if (order == null) {
            throw new NotFoundException("Order not found with orderId: " + orderIdString);
        }
        return enrichOrderResponse(order);
    }

    @Override
    public OrderResponseModel updateOrder(OrderRequestModel orderRequestModel, String orderIdString) {
        log.info("Updating order with orderId: {}", orderIdString);
        Order existing = orderRepository.findByOrderIdentifierOrderId(orderIdString);
        if (existing == null) {
            throw new NotFoundException("Order not found for update with orderId: " + orderIdString);
        }

        if (orderRequestModel.getCustomerId() != null) existing.setCustomerId(orderRequestModel.getCustomerId());
        if (orderRequestModel.getPaymentId() != null) existing.setPaymentId(orderRequestModel.getPaymentId());
        if (orderRequestModel.getProductIds() != null) existing.setProductIds(orderRequestModel.getProductIds());
        if (orderRequestModel.getAmount() != null) existing.setAmount(orderRequestModel.getAmount());
        if (orderRequestModel.getStatus() != null) existing.setStatus(orderRequestModel.getStatus());

        existing.validateBasicInvariant();

        double total = existing.getProductIds().stream()
                .mapToDouble(pid -> {
                    ProductResponseModel product = productServiceClient.getProductByProductId(pid);
                    if (product == null) {
                        throw new NotFoundException("Product not found with id " + pid);
                    }
                    return product.getPrice();
                })
                .sum();

        // And again here
        if (Math.abs(total - existing.getAmount()) > 0.01) {
            throw new OrderPriceMismatchException(
                    BigDecimal.valueOf(total),
                    BigDecimal.valueOf(existing.getAmount())
            );
        }

        // NOTE: Stock adjustments should be handled by the ProductServiceClient if supported.
        Order updated = orderRepository.save(existing);
        return enrichOrderResponse(updated);
    }

    @Override
    public void deleteOrder(String orderIdString) {
        Order existing = orderRepository.findByOrderIdentifierOrderId(orderIdString);
        if (existing == null) {
            throw new NotFoundException("Order not found for deletion with orderId: " + orderIdString);
        }
        orderRepository.delete(existing);
    }

    private OrderResponseModel enrichOrderResponse(Order order) {
        if (order == null) {
            return null;
        }
        log.debug("Enriching order response for orderId: {}",
                order.getOrderIdentifier() != null
                        ? order.getOrderIdentifier().getOrderId()
                        : "N/A");

        CustomerResponseModel customerDetails = null;
        if (order.getCustomerId() != null) {
            try {
                customerDetails = customerServiceClient.getCustomerByCustomerId(order.getCustomerId());
            } catch (Exception e) {
                log.warn("Failed to fetch customer details for customerId {}: {}",
                        order.getCustomerId(), e.getMessage());
            }
        }

        PaymentResponseModel paymentDetails = null;
        if (order.getPaymentId() != null) {
            try {
                paymentDetails = paymentServiceClient.getPaymentById(order.getPaymentId());
            } catch (Exception e) {
                log.warn("Failed to fetch payment details for paymentId {}: {}",
                        order.getPaymentId(), e.getMessage());
            }
        }

        List<ProductResponseModel> productDetailsList = Collections.emptyList();
        if (order.getProductIds() != null && !order.getProductIds().isEmpty()) {
            productDetailsList = new ArrayList<>();
            for (String productId : order.getProductIds()) {
                try {
                    ProductResponseModel product = productServiceClient.getProductByProductId(productId);
                    if (product != null) {
                        productDetailsList.add(product);
                    }
                } catch (Exception e) {
                    log.warn("Failed to fetch product details for productId {}: {}",
                            productId, e.getMessage());
                }
            }
        }

        OrderResponseModel responseModel = orderResponseMapper.entityToOrderResponseModel(order);
        if (customerDetails != null) {
            responseModel.setCustomerFirstName(customerDetails.getFirstName());
            responseModel.setCustomerLastName(customerDetails.getLastName());
            responseModel.setCustomer(customerDetails);
        }
        responseModel.setPayment(paymentDetails);
        responseModel.setProducts(productDetailsList);

        log.debug("Successfully enriched order response for orderId: {}",
                responseModel.getOrderId());
        return responseModel;
    }
}
