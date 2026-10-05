package com.webcodein.firmware.controller;

import com.webcodein.firmware.model.Firmware;
import com.webcodein.firmware.service.FirmwareService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/firmware")
public class FirmwareController {

    private final FirmwareService service;

    public FirmwareController(FirmwareService service) {
        this.service = service;
    }

    @GetMapping("/latest")
    public ResponseEntity<Firmware> checkLatestUpdate(@RequestParam String modelName) {
        Optional<Firmware> latest = service.getLatestUpdate(modelName);
        return latest.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFirmware(@PathVariable UUID id) {
        Resource resource = service.loadFirmwareAsResource(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
