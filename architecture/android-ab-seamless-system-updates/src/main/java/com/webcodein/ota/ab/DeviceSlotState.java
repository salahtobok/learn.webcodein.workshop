package com.webcodein.ota.ab;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "device_slot_states")
public class DeviceSlotState {
    @Id
    private String deviceId;

    @jakarta.persistence.Version
    private Long version;

    private String activeSlot = "A";

    private boolean bootableA = true;
    private boolean successfulA = true;
    private String versionA = "1.0.0";

    private boolean bootableB = false;
    private boolean successfulB = false;
    private String versionB = "";

    public DeviceSlotState() {}

    public DeviceSlotState(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public Long getVersion() { return version; }
    
    public String getActiveSlot() { return activeSlot; }
    public void setActiveSlot(String activeSlot) { this.activeSlot = activeSlot; }
    
    public boolean isBootableA() { return bootableA; }
    public void setBootableA(boolean bootableA) { this.bootableA = bootableA; }
    
    public boolean isSuccessfulA() { return successfulA; }
    public void setSuccessfulA(boolean successfulA) { this.successfulA = successfulA; }
    
    public String getVersionA() { return versionA; }
    public void setVersionA(String versionA) { this.versionA = versionA; }
    
    public boolean isBootableB() { return bootableB; }
    public void setBootableB(boolean bootableB) { this.bootableB = bootableB; }
    
    public boolean isSuccessfulB() { return successfulB; }
    public void setSuccessfulB(boolean successfulB) { this.successfulB = successfulB; }
    
    public String getVersionB() { return versionB; }
    public void setVersionB(String versionB) { this.versionB = versionB; }

    public String getInactiveSlot() {
        return "A".equals(activeSlot) ? "B" : "A";
    }

    public void setSlotBootable(String slot, String version) {
        if ("A".equals(slot)) {
            this.bootableA = true;
            this.successfulA = false;
            this.versionA = version;
        } else {
            this.bootableB = true;
            this.successfulB = false;
            this.versionB = version;
        }
    }

    public void setSlotSuccessful(String slot) {
        if ("A".equals(slot)) {
            this.successfulA = true;
        } else {
            this.successfulB = true;
        }
    }
}
