package cz.smarteon.loxone.system.status;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import java.util.List;

class AirDeviceBase extends UpdatableDevice {

    @XmlAttribute(name = "Type") protected String type;
    @XmlAttribute(name = "Place") protected String place;
    @XmlAttribute(name = "Inst") protected String installation;
    @XmlAttribute(name = "LastReceived") protected String lastReceived; // TODO time
    @XmlAttribute(name = "TimeDiff") protected Integer timeDiff; // TODO semantics??
    @XmlAttribute(name = "Version") protected String version;
    @XmlAttribute(name = "MinVersion") protected String minVersion; // TODO semantics??
    @XmlAttribute(name = "HwVersion") protected String hwVersion;
    @XmlAttribute(name = "Hops") protected Integer hops;
    @XmlAttribute(name = "RoundTripTime") protected Integer roundTripTime;
    @XmlAttribute(name = "QualityExt") protected String qualityExt; // TODO semantics??
    @XmlAttribute(name = "QualityDev") protected String qualityDev; // TODO semantics??
    @XmlAttribute(name = "Online") protected Boolean online;
    @XmlAttribute(name = "Battery") protected Integer battery;
    @XmlAttribute(name = "DummyDev") protected Boolean dummy;
    @XmlElement(name = "OneWireDevice") protected List<OneWireDevice> oneWireDevices;

    AirDeviceBase() {}

    AirDeviceBase(final String type,
              final String code,
              final String name,
              final String place,
              final String installation,
              final String serialNumber,
              final String lastReceived,
              final Integer timeDiff,
              final String version,
              final String minVersion,
              final String hwVersion,
              final Integer hops,
              final Integer roundTripTime,
              final String qualityExt,
              final String qualityDev,
              final Boolean online,
              final Integer battery,
              final Boolean dummy,
              final Boolean updating,
              final Integer updateProgress,
              final List<OneWireDevice> oneWireDevices) {
        super(code, name, serialNumber, updating, updateProgress);
        this.type = type;
        this.place = place;
        this.installation = installation;
        this.lastReceived = lastReceived;
        this.timeDiff = timeDiff;
        this.version = version;
        this.minVersion = minVersion;
        this.hwVersion = hwVersion;
        this.hops = hops;
        this.roundTripTime = roundTripTime;
        this.qualityExt = qualityExt;
        this.qualityDev = qualityDev;
        this.online = online;
        this.battery = battery;
        this.dummy = dummy;
        this.oneWireDevices = oneWireDevices;
    }
}
