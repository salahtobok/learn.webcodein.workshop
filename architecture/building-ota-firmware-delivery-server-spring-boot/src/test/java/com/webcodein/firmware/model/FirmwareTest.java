package com.webcodein.firmware.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class FirmwareTest {

    @Test
    @DisplayName("Should create firmware update with UUID and current releaseDate")
    void shouldCreateFirmware() {
        Instant before = Instant.now().minusSeconds(1);

        Firmware firmware = new Firmware(
                "3.1.0",
                "IoT-Gateway-Pro",
                "firmware-3.1.0.bin",
                "sha256:e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
                204800L,
                true
        );

        assertThat(firmware.getId()).isNotNull();
        assertThat(firmware.getVersion()).isEqualTo("3.1.0");
        assertThat(firmware.getModelName()).isEqualTo("IoT-Gateway-Pro");
        assertThat(firmware.getDownloadUrl()).isEqualTo("firmware-3.1.0.bin");
        assertThat(firmware.getChecksum()).contains("sha256:");
        assertThat(firmware.getFileSize()).isEqualTo(204800L);
        assertThat(firmware.isMandatory()).isTrue();
        assertThat(firmware.getReleaseDate()).isAfterOrEqualTo(before);
    }

    @Test
    @DisplayName("Default constructor should instantiate empty firmware for JPA")
    void defaultConstructor() {
        Firmware firmware = new Firmware();
        assertThat(firmware.getId()).isNull();
        assertThat(firmware.getVersion()).isNull();
        assertThat(firmware.getModelName()).isNull();
        assertThat(firmware.getDownloadUrl()).isNull();
        assertThat(firmware.getChecksum()).isNull();
        assertThat(firmware.getFileSize()).isEqualTo(0L);
        assertThat(firmware.isMandatory()).isFalse();
        assertThat(firmware.getReleaseDate()).isNull();
    }
}
