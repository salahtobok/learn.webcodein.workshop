package com.webcodein.flashing.controller;

import com.webcodein.flashing.model.FlashAudit;
import com.webcodein.flashing.repository.FlashAuditRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class FlashAuditController {

    private final FlashAuditRepository repository;

    public FlashAuditController(FlashAuditRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<FlashAudit> logFlashEvent(@RequestBody FlashAudit audit) {
        FlashAudit saved = repository.save(
            new FlashAudit(
                audit.getDeviceSerialNumber(),
                audit.getPartitionName(),
                audit.getImageChecksum(),
                audit.isSuccessful()
            )
        );
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{serialNumber}")
    public ResponseEntity<List<FlashAudit>> getDeviceHistory(@PathVariable String serialNumber) {
        return ResponseEntity.ok(repository.findByDeviceSerialNumber(serialNumber));
    }
}
