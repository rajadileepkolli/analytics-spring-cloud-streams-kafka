package com.example.outboxpattern.services;

import com.example.outboxpattern.entities.Order;
import com.example.outboxpattern.exception.OrderNotFoundException;
import com.example.outboxpattern.mapper.OrderMapper;
import com.example.outboxpattern.model.query.FindOrdersQuery;
import com.example.outboxpattern.model.request.OrderRequest;
import com.example.outboxpattern.model.response.OrderResponse;
import com.example.outboxpattern.model.response.PagedResult;
import com.example.outboxpattern.repositories.OrderRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public PagedResult<OrderResponse> findAllOrders(FindOrdersQuery findOrdersQuery) {

        // create Pageable instance
        Pageable pageable = createPageable(findOrdersQuery);

        Page<Order> ordersPage = orderRepository.findAll(pageable);

        List<OrderResponse> orderResponseList = orderMapper.toResponseList(ordersPage.getContent());

        return new PagedResult<>(ordersPage, orderResponseList);
    }

    private Pageable createPageable(FindOrdersQuery findOrdersQuery) {
        int pageNo = Math.max(findOrdersQuery.pageNo() - 1, 0);
        Sort sort = Sort.by(
                findOrdersQuery.sortDir().equalsIgnoreCase(Sort.Direction.ASC.name())
                        ? Sort.Order.asc(findOrdersQuery.sortBy())
                        : Sort.Order.desc(findOrdersQuery.sortBy()));
        return PageRequest.of(pageNo, findOrdersQuery.pageSize(), sort);
    }

    public Optional<OrderResponse> findOrderById(Long id) {
        return orderRepository.findById(id).map(orderMapper::toResponse);
    }

    @Transactional
    public OrderResponse saveOrder(OrderRequest orderRequest) {
        Order order = orderMapper.toEntity(orderRequest);
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponse(savedOrder);
    }

    @Transactional
    public OrderResponse updateOrder(Long id, OrderRequest orderRequest) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

        // Update the order object with data from orderRequest
        orderMapper.mapOrderWithRequest(order, orderRequest);

        // Save the updated order object
        Order updatedOrder = orderRepository.save(order);

        return orderMapper.toResponse(updatedOrder);
    }

    @Transactional
    public void deleteOrderById(Long id) {
        orderRepository.deleteById(id);
    }
}
