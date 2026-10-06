package com.webcodein.workshop.ota;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "firmware_versions")
public class FirmwareVersion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String version;
    
    @Column(nullable = false)
    private String fullRomSha256;
    
    @Column(nullable = false)
    private Instant releaseDate;

    public FirmwareVersion() {}

    public FirmwareVersion(String version, String fullRomSha256, Instant releaseDate) {
        this.version = version;
        this.fullRomSha256 = fullRomSha256;
        this.releaseDate = releaseDate;
    }

    public Long getId() { return id; }
    public String getVersion() { return version; }
    public String getFullRomSha256() { return fullRomSha256; }
    public Instant getReleaseDate() { return releaseDate; }
}
