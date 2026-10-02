package com.webcodein.workshop.architecture.archunitenforceddd.domain.model;
import com.webcodein.workshop.architecture.archunitenforceddd.domain.annotations.AggregateRoot;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AggregateRoot
@Entity
@Table(name = "orders")
public class Order {
    @Id
    private UUID id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<OrderLine> lines = new ArrayList<>();

    protected Order() {} // JPA

    public Order(UUID id) {
        this.id = id;
    }
    
    public UUID getId() { return id; }

    public void addLine(OrderLine line) {
        this.lines.add(line);
    }
}
