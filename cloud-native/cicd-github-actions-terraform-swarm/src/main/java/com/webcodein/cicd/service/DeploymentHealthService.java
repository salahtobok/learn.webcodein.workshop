package com.webcodein.cicd.service;

import com.webcodein.cicd.model.DeploymentStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Service
public class DeploymentHealthService {

    private final String version;

    public DeploymentHealthService(@Value("${app.version:1.0.0}") String version) {
        this.version = version;
    }

    public DeploymentStatus getCurrentStatus() {
        String hostname = resolveHostname();
        return DeploymentStatus.up(version, hostname);
    }

    public String resolveHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown-host";
        }
    }

    public String getVersion() {
        return version;
    }
}
