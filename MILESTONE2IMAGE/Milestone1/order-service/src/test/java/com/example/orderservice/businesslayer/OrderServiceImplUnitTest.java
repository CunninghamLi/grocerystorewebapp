package com.example.orderservice.businesslayer;

import com.example.orderservice.datalayer.Order;
import com.example.orderservice.datalayer.OrderIdentifier;
import com.example.orderservice.datalayer.OrderRepository;
import com.example.orderservice.datamapperlayer.OrderResponseMapper;
import com.example.orderservice.presentationlayer.OrderResponseModel;
import com.example.orderservice.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplUnitTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderResponseMapper orderResponseMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;
    private OrderResponseModel responseModel;

    @BeforeEach
    void setUp() {
        // Instead of calling the 3-arg constructor, use the no-args + setter
        order = new Order();
        order.setOrderIdentifier(new OrderIdentifier("order-001"));

        responseModel = new OrderResponseModel();
        responseModel.setOrderId("order-001");
    }

    @Test
    void whenGetOrders_thenReturnResponseList() {
        when(orderRepository.findAll())
                .thenReturn(Collections.singletonList(order));
        when(orderResponseMapper.entityToOrderResponseModel(order))
                .thenReturn(responseModel);

        List<OrderResponseModel> result = orderService.getOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("order-001", result.get(0).getOrderId());

        verify(orderRepository, times(1)).findAll();
        verify(orderResponseMapper, times(1))
                .entityToOrderResponseModel(order);
    }

    @Test
    void whenGetOrderByIdNotFound_thenThrowNotFoundException() {
        when(orderRepository.findByOrderIdentifierOrderId("not-exist"))
                .thenReturn(null);

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> orderService.getOrderById("not-exist")
        );
        assertTrue(
                ex.getMessage().contains("not-exist"),
                "Expected exception message to mention the order ID"
        );

        verify(orderRepository, times(1))
                .findByOrderIdentifierOrderId("not-exist");
    }
}
