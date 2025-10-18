package com.example.orderservice.businesslayer;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import com.example.orderservice.datalayer.Order;
import com.example.orderservice.datalayer.OrderIdentifier;
import com.example.orderservice.datalayer.OrderRepository;
import com.example.orderservice.datalayer.OrderStatus;
import com.example.orderservice.datamapperlayer.OrderRequestMapper;
import com.example.orderservice.datamapperlayer.OrderResponseMapper;
import com.example.orderservice.presentationlayer.OrderRequestModel;
import com.example.orderservice.presentationlayer.OrderResponseModel;
import com.example.orderservice.presentationlayer.customer.CustomerResponseModel;
import com.example.orderservice.presentationlayer.payment.PaymentResponseModel;
import com.example.orderservice.presentationlayer.product.ProductResponseModel;
import com.example.orderservice.domainclient.CustomerServiceClient;
import com.example.orderservice.domainclient.PaymentServiceClient;
import com.example.orderservice.domainclient.ProductServiceClient;
import com.example.orderservice.utils.exceptions.NotFoundException;
import com.example.orderservice.utils.exceptions.OrderPriceMismatchException; // Added import

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderServiceImplCoverageTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    OrderRequestMapper orderRequestMapper;

    @Mock
    OrderResponseMapper orderResponseMapper;

    @Mock
    CustomerServiceClient customerServiceClient;

    @Mock
    PaymentServiceClient paymentServiceClient;

    @Mock
    ProductServiceClient productServiceClient;

    @InjectMocks
    OrderServiceImpl service;

    private OrderRequestModel req;
    private Order dummyOrder;
    private OrderResponseModel dummyResp;

    @BeforeEach
    void setUp() {
        // Prepare a basic request for addOrder tests
        req = new OrderRequestModel();
        req.setProductIds(List.of("pA", "pB"));
        req.setAmount(12.5); // This is the input amount for the error message

        // Dummy Order entity produced by mapper
        dummyOrder = new Order();
        dummyOrder.setOrderIdentifier(new OrderIdentifier(UUID.randomUUID().toString()));
        dummyOrder.setProductIds(req.getProductIds());
        dummyOrder.setAmount(req.getAmount());
        dummyOrder.setCreatedAt(LocalDateTime.now());
        dummyOrder.setStatus(OrderStatus.PROCESSING);

        // Dummy response model produced by mapper
        dummyResp = new OrderResponseModel();
        dummyResp.setOrderId(dummyOrder.getOrderIdentifier().getOrderId());

        // Stub mapping methods
        given(orderRequestMapper.requestModelToEntity(eq(req), any(OrderIdentifier.class)))
                .willReturn(dummyOrder);
        given(orderResponseMapper.entityToOrderResponseModel(dummyOrder))
                .willReturn(dummyResp);

        lenient().when(customerServiceClient.getCustomerByCustomerId(anyString()))
                .thenReturn(null);
        lenient().when(paymentServiceClient.getPaymentById(anyString()))
                .thenReturn(null);

    }

    @Test
    void addOrder_success() {
        ProductResponseModel pA = new ProductResponseModel(); pA.setProductId("pA"); pA.setPrice(5.0);
        ProductResponseModel pB = new ProductResponseModel(); pB.setProductId("pB"); pB.setPrice(7.5);

        given(productServiceClient.getProductByProductId("pA")).willReturn(pA);
        given(productServiceClient.getProductByProductId("pB")).willReturn(pB);
        given(orderRepository.save(dummyOrder)).willReturn(dummyOrder);

        OrderResponseModel response = service.addOrder(req);

        assertThat(response).isEqualTo(dummyResp);
        then(orderRepository).should().save(dummyOrder);
    }

    @Test
    void addOrder_missingProduct_throwsNotFound() {
        ProductResponseModel pA = new ProductResponseModel(); pA.setProductId("pA"); pA.setPrice(5.0);
        given(productServiceClient.getProductByProductId("pA")).willReturn(pA);
        given(productServiceClient.getProductByProductId("pB")).willReturn(null);

        assertThatThrownBy(() -> service.addOrder(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Product not found with id pB");
    }

    @Test
    void addOrder_amountMismatch_throwsIllegalState() { // Test name can remain, or be updated to reflect OrderPriceMismatchException
        // For this test, the product prices will sum to 2.0 (1.0 + 1.0)
        ProductResponseModel cheapProductA = new ProductResponseModel(); cheapProductA.setProductId("pA"); cheapProductA.setPrice(1.0);
        ProductResponseModel cheapProductB = new ProductResponseModel(); cheapProductB.setProductId("pB"); cheapProductB.setPrice(1.0);
        given(productServiceClient.getProductByProductId("pA")).willReturn(cheapProductA);
        given(productServiceClient.getProductByProductId("pB")).willReturn(cheapProductB);


        // req.setAmount(12.5) is set in setUp()
        // Calculated total will be 1.0 (pA) + 1.0 (pB) = 2.0
        assertThatThrownBy(() -> service.addOrder(req))
                .isInstanceOf(OrderPriceMismatchException.class) // Changed here
                .hasMessageContaining("THE PRICE OF THE ITEMS YOU SELECTED (2.0) DOES NOT MATCH THE AMOUNT THAT YOU INPUT (12.5)"); // Updated message
    }


    @Test
    void updateOrder_success() {
        String oid = dummyOrder.getOrderIdentifier().getOrderId();
        // Create a new request for update
        OrderRequestModel updReq = new OrderRequestModel(); // Note: This request model needs an amount if you want to test price match
        updReq.setProductIds(List.of("x", "y"));
        updReq.setAmount(5.0); // Make sure amount is set for the update request


        // Stub finding the existing order
        given(orderRepository.findByOrderIdentifierOrderId(oid)).willReturn(dummyOrder);

        // Stub product prices for update
        ProductResponseModel x = new ProductResponseModel(); x.setProductId("x"); x.setPrice(3.0);
        ProductResponseModel y = new ProductResponseModel(); y.setProductId("y"); y.setPrice(2.0);
        given(productServiceClient.getProductByProductId("x")).willReturn(x);
        given(productServiceClient.getProductByProductId("y")).willReturn(y);

        // The service will update the dummyOrder's amount based on updReq if not null
        // Or, if updReq.getAmount() is null, it will use dummyOrder's existing amount.
        // For a successful update where prices match, ensure dummyOrder's amount is updated to 5.0
        // *before* the save or ensure the updReq.getAmount() correctly reflects the sum.
        // The OrderServiceImpl updates the entity 'existing' (which is dummyOrder here) with values from updReq.
        // So, dummyOrder.setAmount(updReq.getAmount()) will happen internally in the service.

        given(orderRepository.save(dummyOrder)).willReturn(dummyOrder);

        OrderResponseModel result = service.updateOrder(updReq, oid);

        assertThat(result).isEqualTo(dummyResp);
        assertThat(dummyOrder.getAmount()).isEqualTo(5.0); // Verify the amount was updated on the entity
        then(orderRepository).should().save(dummyOrder);
    }

    @Test
    void updateOrder_missingOrder_throwsNotFound() {
        given(orderRepository.findByOrderIdentifierOrderId(anyString())).willReturn(null);

        assertThatThrownBy(() -> service.updateOrder(req, "invalid-id"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Order not found for update");
    }

    @Test
    void updateOrder_missingProduct_throwsNotFound() {
        String oid = dummyOrder.getOrderIdentifier().getOrderId();
        given(orderRepository.findByOrderIdentifierOrderId(oid)).willReturn(dummyOrder);
        ProductResponseModel good = new ProductResponseModel(); good.setProductId("pA"); good.setPrice(1.0);
        given(productServiceClient.getProductByProductId("pA")).willReturn(good);
        given(productServiceClient.getProductByProductId("pB")).willReturn(null); // pB is missing

        // req (from setUp) has productIds "pA", "pB" and amount 12.5
        assertThatThrownBy(() -> service.updateOrder(req, oid))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Product not found with id pB");
    }

    @Test
    void updateOrder_amountMismatch_throwsIllegalState() { // Test name can remain, or be updated
        String oid = dummyOrder.getOrderIdentifier().getOrderId();
        given(orderRepository.findByOrderIdentifierOrderId(oid)).willReturn(dummyOrder);

        // req (from setUp) has productIds "pA", "pB" and amount 12.5
        // We will mock products so their sum is different from 12.5
        ProductResponseModel cheapProductA = new ProductResponseModel(); cheapProductA.setProductId("pA"); cheapProductA.setPrice(1.0);
        ProductResponseModel cheapProductB = new ProductResponseModel(); cheapProductB.setProductId("pB"); cheapProductB.setPrice(1.0);
        given(productServiceClient.getProductByProductId("pA")).willReturn(cheapProductA);
        given(productServiceClient.getProductByProductId("pB")).willReturn(cheapProductB);

        // The service will use req.getProductIds() which are "pA", "pB".
        // Calculated total will be 1.0 + 1.0 = 2.0.
        // The service will use req.getAmount() which is 12.5.
        assertThatThrownBy(() -> service.updateOrder(req, oid))
                .isInstanceOf(OrderPriceMismatchException.class) // Changed here
                .hasMessageContaining("THE PRICE OF THE ITEMS YOU SELECTED (2.0) DOES NOT MATCH THE AMOUNT THAT YOU INPUT (12.5)"); // Updated message
    }
}