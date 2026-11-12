package com.webcodein.firmware.service;

import com.webcodein.firmware.model.Firmware;
import com.webcodein.firmware.repository.FirmwareRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FirmwareServiceTest {

    @Mock
    private FirmwareRepository repository;

    private FirmwareService service;

    @BeforeEach
    void setUp() {
        service = new FirmwareService(repository);
    }

    @Test
    @DisplayName("Should return latest firmware update when found")
    void shouldReturnLatestUpdate() {
        Firmware firmware = new Firmware("2.5.0", "SensorNode", "update.bin", "check123", 1024L, false);
        when(repository.findTopByModelNameOrderByReleaseDateDesc("SensorNode"))
                .thenReturn(Optional.of(firmware));

        Optional<Firmware> result = service.getLatestUpdate("SensorNode");

        assertThat(result).isPresent();
        assertThat(result.get().getVersion()).isEqualTo("2.5.0");
        assertThat(result.get().getModelName()).isEqualTo("SensorNode");
    }

    @Test
    @DisplayName("Should return empty optional when no update found for model")
    void shouldReturnEmptyWhenNoUpdate() {
        when(repository.findTopByModelNameOrderByReleaseDateDesc("UnknownDevice"))
                .thenReturn(Optional.empty());

        Optional<Firmware> result = service.getLatestUpdate("UnknownDevice");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should throw exception when firmware ID not in database")
    void shouldThrowWhenFirmwareNotFound() {
        UUID randomId = UUID.randomUUID();
        when(repository.findById(randomId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadFirmwareAsResource(randomId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Firmware not found");
    }

    @Test
    @DisplayName("Should throw exception when binary file missing on disk")
    void shouldThrowWhenFileMissingOnDisk() {
        UUID id = UUID.randomUUID();
        Firmware firmware = new Firmware("1.0.0", "ModelA", "non_existent_file.bin", "hash", 100L, false);
        when(repository.findById(id)).thenReturn(Optional.of(firmware));

        assertThatThrownBy(() -> service.loadFirmwareAsResource(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("File not found");
    }

    @Test
    @DisplayName("Should return Resource when binary file exists in data directory")
    void shouldReturnResourceWhenFileExists() throws Exception {
        Path storageDir = Paths.get("data/firmware");
        Files.createDirectories(storageDir);
        Path testFile = storageDir.resolve("valid_firmware.bin");
        Files.write(testFile, new byte[]{0x01, 0x02, 0x03});

        try {
            UUID id = UUID.randomUUID();
            Firmware firmware = new Firmware("1.0.0", "ModelA", "valid_firmware.bin", "hash", 3L, false);
            when(repository.findById(id)).thenReturn(Optional.of(firmware));

            Resource resource = service.loadFirmwareAsResource(id);

            assertThat(resource).isNotNull();
            assertThat(resource.exists()).isTrue();
            assertThat(resource.contentLength()).isEqualTo(3L);
        } finally {
            Files.deleteIfExists(testFile);
        }
    }
}
