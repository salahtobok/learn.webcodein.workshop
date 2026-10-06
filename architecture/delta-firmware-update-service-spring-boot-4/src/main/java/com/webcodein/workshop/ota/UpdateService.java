package com.webcodein.workshop.ota;

import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UpdateService {
    
    private final FirmwareVersionRepository versionRepo;
    private final DeltaPatchRepository patchRepo;

    public UpdateService(FirmwareVersionRepository versionRepo, DeltaPatchRepository patchRepo) {
        this.versionRepo = versionRepo;
        this.patchRepo = patchRepo;
    }

    public Optional<UpdateResponse> findUpdate(String currentVersion, String deviceModel) {
        Optional<FirmwareVersion> latestOpt = versionRepo.findTopByOrderByReleaseDateDesc();
        if (latestOpt.isEmpty()) return Optional.empty();
        
        FirmwareVersion latest = latestOpt.get();
        if (latest.getVersion().equals(currentVersion)) {
            return Optional.empty(); // Device is up to date
        }

        Optional<FirmwareVersion> currentOpt = versionRepo.findByVersion(currentVersion);
        if (currentOpt.isEmpty()) {
            return Optional.empty(); // Unknown version
        }
        
        FirmwareVersion current = currentOpt.get();
        
        // Find if a delta patch exists between current and latest
        Optional<DeltaPatch> patchOpt = patchRepo.findByFromVersionAndToVersion(current, latest);
        
        if (patchOpt.isPresent()) {
            DeltaPatch patch = patchOpt.get();
            // In a real app, generate a pre-signed S3 URL here
            String downloadUrl = "https://s3.amazonaws.com/ota-bucket/" + patch.getPatchObjectKey();
            
            return Optional.of(new UpdateResponse(
                latest.getVersion(),
                downloadUrl,
                patch.getPatchSha256(),
                patch.getSizeBytes(),
                true
            ));
        }
        
        // Fallback to full ROM if no delta patch exists
        String fullUrl = "https://s3.amazonaws.com/ota-bucket/full-rom-" + latest.getVersion() + ".zip";
        return Optional.of(new UpdateResponse(
            latest.getVersion(),
            fullUrl,
            latest.getFullRomSha256(),
            2000000000L, // 2GB fake size
            true
        ));
    }
}
