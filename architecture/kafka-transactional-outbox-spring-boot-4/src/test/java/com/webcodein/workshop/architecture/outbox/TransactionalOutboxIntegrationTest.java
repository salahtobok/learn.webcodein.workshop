package com.webcodein.workshop.architecture.outbox;

import com.webcodein.workshop.architecture.outbox.consumer.OrderEventConsumer;
import com.webcodein.workshop.architecture.outbox.dto.OrderRequest;
import com.webcodein.workshop.architecture.outbox.entity.Order;
import com.webcodein.workshop.architecture.outbox.entity.OrderStatus;
import com.webcodein.workshop.architecture.outbox.entity.OutboxEvent;
import com.webcodein.workshop.architecture.outbox.repository.OrderRepository;
import com.webcodein.workshop.architecture.outbox.repository.OutboxEventRepository;
import com.webcodein.workshop.architecture.outbox.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@Testcontainers
class TransactionalOutboxIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("apache/kafka-native:3.8.0"));

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Autowired
    private OrderEventConsumer orderEventConsumer;

    @BeforeEach
    void setUp() {
        orderEventConsumer.clear();
        outboxEventRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @Test
    @DisplayName("Transactional Outbox: Persists both Order and OutboxEvent in the same ACID database transaction")
    void testOrderCreationPersistsOrderAndOutboxEvent() {
        OrderRequest request = new OrderRequest(
                "cust-4021",
                "PROD-LAPTOP-X",
                1,
                new BigDecimal("1299.99")
        );

        Order createdOrder = orderService.createOrder(request);

        assertThat(createdOrder).isNotNull();
        assertThat(createdOrder.getId()).isNotNull();
        assertThat(createdOrder.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(createdOrder.getCustomerId()).isEqualTo("cust-4021");

        // Verify Order is in database
        assertThat(orderRepository.findById(createdOrder.getId())).isPresent();

        // Verify Outbox Event is in database with matching aggregate ID
        List<OutboxEvent> outboxEvents = outboxEventRepository.findAll();
        assertThat(outboxEvents).hasSize(1);

        OutboxEvent outboxEvent = outboxEvents.getFirst();
        assertThat(outboxEvent.getAggregateType()).isEqualTo("Order");
        assertThat(outboxEvent.getAggregateId()).isEqualTo(createdOrder.getId().toString());
        assertThat(outboxEvent.getEventType()).isEqualTo("OrderCreated");
        assertThat(outboxEvent.getPayload()).contains("PROD-LAPTOP-X");
    }

    @Test
    @DisplayName("Transactional Outbox: Polling publisher reliably transmits event to Kafka and marks processed")
    void testPollingPublisherDeliversToKafkaAndMarksProcessed() {
        OrderRequest request = new OrderRequest(
                "cust-9912",
                "PROD-SERVER-BLADE",
                2,
                new BigDecimal("4999.00")
        );

        Order createdOrder = orderService.createOrder(request);

        // Wait for OutboxPollingPublisher to publish to Kafka and mark event processed
        await().atMost(10, TimeUnit.SECONDS)
                .pollInterval(Duration.ofMillis(200))
                .untilAsserted(() -> {
                    List<OutboxEvent> events = outboxEventRepository.findAll();
                    assertThat(events).isNotEmpty();
                    assertThat(events.getFirst().isProcessed()).isTrue();
                });

        // Wait for Kafka Consumer to receive the message from Kafka broker
        await().atMost(10, TimeUnit.SECONDS)
                .pollInterval(Duration.ofMillis(200))
                .untilAsserted(() -> {
                    List<String> payloads = orderEventConsumer.getReceivedPayloads();
                    assertThat(payloads).isNotEmpty();
                    assertThat(payloads.getFirst())
                            .contains(createdOrder.getId().toString())
                            .contains("PROD-SERVER-BLADE")
                            .contains("cust-9912");
                });
    }
}
