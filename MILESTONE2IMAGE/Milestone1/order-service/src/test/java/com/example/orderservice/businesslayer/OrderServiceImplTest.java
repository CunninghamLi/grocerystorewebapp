package com.example.orderservice.businesslayer;

import com.example.orderservice.datalayer.Order;
import com.example.orderservice.datalayer.OrderIdentifier;
import com.example.orderservice.datalayer.OrderStatus;
import com.example.orderservice.datalayer.OrderRepository;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock private OrderRepository orderRepository;
    @Mock private OrderResponseMapper orderResponseMapper;
    @Mock private CustomerServiceClient customerServiceClient;
    @Mock private PaymentServiceClient paymentServiceClient;
    @Mock private ProductServiceClient productServiceClient;

    @InjectMocks private OrderServiceImpl orderService;

    private Order entity;
    private OrderResponseModel baseResponse;
    private CustomerResponseModel customerDto;
    private PaymentResponseModel paymentDto;
    private ProductResponseModel prod1, prod2, prod3;

    private static final String ORDER_ID = "OID123";

    @BeforeEach
    void setUp() {
        OrderIdentifier identifier = new OrderIdentifier(ORDER_ID);
        entity = new Order();
        entity.setOrderIdentifier(identifier);
        entity.setCustomerId("10");
        entity.setPaymentId("20");
        entity.setProductIds(List.of("1","2","3"));
        entity.setCreatedAt(LocalDateTime.of(2025,1,1,12,0));
        entity.setStatus(OrderStatus.RECEIVED);
        entity.setAmount(100.50);

        baseResponse = new OrderResponseModel();
        baseResponse.setOrderId(ORDER_ID);
        baseResponse.setCustomerId("10");
        baseResponse.setCreatedAt(entity.getCreatedAt());
        baseResponse.setStatus(entity.getStatus());
        baseResponse.setAmount(entity.getAmount());
        baseResponse.setProductIds(List.of("1","2","3"));

        customerDto = new CustomerResponseModel();
        customerDto.setCustomerId("10");
        customerDto.setFirstName("Jane");
        customerDto.setLastName("Doe");

        paymentDto = new PaymentResponseModel();
        paymentDto.setPaymentId("20");
        paymentDto.setAmount("100.50");
        paymentDto.setMethod("Credit Card");
        paymentDto.setCurrency("USD");
        paymentDto.setPaymentDate(LocalDate.of(2025,1,1));

        prod1 = new ProductResponseModel(); prod1.setProductId("1");
        prod2 = new ProductResponseModel(); prod2.setProductId("2");
        prod3 = new ProductResponseModel(); prod3.setProductId("3");
    }

    @Test
    void whenGetOrders_thenReturnEnrichedList() {
        when(orderRepository.findAll()).thenReturn(List.of(entity));
        when(orderResponseMapper.entityToOrderResponseModel(entity)).thenReturn(baseResponse);
        when(customerServiceClient.getCustomerByCustomerId("10")).thenReturn(customerDto);
        when(paymentServiceClient.getPaymentById("20")).thenReturn(paymentDto);
        when(productServiceClient.getProductByProductId("1")).thenReturn(prod1);
        when(productServiceClient.getProductByProductId("2")).thenReturn(prod2);
        when(productServiceClient.getProductByProductId("3")).thenReturn(prod3);

        var results = orderService.getOrders();
        assertThat(results).hasSize(1);
        var out = results.get(0);
        assertThat(out.getOrderId()).isEqualTo(ORDER_ID);
        assertThat(out.getCustomerFirstName()).isEqualTo("Jane");
        assertThat(out.getPayment().getMethod()).isEqualTo("Credit Card");
        assertThat(out.getProducts()).extracting(ProductResponseModel::getProductId)
                .containsExactly("1","2","3");
    }

    @Test
    void whenGetByIdExists_thenReturnEnriched() {
        when(orderRepository.findByOrderIdentifierOrderId(ORDER_ID)).thenReturn(entity);
        when(orderResponseMapper.entityToOrderResponseModel(entity)).thenReturn(baseResponse);
        when(customerServiceClient.getCustomerByCustomerId("10")).thenReturn(customerDto);
        when(paymentServiceClient.getPaymentById("20")).thenReturn(paymentDto);
        when(productServiceClient.getProductByProductId("1")).thenReturn(prod1);
        when(productServiceClient.getProductByProductId("2")).thenReturn(prod2);
        when(productServiceClient.getProductByProductId("3")).thenReturn(prod3);

        var out = orderService.getOrderById(ORDER_ID);
        assertThat(out.getOrderId()).isEqualTo(ORDER_ID);
        assertThat(out.getCustomerLastName()).isEqualTo("Doe");
        assertThat(out.getPayment().getCurrency()).isEqualTo("USD");
        assertThat(out.getProducts()).extracting(ProductResponseModel::getProductId)
                .containsExactly("1","2","3");
    }

    @Test
    void whenGetByIdMissing_thenThrow() {
        when(orderRepository.findByOrderIdentifierOrderId(ORDER_ID)).thenReturn(null);
        assertThrows(NotFoundException.class, () -> orderService.getOrderById(ORDER_ID));
    }

    @Test
    void whenCustomerServiceFails_thenSkipCustomer() {
        when(orderRepository.findAll()).thenReturn(List.of(entity));
        when(orderResponseMapper.entityToOrderResponseModel(entity)).thenReturn(baseResponse);
        doThrow(new RuntimeException("fail")).when(customerServiceClient).getCustomerByCustomerId("10");
        when(paymentServiceClient.getPaymentById("20")).thenReturn(paymentDto);
        when(productServiceClient.getProductByProductId("1")).thenReturn(prod1);
        when(productServiceClient.getProductByProductId("2")).thenReturn(prod2);
        when(productServiceClient.getProductByProductId("3")).thenReturn(prod3);

        var out = orderService.getOrders().get(0);
        assertThat(out.getCustomerFirstName()).isNull();
        assertThat(out.getPayment().getMethod()).isEqualTo("Credit Card");
        assertThat(out.getProducts()).extracting(ProductResponseModel::getProductId)
                .containsExactly("1","2","3");
    }

    @Test
    void whenPaymentServiceFails_thenSkipPayment() {
        when(orderRepository.findAll()).thenReturn(List.of(entity));
        when(orderResponseMapper.entityToOrderResponseModel(entity)).thenReturn(baseResponse);
        when(customerServiceClient.getCustomerByCustomerId("10")).thenReturn(customerDto);
        doThrow(new RuntimeException("fail")).when(paymentServiceClient).getPaymentById("20");
        when(productServiceClient.getProductByProductId("1")).thenReturn(prod1);
        when(productServiceClient.getProductByProductId("2")).thenReturn(prod2);
        when(productServiceClient.getProductByProductId("3")).thenReturn(prod3);

        var out = orderService.getOrders().get(0);
        assertThat(out.getPayment()).isNull();
        assertThat(out.getCustomerFirstName()).isEqualTo("Jane");
        assertThat(out.getProducts()).extracting(ProductResponseModel::getProductId)
                .containsExactly("1","2","3");
    }

    @Test
    void whenProductServiceFailsForOne_thenSkipThatProduct() {
        when(orderRepository.findAll()).thenReturn(List.of(entity));
        when(orderResponseMapper.entityToOrderResponseModel(entity)).thenReturn(baseResponse);
        when(customerServiceClient.getCustomerByCustomerId("10")).thenReturn(customerDto);
        when(paymentServiceClient.getPaymentById("20")).thenReturn(paymentDto);
        when(productServiceClient.getProductByProductId("1")).thenReturn(prod1);
        doThrow(new RuntimeException("fail")).when(productServiceClient).getProductByProductId("2");
        when(productServiceClient.getProductByProductId("3")).thenReturn(prod3);

        var out = orderService.getOrders().get(0);
        assertThat(out.getProducts()).extracting(ProductResponseModel::getProductId)
                .containsExactly("1","3");
    }

    @Test
    void whenNoProducts_thenEmptyList() {
        entity.setProductIds(List.of());
        when(orderRepository.findAll()).thenReturn(List.of(entity));
        when(orderResponseMapper.entityToOrderResponseModel(entity)).thenReturn(baseResponse);
        when(customerServiceClient.getCustomerByCustomerId("10")).thenReturn(customerDto);
        when(paymentServiceClient.getPaymentById("20")).thenReturn(paymentDto);

        var out = orderService.getOrders().get(0);
        assertThat(out.getProducts()).isEmpty();
    }



    @Test
    void whenDeleteOrder_thenDeleteEntity() {
        when(orderRepository.findByOrderIdentifierOrderId(ORDER_ID)).thenReturn(entity);
        orderService.deleteOrder(ORDER_ID);
        verify(orderRepository).delete(entity);
    }

    @Test
    void whenDeleteOrderMissing_thenThrowNotFound() {
        when(orderRepository.findByOrderIdentifierOrderId("none")).thenReturn(null);
        assertThrows(NotFoundException.class, () -> orderService.deleteOrder("none"));
    }
}
