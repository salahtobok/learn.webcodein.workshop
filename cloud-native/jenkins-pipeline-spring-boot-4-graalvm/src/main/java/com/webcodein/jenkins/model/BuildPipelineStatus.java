package com.webcodein.jenkins.model;

import java.time.Instant;

public record BuildPipelineStatus(
        String application,
        String status,
        String pipelineEngine,
        String jvmRuntime,
        String graalvmNativeTarget,
        Instant checkedAt
) {
}
