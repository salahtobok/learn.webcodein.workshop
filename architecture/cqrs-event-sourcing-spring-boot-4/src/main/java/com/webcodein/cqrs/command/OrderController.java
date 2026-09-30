package com.webcodein.cqrs.command;

import com.webcodein.cqrs.query.OrderReadModel;
import com.webcodein.cqrs.query.OrderReadRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderCommandService commandService;
    private final OrderReadRepository readRepository;

    public OrderController(OrderCommandService commandService, OrderReadRepository readRepository) {
        this.commandService = commandService;
        this.readRepository = readRepository;
    }

    @PostMapping
    public UUID createOrder(@RequestParam String customerId, @RequestParam double amount) {
        return commandService.createOrder(customerId, amount);
    }

    @PostMapping("/{id}/confirm")
    public void confirmOrder(@PathVariable UUID id) {
        commandService.confirmOrder(id);
    }

    @GetMapping
    public List<OrderReadModel> getAllOrders() {
        return readRepository.findAll();
    }
}
