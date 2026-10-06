package com.webcodein.ota.ab;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceSlotStateRepository extends JpaRepository<DeviceSlotState, String> {
}
