package com.example.outboxpattern.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.willDoNothing;

import com.example.outboxpattern.entities.Order;
import com.example.outboxpattern.mapper.OrderMapper;
import com.example.outboxpattern.model.response.OrderResponse;
import com.example.outboxpattern.repositories.OrderRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    @Test
    void findOrderById() {
        // given
        given(orderRepository.findById(1L)).willReturn(Optional.of(getOrder()));
        given(orderMapper.toResponse(any(Order.class))).willReturn(getOrderResponse());
        // when
        Optional<OrderResponse> optionalOrder = orderService.findOrderById(1L);
        // then
        assertThat(optionalOrder).isPresent();
        OrderResponse order = optionalOrder.get();
        assertThat(order.id()).isEqualTo(1L);
        assertThat(order.text()).isEqualTo("junitTest");
    }

    @Test
    void deleteOrderById() {
        // given
        willDoNothing().given(orderRepository).deleteById(1L);
        // when
        orderService.deleteOrderById(1L);
        // then
        verify(orderRepository, times(1)).deleteById(1L);
    }

    private Order getOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setText("junitTest");
        return order;
    }

    private OrderResponse getOrderResponse() {
        return new OrderResponse(1L, "junitTest");
    }
}
