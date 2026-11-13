package com.webcodein.workshop.kafka.unit;

import com.webcodein.workshop.kafka.model.DltMessageRecord;
import com.webcodein.workshop.kafka.model.OrderEvent;
import com.webcodein.workshop.kafka.model.RetryStats;
import com.webcodein.workshop.kafka.service.KafkaRetryConsumerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaRetryConsumerServiceTest {

    private KafkaRetryConsumerService service;

    @BeforeEach
    void setUp() {
        service = new KafkaRetryConsumerService();
    }

    @Test
    @DisplayName("Should successfully process normal event on first attempt")
    void shouldProcessNormalEventOnFirstAttempt() {
        OrderEvent normal = new OrderEvent("ORD-OK-1", "CUST-1", new BigDecimal("100.00"), "CREATED", 0);
        String action = service.processOrderEvent(normal);

        assertThat(action).isEqualTo("PROCESSED_SUCCESSFULLY");
        RetryStats stats = service.getStats();
        assertThat(stats.totalProcessed()).isEqualTo(1);
        assertThat(stats.successfulFirstAttempt()).isEqualTo(1);
        assertThat(stats.deadLetterCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should route poison pill immediately to DLT")
    void shouldRoutePoisonPillImmediatelyToDlt() {
        OrderEvent poison = new OrderEvent("ORD-POISON-99", "CUST-1", new BigDecimal("500.00"), "CREATED", 0);
        String action = service.processOrderEvent(poison);

        assertThat(action).isEqualTo("ROUTED_TO_DLT_FATAL");
        List<DltMessageRecord> dltList = service.getAllDltMessages();
        assertThat(dltList).hasSize(1);
        assertThat(dltList.getFirst().exceptionMessage()).contains("Poison pill");
    }

    @Test
    @DisplayName("Should route failing message to retry topic then exhaust to DLT")
    void shouldHandleRetryProgressionToDlt() {
        OrderEvent fail0 = new OrderEvent("ORD-FAIL-1", "CUST-1", new BigDecimal("25.00"), "CREATED", 0);
        String action0 = service.processOrderEvent(fail0);
        assertThat(action0).isEqualTo("FORWARDED_TO_RETRY_TOPIC:orders-retry-1000");

        OrderEvent fail1 = fail0.withIncrementedRetry();
        String action1 = service.processOrderEvent(fail1);
        assertThat(action1).isEqualTo("FORWARDED_TO_RETRY_TOPIC:orders-retry-2000");

        OrderEvent fail2 = fail1.withIncrementedRetry();
        String action2 = service.processOrderEvent(fail2);
        assertThat(action2).isEqualTo("FORWARDED_TO_RETRY_TOPIC:orders-retry-4000");

        OrderEvent fail3 = fail2.withIncrementedRetry();
        String action3 = service.processOrderEvent(fail3);
        assertThat(action3).isEqualTo("ROUTED_TO_DLT_EXHAUSTED");

        assertThat(service.getAllDltMessages()).hasSize(1);
    }

    @Test
    @DisplayName("Should calculate exponential delay accurately")
    void shouldCalculateExponentialDelay() {
        assertThat(service.calculateDelayMs(1)).isEqualTo(1000);
        assertThat(service.calculateDelayMs(2)).isEqualTo(2000);
        assertThat(service.calculateDelayMs(3)).isEqualTo(4000);
    }

    @Test
    @DisplayName("Should replay DLT message")
    void shouldReplayDltMessage() {
        OrderEvent poison = new OrderEvent("ORD-POISON-1", "CUST-1", new BigDecimal("10.00"), "CREATED", 0);
        service.processOrderEvent(poison);

        DltMessageRecord dltRecord = service.getAllDltMessages().getFirst();
        boolean replayed = service.replayMessage(dltRecord.id());

        assertThat(replayed).isTrue();
        assertThat(service.findDltMessage(dltRecord.id()).orElseThrow().replayed()).isTrue();
    }
}
