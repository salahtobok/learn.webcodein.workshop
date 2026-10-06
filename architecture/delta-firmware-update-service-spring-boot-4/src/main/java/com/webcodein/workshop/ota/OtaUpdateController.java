package com.webcodein.workshop.ota;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ota")
public class OtaUpdateController {

    private final UpdateService updateService;

    public OtaUpdateController(UpdateService updateService) {
        this.updateService = updateService;
    }

    @GetMapping("/check")
    public ResponseEntity<UpdateResponse> checkForUpdate(
            @RequestParam String currentVersion,
            @RequestParam String deviceModel) {
            
        return updateService.findUpdate(currentVersion, deviceModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}
