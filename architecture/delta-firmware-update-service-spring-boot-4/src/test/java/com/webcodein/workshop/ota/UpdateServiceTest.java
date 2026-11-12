package com.webcodein.workshop.ota;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateServiceTest {

    @Mock
    private FirmwareVersionRepository versionRepo;

    @Mock
    private DeltaPatchRepository patchRepo;

    @InjectMocks
    private UpdateService updateService;

    private FirmwareVersion v1;
    private FirmwareVersion v2;

    @BeforeEach
    void setUp() {
        v1 = new FirmwareVersion("1.0.0", "sha-full-100", Instant.parse("2026-01-01T00:00:00Z"));
        v2 = new FirmwareVersion("1.1.0", "sha-full-110", Instant.parse("2026-06-01T00:00:00Z"));
    }

    @Test
    @DisplayName("Should return delta patch when patch exists between current and latest versions")
    void shouldReturnDeltaPatchWhenAvailable() {
        when(versionRepo.findTopByOrderByReleaseDateDesc()).thenReturn(Optional.of(v2));
        when(versionRepo.findByVersion("1.0.0")).thenReturn(Optional.of(v1));

        DeltaPatch patch = new DeltaPatch(v1, v2, "patches/patch-1.0-to-1.1.bsdiff", 15_000_000L, "sha256-delta-123");
        when(patchRepo.findByFromVersionAndToVersion(v1, v2)).thenReturn(Optional.of(patch));

        Optional<UpdateResponse> response = updateService.findUpdate("1.0.0", "PixelTablet");

        assertThat(response).isPresent();
        assertThat(response.get().targetVersion()).isEqualTo("1.1.0");
        assertThat(response.get().downloadUrl()).isEqualTo("https://s3.amazonaws.com/ota-bucket/patches/patch-1.0-to-1.1.bsdiff");
        assertThat(response.get().patchSha256()).isEqualTo("sha256-delta-123");
        assertThat(response.get().sizeBytes()).isEqualTo(15_000_000L);
        assertThat(response.get().isCritical()).isTrue();
    }

    @Test
    @DisplayName("Should fallback to full ROM when no delta patch exists between versions")
    void shouldFallbackToFullRomWhenNoDeltaExists() {
        when(versionRepo.findTopByOrderByReleaseDateDesc()).thenReturn(Optional.of(v2));
        when(versionRepo.findByVersion("1.0.0")).thenReturn(Optional.of(v1));
        when(patchRepo.findByFromVersionAndToVersion(v1, v2)).thenReturn(Optional.empty());

        Optional<UpdateResponse> response = updateService.findUpdate("1.0.0", "PixelTablet");

        assertThat(response).isPresent();
        assertThat(response.get().targetVersion()).isEqualTo("1.1.0");
        assertThat(response.get().downloadUrl()).isEqualTo("https://s3.amazonaws.com/ota-bucket/full-rom-1.1.0.zip");
        assertThat(response.get().patchSha256()).isEqualTo("sha-full-110");
        assertThat(response.get().sizeBytes()).isEqualTo(2_000_000_000L);
        assertThat(response.get().isCritical()).isTrue();
    }

    @Test
    @DisplayName("Should return empty optional when device is already running the latest version")
    void shouldReturnEmptyWhenDeviceAlreadyUpToDate() {
        when(versionRepo.findTopByOrderByReleaseDateDesc()).thenReturn(Optional.of(v2));

        Optional<UpdateResponse> response = updateService.findUpdate("1.1.0", "PixelTablet");

        assertThat(response).isEmpty();
        verify(patchRepo, never()).findByFromVersionAndToVersion(any(), any());
    }

    @Test
    @DisplayName("Should return empty optional when current version is not found in catalog")
    void shouldReturnEmptyWhenCurrentVersionUnknown() {
        when(versionRepo.findTopByOrderByReleaseDateDesc()).thenReturn(Optional.of(v2));
        when(versionRepo.findByVersion("0.9.0")).thenReturn(Optional.empty());

        Optional<UpdateResponse> response = updateService.findUpdate("0.9.0", "PixelTablet");

        assertThat(response).isEmpty();
        verify(patchRepo, never()).findByFromVersionAndToVersion(any(), any());
    }

    @Test
    @DisplayName("Should return empty optional when repository has no firmware versions")
    void shouldReturnEmptyWhenNoReleasesExist() {
        when(versionRepo.findTopByOrderByReleaseDateDesc()).thenReturn(Optional.empty());

        Optional<UpdateResponse> response = updateService.findUpdate("1.0.0", "PixelTablet");

        assertThat(response).isEmpty();
        verify(versionRepo, never()).findByVersion(any());
    }
}
