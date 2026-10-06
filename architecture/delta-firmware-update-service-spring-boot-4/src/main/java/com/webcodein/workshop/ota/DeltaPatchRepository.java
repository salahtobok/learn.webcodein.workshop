package com.webcodein.workshop.ota;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DeltaPatchRepository extends JpaRepository<DeltaPatch, Long> {
    Optional<DeltaPatch> findByFromVersionAndToVersion(FirmwareVersion from, FirmwareVersion to);
}
