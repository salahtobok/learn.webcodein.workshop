package com.webcodein.jenkins.service;

import com.webcodein.jenkins.model.BuildPipelineStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PipelineStatusService {

    public BuildPipelineStatus getCurrentStatus() {
        return new BuildPipelineStatus(
                "jenkins-pipeline-spring-boot-4-graalvm",
                "UP",
                "Jenkins Declarative Pipeline",
                Runtime.version().toString(),
                "Linux x86_64 AOT Native Executable",
                Instant.now()
        );
    }
}
