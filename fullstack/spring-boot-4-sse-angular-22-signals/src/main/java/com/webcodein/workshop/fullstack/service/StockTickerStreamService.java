package com.webcodein.workshop.fullstack.service;

import com.webcodein.workshop.fullstack.model.PriceUpdateEvent;
import com.webcodein.workshop.fullstack.model.StreamStats;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class StockTickerStreamService {

    // 30 minutes timeout for persistent browser push streams
    public static final long DEFAULT_TIMEOUT_MS = 30 * 60 * 1000L;

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final AtomicLong eventCounter = new AtomicLong(0);

    public SseEmitter subscribe(String clientId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT_MS);

        emitters.put(clientId, emitter);

        emitter.onCompletion(() -> emitters.remove(clientId));
        emitter.onTimeout(() -> {
            emitter.complete();
            emitters.remove(clientId);
        });
        emitter.onError(e -> emitters.remove(clientId));

        // Send initial connection handshake event
        try {
            emitter.send(SseEmitter.event()
                    .name("INIT_CONNECTION")
                    .id("0")
                    .data("Connected to Real-Time Price Stream on Java 25 Virtual Threads"));
        } catch (IOException e) {
            emitters.remove(clientId);
        }

        return emitter;
    }

    public int broadcastPriceUpdate(PriceUpdateEvent update) {
        eventCounter.incrementAndGet();
        int successfulDeliveries = 0;

        for (Map.Entry<String, SseEmitter> entry : emitters.entrySet()) {
            String clientId = entry.getKey();
            SseEmitter emitter = entry.getValue();
            try {
                emitter.send(SseEmitter.event()
                        .name("price_tick")
                        .id(String.valueOf(update.sequenceNumber()))
                        .data(update));
                successfulDeliveries++;
            } catch (Exception e) {
                emitters.remove(clientId);
            }
        }
        return successfulDeliveries;
    }

    public void removeClient(String clientId) {
        SseEmitter emitter = emitters.remove(clientId);
        if (emitter != null) {
            emitter.complete();
        }
    }

    public int getActiveEmitterCount() {
        return emitters.size();
    }

    public StreamStats getStats() {
        return new StreamStats(
                emitters.size(),
                eventCounter.get(),
                true // Virtual threads active
        );
    }
}
