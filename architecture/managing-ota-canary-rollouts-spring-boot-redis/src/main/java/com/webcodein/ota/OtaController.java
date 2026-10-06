package com.webcodein.ota;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ota")
public class OtaController {

    private final OtaRolloutService otaRolloutService;

    public OtaController(OtaRolloutService otaRolloutService) {
        this.otaRolloutService = otaRolloutService;
    }

    @GetMapping("/check")
    public ResponseEntity<String> checkUpdate(@RequestParam long deviceId, @RequestParam String version) {
        if (otaRolloutService.isEligibleForUpdate(deviceId, version)) {
            return ResponseEntity.ok("Update available for " + version);
        }
        return ResponseEntity.ok("No updates available");
    }

    @PostMapping("/enable/{version}")
    public ResponseEntity<Void> enableForDevice(@PathVariable String version, @RequestParam long deviceId) {
        otaRolloutService.enableUpdateForDevice(deviceId, version);
        return ResponseEntity.ok().build();
    }
}
