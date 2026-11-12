package com.webcodein.ota;

import com.webcodein.ota.model.CanaryCheckRequest;
import com.webcodein.ota.model.CanaryCheckResponse;
import com.webcodein.ota.model.RolloutStatsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * REST API gateway for OTA canary rollout evaluation and fleet administration.
 */
@RestController
@RequestMapping("/api/ota")
public class OtaController {

    private final OtaRolloutService otaRolloutService;

    public OtaController(OtaRolloutService otaRolloutService) {
        this.otaRolloutService = Objects.requireNonNull(otaRolloutService, "OtaRolloutService must not be null");
    }

    /**
     * Evaluates device update eligibility using structured JSON payload.
     */
    @PostMapping("/evaluate")
    public ResponseEntity<CanaryCheckResponse> evaluateEligibility(@RequestBody CanaryCheckRequest request) {
        CanaryCheckResponse response = otaRolloutService.evaluateEligibility(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Legacy query-parameter endpoint for backward compatibility.
     */
    @GetMapping("/check")
    public ResponseEntity<String> checkUpdate(@RequestParam long deviceId, @RequestParam String version) {
        if (otaRolloutService.isEligibleForUpdate(deviceId, version)) {
            return ResponseEntity.ok("Update available for " + version);
        }
        return ResponseEntity.ok("No updates available");
    }

    /**
     * Grants firmware update access to an individual device offset.
     */
    @PostMapping("/enable/{version}")
    public ResponseEntity<Void> enableForDevice(@PathVariable String version, @RequestParam long deviceId) {
        otaRolloutService.enableUpdateForDevice(deviceId, version);
        return ResponseEntity.ok().build();
    }

    /**
     * Revokes firmware update access from an individual device offset.
     */
    @PostMapping("/disable/{version}")
    public ResponseEntity<Void> disableForDevice(@PathVariable String version, @RequestParam long deviceId) {
        otaRolloutService.disableUpdateForDevice(deviceId, version);
        return ResponseEntity.ok().build();
    }

    /**
     * Returns real-time BITCOUNT metrics and adoption percentage for an active version.
     */
    @GetMapping("/stats/{version}")
    public ResponseEntity<RolloutStatsResponse> getRolloutStats(
            @PathVariable String version,
            @RequestParam(defaultValue = "10000000") long fleetSize
    ) {
        RolloutStatsResponse stats = otaRolloutService.getRolloutStats(version, fleetSize);
        return ResponseEntity.ok(stats);
    }

    /**
     * Emergency kill-switch: purges rollout key in under 1 millisecond fleet-wide.
     */
    @DeleteMapping("/emergency-halt/{version}")
    public ResponseEntity<Void> emergencyHalt(@PathVariable String version) {
        boolean deleted = otaRolloutService.emergencyHaltRollout(version);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
