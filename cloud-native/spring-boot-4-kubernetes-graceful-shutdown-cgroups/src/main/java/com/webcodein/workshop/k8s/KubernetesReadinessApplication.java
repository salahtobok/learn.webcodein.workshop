package com.webcodein.workshop.k8s;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KubernetesReadinessApplication {

    public static void main(String[] args) {
        SpringApplication.run(KubernetesReadinessApplication.class, args);
    }
}
