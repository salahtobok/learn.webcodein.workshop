package com.webcodein.firmware.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "firmware_updates")
public class Firmware {

    @Id
    private UUID id;
    private String version;
    private String modelName;
    private String downloadUrl;
    private String checksum;
    private long fileSize;
    private Instant releaseDate;
    private boolean mandatory;

    protected Firmware() {}

    public Firmware(String version, String modelName, String downloadUrl, String checksum, long fileSize, boolean mandatory) {
        this.id = UUID.randomUUID();
        this.version = version;
        this.modelName = modelName;
        this.downloadUrl = downloadUrl;
        this.checksum = checksum;
        this.fileSize = fileSize;
        this.releaseDate = Instant.now();
        this.mandatory = mandatory;
    }

    public UUID getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public String getModelName() {
        return modelName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getChecksum() {
        return checksum;
    }

    public long getFileSize() {
        return fileSize;
    }

    public Instant getReleaseDate() {
        return releaseDate;
    }

    public boolean isMandatory() {
        return mandatory;
    }
}
