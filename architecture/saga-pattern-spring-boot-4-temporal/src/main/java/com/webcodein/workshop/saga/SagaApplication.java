package com.webcodein.workshop.saga;

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SagaApplication {

    private static final Logger log = LoggerFactory.getLogger(SagaApplication.class);
    public static final String TASK_QUEUE = "OrderFulfillmentTaskQueue";

    public static void main(String[] args) {
        SpringApplication.run(SagaApplication.class, args);
    }

    @Bean
    public WorkflowClient workflowClient() {
        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
        return WorkflowClient.newInstance(service);
    }

    @Bean
    @ConditionalOnProperty(name = "temporal.worker.enabled", havingValue = "true", matchIfMissing = true)
    public CommandLineRunner startWorker(WorkflowClient client) {
        return args -> {
            if (client == null || client.getOptions() == null) {
                return;
            }
            try {
                WorkerFactory factory = WorkerFactory.newInstance(client);
                Worker worker = factory.newWorker(TASK_QUEUE);

                // Register workflows and activities
                worker.registerWorkflowImplementationTypes(OrderFulfillmentWorkflowImpl.class);
                worker.registerActivitiesImplementations(new OrderActivitiesImpl());

                // Start polling
                factory.start();
                log.info("Temporal Worker started for task queue: {}", TASK_QUEUE);
            } catch (Exception e) {
                log.warn("Could not start Temporal worker (is Temporal cluster running?): {}", e.getMessage());
            }
        };
    }
}
