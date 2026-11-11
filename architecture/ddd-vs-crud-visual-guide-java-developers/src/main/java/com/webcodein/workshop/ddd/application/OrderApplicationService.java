package com.webcodein.workshop.ddd.application;

import com.webcodein.workshop.ddd.domain.Email;
import com.webcodein.workshop.ddd.domain.Money;
import com.webcodein.workshop.ddd.domain.Order;
import com.webcodein.workshop.ddd.domain.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class OrderApplicationService {

    private final OrderRepository repository;

    public OrderApplicationService(OrderRepository repository) {
        this.repository = repository;
    }

    public Long createOrder(CreateOrderCommand command) {
        Email email = new Email(command.email());
        Money money = Money.of(command.amount(), command.currency() != null ? command.currency() : "USD");
        Order order = new Order(email, money);
        Order saved = repository.save(order);
        return saved.getId();
    }

    public void fulfillOrder(Long id) {
        Order order = loadOrder(id);
        order.fulfill();
        repository.save(order);
    }

    public void cancelOrder(Long id, String reason) {
        Order order = loadOrder(id);
        order.cancel(reason);
        repository.save(order);
    }

    @Transactional(readOnly = true)
    public OrderDto getOrder(Long id) {
        Order order = loadOrder(id);
        return OrderDto.fromDomain(order);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> listOrders() {
        return repository.findAll().stream()
                .map(OrderDto::fromDomain)
                .toList();
    }

    private Order loadOrder(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + id));
    }
}
