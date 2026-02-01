package com.promoquoter.service;

import com.promoquoter.domain.Order;
import com.promoquoter.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class IdempotencyService {

    private final OrderRepository orderRepository;

    public IdempotencyService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public Optional<Order> findByKey(String idempotencyKey) {
        return orderRepository.findByIdempotencyKey(idempotencyKey);
    }
}
