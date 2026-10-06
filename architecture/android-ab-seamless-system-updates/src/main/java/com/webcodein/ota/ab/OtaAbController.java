package com.webcodein.ota.ab;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/devices")
public class OtaAbController {

    private final DeviceSlotStateRepository repository;

    public OtaAbController(DeviceSlotStateRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{deviceId}/state")
    public DeviceSlotState getState(@PathVariable String deviceId) {
        return repository.findById(deviceId)
                .orElseGet(() -> repository.save(new DeviceSlotState(deviceId)));
    }

    @PostMapping("/{deviceId}/stage-update")
    public ResponseEntity<DeviceSlotState> stageUpdate(@PathVariable String deviceId, @RequestBody Map<String, String> payload) {
        DeviceSlotState state = getState(deviceId);
        String targetVersion = payload.get("version");
        String inactiveSlot = state.getInactiveSlot();
        
        // Simulating the device flashing the inactive slot in the background
        state.setSlotBootable(inactiveSlot, targetVersion);
        repository.save(state);
        
        return ResponseEntity.ok(state);
    }

    @PostMapping("/{deviceId}/reboot")
    public ResponseEntity<DeviceSlotState> simulateReboot(@PathVariable String deviceId) {
        DeviceSlotState state = getState(deviceId);
        String inactiveSlot = state.getInactiveSlot();
        
        // Flip the active slot to the newly flashed slot
        state.setActiveSlot(inactiveSlot);
        repository.save(state);
        
        return ResponseEntity.ok(state);
    }

    @PostMapping("/{deviceId}/mark-successful")
    public ResponseEntity<DeviceSlotState> markSuccessful(@PathVariable String deviceId) {
        DeviceSlotState state = getState(deviceId);
        
        // Device successfully booted, marks the current active slot as successful
        state.setSlotSuccessful(state.getActiveSlot());
        repository.save(state);
        
        return ResponseEntity.ok(state);
    }

    @PostMapping("/{deviceId}/fallback")
    public ResponseEntity<DeviceSlotState> simulateFallback(@PathVariable String deviceId) {
        DeviceSlotState state = getState(deviceId);
        
        // Device failed to boot, bootloader rolls back to the other slot
        String fallbackSlot = state.getInactiveSlot();
        state.setActiveSlot(fallbackSlot);
        
        // Mark the failing slot as unbootable
        if ("A".equals(state.getInactiveSlot())) {
            state.setBootableA(false);
        } else {
            state.setBootableB(false);
        }
        
        repository.save(state);
        return ResponseEntity.ok(state);
    }
}
