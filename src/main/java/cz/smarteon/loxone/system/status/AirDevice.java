package cz.smarteon.loxone.system.status;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.jetbrains.annotations.Nullable;

@JsonDeserialize(using = AirDeviceDeserializer.class)
public class AirDevice extends AirDeviceBase {

    AirDevice(final AirDeviceBase ad) {
        super(ad.type, ad.getCode(), ad.getName(), ad.place, ad.installation, ad.getSerialNumber(),
                ad.lastReceived, ad.timeDiff, ad.version, ad.minVersion, ad.hwVersion, ad.hops, ad.roundTripTime,
                ad.qualityExt, ad.qualityDev, ad.online, ad.battery, ad.dummy, ad.updating, ad.updateProgress,
                ad.oneWireDevices);
    }

    @Nullable
    public String getType() {
        return type;
    }

    @Nullable
    public String getPlace() {
        return place;
    }

    @Nullable
    public String getInstallation() {
        return installation;
    }

    @Nullable
    public String getLastReceived() {
        return lastReceived;
    }

    @Nullable
    public Integer getTimeDiff() {
        return timeDiff;
    }

    @Nullable
    public String getVersion() {
        return version;
    }

    @Nullable
    public String getMinVersion() {
        return minVersion;
    }

    @Nullable
    public String getHwVersion() {
        return hwVersion;
    }

    @Nullable
    public Integer getHops() {
        return hops;
    }

    @Nullable
    public Integer getRoundTripTime() {
        return roundTripTime;
    }

    @Nullable
    public String getQualityExt() {
        return qualityExt;
    }

    @Nullable
    public String getQualityDev() {
        return qualityDev;
    }

    public boolean isOnline() {
        return Boolean.TRUE.equals(online);
    }

    @Nullable
    public Integer getBattery() {
        return battery;
    }

    public boolean getDummy() {
        return Boolean.TRUE.equals(dummy);
    }
}
