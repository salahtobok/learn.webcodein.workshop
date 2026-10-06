package com.webcodein.workshop.ota;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FirmwareVersionRepository extends JpaRepository<FirmwareVersion, Long> {
    Optional<FirmwareVersion> findByVersion(String version);
    Optional<FirmwareVersion> findTopByOrderByReleaseDateDesc();
}
