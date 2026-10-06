package com.webcodein.workshop.ota;

import jakarta.persistence.*;

@Entity
@Table(name = "delta_patches")
public class DeltaPatch {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "from_version_id", nullable = false)
    private FirmwareVersion fromVersion;
    
    @ManyToOne
    @JoinColumn(name = "to_version_id", nullable = false)
    private FirmwareVersion toVersion;
    
    @Column(nullable = false)
    private String patchObjectKey;
    
    @Column(nullable = false)
    private Long sizeBytes;
    
    @Column(nullable = false)
    private String patchSha256;

    public DeltaPatch() {}

    public DeltaPatch(FirmwareVersion fromVersion, FirmwareVersion toVersion, String patchObjectKey, Long sizeBytes, String patchSha256) {
        this.fromVersion = fromVersion;
        this.toVersion = toVersion;
        this.patchObjectKey = patchObjectKey;
        this.sizeBytes = sizeBytes;
        this.patchSha256 = patchSha256;
    }

    public Long getId() { return id; }
    public FirmwareVersion getFromVersion() { return fromVersion; }
    public FirmwareVersion getToVersion() { return toVersion; }
    public String getPatchObjectKey() { return patchObjectKey; }
    public Long getSizeBytes() { return sizeBytes; }
    public String getPatchSha256() { return patchSha256; }
}
