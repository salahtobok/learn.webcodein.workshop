package com.webcodein.workshop.ota;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class FirmwareVersionTest {

    @Test
    @DisplayName("Should correctly instantiate and expose FirmwareVersion attributes")
    void shouldCreateFirmwareVersionWithAttributes() {
        Instant release = Instant.parse("2026-10-15T12:00:00Z");
        FirmwareVersion version = new FirmwareVersion("2.0.0", "sha256-full-rom-200", release);

        assertThat(version.getId()).isNull();
        assertThat(version.getVersion()).isEqualTo("2.0.0");
        assertThat(version.getFullRomSha256()).isEqualTo("sha256-full-rom-200");
        assertThat(version.getReleaseDate()).isEqualTo(release);
    }

    @Test
    @DisplayName("Should instantiate default constructor for JPA reflection")
    void shouldSupportDefaultConstructor() {
        FirmwareVersion version = new FirmwareVersion();
        assertThat(version.getId()).isNull();
        assertThat(version.getVersion()).isNull();
        assertThat(version.getFullRomSha256()).isNull();
        assertThat(version.getReleaseDate()).isNull();
    }
}
