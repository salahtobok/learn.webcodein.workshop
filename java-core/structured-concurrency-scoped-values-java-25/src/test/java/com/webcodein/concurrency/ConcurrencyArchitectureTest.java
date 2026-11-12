package com.webcodein.concurrency;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConcurrencyArchitectureTest {

    private final List<Class<?>> controllerClasses = List.of(
            com.webcodein.concurrency.web.OrderCheckoutController.class
    );

    private final List<Class<?>> serviceClasses = List.of(
            com.webcodein.concurrency.service.OrderProcessingService.class,
            com.webcodein.concurrency.service.InventoryService.class,
            com.webcodein.concurrency.service.PaymentService.class,
            com.webcodein.concurrency.service.FraudDetectionService.class,
            com.webcodein.concurrency.service.ShippingCarrierService.class
    );

    private final List<Class<?>> modelClasses = List.of(
            com.webcodein.concurrency.model.OrderRequest.class,
            com.webcodein.concurrency.model.OrderResult.class,
            com.webcodein.concurrency.model.RequestContext.class,
            com.webcodein.concurrency.model.ShippingQuote.class
    );

    @Test
    @DisplayName("Controllers should be annotated with @RestController and reside in web package")
    void controllersShouldResideInWebPackage() {
        for (Class<?> controller : controllerClasses) {
            assertThat(controller.isAnnotationPresent(RestController.class))
                    .as("%s must be annotated with @RestController", controller.getSimpleName())
                    .isTrue();
            assertThat(controller.getPackageName()).isEqualTo("com.webcodein.concurrency.web");
            assertThat(controller.getSimpleName()).endsWith("Controller");
        }
    }

    @Test
    @DisplayName("Services should be annotated with @Service and reside in service package")
    void servicesShouldResideInServicePackage() {
        for (Class<?> service : serviceClasses) {
            assertThat(service.isAnnotationPresent(Service.class))
                    .as("%s must be annotated with @Service", service.getSimpleName())
                    .isTrue();
            assertThat(service.getPackageName()).isEqualTo("com.webcodein.concurrency.service");
            assertThat(service.getSimpleName()).endsWith("Service");
        }
    }

    @Test
    @DisplayName("Service layer must not depend on web layer classes")
    void servicesShouldNotDependOnWebLayer() {
        for (Class<?> service : serviceClasses) {
            for (Field field : service.getDeclaredFields()) {
                assertThat(field.getType().getPackageName())
                        .as("Service field %s in %s must not reference web package", field.getName(), service.getSimpleName())
                        .isNotEqualTo("com.webcodein.concurrency.web");
            }
        }
    }

    @Test
    @DisplayName("Domain models must not depend on services or web layers")
    void modelsShouldNotDependOnServicesOrWeb() {
        for (Class<?> model : modelClasses) {
            for (Field field : model.getDeclaredFields()) {
                assertThat(field.getType().getPackageName())
                        .as("Model field %s in %s must not reference service or web package", field.getName(), model.getSimpleName())
                        .isNotEqualTo("com.webcodein.concurrency.service")
                        .isNotEqualTo("com.webcodein.concurrency.web");
            }
        }
    }

    @Test
    @DisplayName("Controllers must not depend on peer controller classes")
    void controllersShouldNotDependOnOtherControllers() {
        for (Class<?> controller : controllerClasses) {
            for (Field field : controller.getDeclaredFields()) {
                if (field.getType().getSimpleName().endsWith("Controller")) {
                    assertThat(field.getType())
                            .as("Controller %s should not depend on peer controller %s", controller.getSimpleName(), field.getType().getSimpleName())
                            .isNotEqualTo(controller);
                }
            }
        }
    }
}
