package com.webcodein.workshop.ota;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class DeltaPatchTest {

    @Test
    @DisplayName("Should correctly instantiate and expose DeltaPatch attributes")
    void shouldCreateDeltaPatchWithAttributes() {
        Instant now = Instant.now();
        FirmwareVersion v1 = new FirmwareVersion("1.0.0", "sha-full-100", now.minusSeconds(3600));
        FirmwareVersion v2 = new FirmwareVersion("1.1.0", "sha-full-110", now);

        DeltaPatch patch = new DeltaPatch(v1, v2, "patches/patch-1.0-to-1.1.bsdiff", 15_728_640L, "sha256-patch-xyz");

        assertThat(patch.getFromVersion()).isEqualTo(v1);
        assertThat(patch.getToVersion()).isEqualTo(v2);
        assertThat(patch.getPatchObjectKey()).isEqualTo("patches/patch-1.0-to-1.1.bsdiff");
        assertThat(patch.getSizeBytes()).isEqualTo(15_728_640L);
        assertThat(patch.getPatchSha256()).isEqualTo("sha256-patch-xyz");
        assertThat(patch.getId()).isNull();
    }

    @Test
    @DisplayName("Should instantiate default constructor for JPA reflection")
    void shouldSupportDefaultConstructor() {
        DeltaPatch patch = new DeltaPatch();
        assertThat(patch.getId()).isNull();
        assertThat(patch.getFromVersion()).isNull();
        assertThat(patch.getToVersion()).isNull();
        assertThat(patch.getPatchObjectKey()).isNull();
        assertThat(patch.getSizeBytes()).isNull();
        assertThat(patch.getPatchSha256()).isNull();
    }
}
