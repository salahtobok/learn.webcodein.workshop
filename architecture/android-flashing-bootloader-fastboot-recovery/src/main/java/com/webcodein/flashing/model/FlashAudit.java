package com.webcodein.flashing.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "flash_audits")
public class FlashAudit {

    @Id
    private UUID id;
    private String deviceSerialNumber;
    private String partitionName;
    private String imageChecksum;
    private boolean successful;
    private Instant flashedAt;

    protected FlashAudit() {}

    public FlashAudit(String deviceSerialNumber, String partitionName, String imageChecksum, boolean successful) {
        this.id = UUID.randomUUID();
        this.deviceSerialNumber = deviceSerialNumber;
        this.partitionName = partitionName;
        this.imageChecksum = imageChecksum;
        this.successful = successful;
        this.flashedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getDeviceSerialNumber() { return deviceSerialNumber; }
    public String getPartitionName() { return partitionName; }
    public String getImageChecksum() { return imageChecksum; }
    public boolean isSuccessful() { return successful; }
    public Instant getFlashedAt() { return flashedAt; }
}
