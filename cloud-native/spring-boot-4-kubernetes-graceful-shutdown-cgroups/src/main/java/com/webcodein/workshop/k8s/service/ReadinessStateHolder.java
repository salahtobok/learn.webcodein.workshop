package com.webcodein.workshop.k8s.service;

import org.springframework.boot.availability.ApplicationAvailability;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ReadinessStateHolder {

    private final ApplicationAvailability availability;
    private final ApplicationEventPublisher eventPublisher;

    public ReadinessStateHolder(ApplicationAvailability availability, ApplicationEventPublisher eventPublisher) {
        this.availability = Objects.requireNonNull(availability, "availability cannot be null");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher cannot be null");
    }

    public void markOutOfService() {
        AvailabilityChangeEvent.publish(eventPublisher, this, ReadinessState.REFUSING_TRAFFIC);
    }

    public void markInService() {
        AvailabilityChangeEvent.publish(eventPublisher, this, ReadinessState.ACCEPTING_TRAFFIC);
    }

    public ReadinessState getReadinessState() {
        return availability.getReadinessState();
    }

    public LivenessState getLivenessState() {
        return availability.getLivenessState();
    }
}
