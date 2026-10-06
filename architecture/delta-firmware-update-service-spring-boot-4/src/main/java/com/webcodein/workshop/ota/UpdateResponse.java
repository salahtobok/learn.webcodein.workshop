package com.webcodein.workshop.ota;

public record UpdateResponse(
    String targetVersion,
    String downloadUrl,
    String patchSha256,
    Long sizeBytes,
    boolean isCritical
) {}
