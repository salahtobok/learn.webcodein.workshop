package com.webcodein.workshop.k8s.config;

import com.webcodein.workshop.k8s.service.ReadinessStateHolder;
import com.webcodein.workshop.k8s.service.WorkloadProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class GracefulShutdownListener implements SmartLifecycle {

    private static final Logger log = LoggerFactory.getLogger(GracefulShutdownListener.class);

    private final ReadinessStateHolder readinessStateHolder;
    private final WorkloadProcessingService workloadProcessingService;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public GracefulShutdownListener(ReadinessStateHolder readinessStateHolder,
                                    WorkloadProcessingService workloadProcessingService) {
        this.readinessStateHolder = readinessStateHolder;
        this.workloadProcessingService = workloadProcessingService;
    }

    @Override
    public void start() {
        running.set(true);
        log.info("GracefulShutdownListener initialized: readiness probes active.");
    }

    @Override
    public void stop() {
        log.warn("SIGTERM or shutdown initiated: marking readiness probe OUT_OF_SERVICE.");
        readinessStateHolder.markOutOfService();
        workloadProcessingService.markShuttingDown();
        running.set(false);
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }

    @Override
    public int getPhase() {
        // High phase number ensures this lifecycle runs early in the shutdown sequence
        return Integer.MAX_VALUE - 100;
    }
}
