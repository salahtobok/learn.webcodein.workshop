package com.webcodein.firmware.service;

import com.webcodein.firmware.model.Firmware;
import com.webcodein.firmware.repository.FirmwareRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
public class FirmwareService {

    private final FirmwareRepository repository;
    private final Path storageLocation = Paths.get("data/firmware").toAbsolutePath().normalize();

    public FirmwareService(FirmwareRepository repository) {
        this.repository = repository;
    }

    public Optional<Firmware> getLatestUpdate(String modelName) {
        return repository.findTopByModelNameOrderByReleaseDateDesc(modelName);
    }

    public Resource loadFirmwareAsResource(UUID firmwareId) {
        Firmware firmware = repository.findById(firmwareId)
                .orElseThrow(() -> new RuntimeException("Firmware not found"));

        try {
            Path filePath = storageLocation.resolve(firmware.getDownloadUrl()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found");
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found", ex);
        }
    }
}
