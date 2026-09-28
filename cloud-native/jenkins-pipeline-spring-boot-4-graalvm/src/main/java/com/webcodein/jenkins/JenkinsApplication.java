package com.webcodein.jenkins;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class JenkinsApplication {

    public static void main(String[] args) {
        SpringApplication.run(JenkinsApplication.class, args);
    }
}

@RestController
class StatusController {

    @GetMapping("/api/status")
    public String getStatus() {
        return "Application is running beautifully, built by Jenkins!";
    }
}
