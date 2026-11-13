package com.webcodein.workshop.fullstack.unit;

import com.webcodein.workshop.fullstack.model.PriceUpdateEvent;
import com.webcodein.workshop.fullstack.model.StreamStats;
import com.webcodein.workshop.fullstack.service.StockTickerStreamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class StockTickerStreamServiceTest {

    private StockTickerStreamService service;

    @BeforeEach
    void setUp() {
        service = new StockTickerStreamService();
    }

    @Test
    @DisplayName("Should subscribe new client and track active emitters")
    void shouldSubscribeClient() {
        SseEmitter emitter = service.subscribe("client-1");

        assertThat(emitter).isNotNull();
        assertThat(service.getActiveEmitterCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should broadcast price updates to active subscribers")
    void shouldBroadcastPriceUpdate() {
        service.subscribe("client-A");
        service.subscribe("client-B");

        PriceUpdateEvent event = new PriceUpdateEvent("GOOGL", new BigDecimal("185.20"), new BigDecimal("0.75"), 1L, null);
        int delivered = service.broadcastPriceUpdate(event);

        assertThat(delivered).isEqualTo(2);
        StreamStats stats = service.getStats();
        assertThat(stats.activeEmitters()).isEqualTo(2);
        assertThat(stats.totalEventsPushed()).isEqualTo(1);
        assertThat(stats.virtualThreadsEnabled()).isTrue();
    }

    @Test
    @DisplayName("Should remove client cleanly")
    void shouldRemoveClient() {
        service.subscribe("client-X");
        assertThat(service.getActiveEmitterCount()).isEqualTo(1);

        service.removeClient("client-X");
        assertThat(service.getActiveEmitterCount()).isEqualTo(0);
    }
}
