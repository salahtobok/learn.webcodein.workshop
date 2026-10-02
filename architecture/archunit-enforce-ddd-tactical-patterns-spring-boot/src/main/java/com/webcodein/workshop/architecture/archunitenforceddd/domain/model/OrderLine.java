package com.webcodein.workshop.architecture.archunitenforceddd.domain.model;
import com.webcodein.workshop.architecture.archunitenforceddd.domain.annotations.DomainEntity;
import jakarta.persistence.*;
import java.util.UUID;

@DomainEntity
@Entity
@Table(name = "order_lines")
public class OrderLine {
    @Id
    private UUID id;

    private String productCode;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "price_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "price_currency"))
    })
    private Money price;

    protected OrderLine() {} // JPA

    public OrderLine(UUID id, String productCode, Money price) {
        this.id = id;
        this.productCode = productCode;
        this.price = price;
    }
    
    public UUID getId() { return id; }
}
