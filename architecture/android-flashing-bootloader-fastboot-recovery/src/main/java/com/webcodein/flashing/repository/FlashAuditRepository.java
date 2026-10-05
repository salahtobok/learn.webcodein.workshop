package com.webcodein.flashing.repository;

import com.webcodein.flashing.model.FlashAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FlashAuditRepository extends JpaRepository<FlashAudit, UUID> {
    List<FlashAudit> findByDeviceSerialNumber(String deviceSerialNumber);
}
