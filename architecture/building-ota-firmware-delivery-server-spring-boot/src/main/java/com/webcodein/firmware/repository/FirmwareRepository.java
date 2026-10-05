package com.webcodein.firmware.repository;

import com.webcodein.firmware.model.Firmware;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FirmwareRepository extends JpaRepository<Firmware, UUID> {
    
    Optional<Firmware> findTopByModelNameOrderByReleaseDateDesc(String modelName);
}
