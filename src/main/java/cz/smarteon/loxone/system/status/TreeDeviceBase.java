package cz.smarteon.loxone.system.status;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.util.List;

class TreeDeviceBase extends UpdatableDevice {

    @XmlAttribute(name = "Place") protected String place;
    @XmlAttribute(name = "Inst") protected String installation;
    @XmlAttribute(name = "Version") protected String version;
    @XmlAttribute(name = "Online") protected Boolean online;
    @XmlAttribute(name = "LastReceived") protected String lastReceived; // TODO time
    @XmlAttribute(name = "TimeDiff") protected Integer timeDiff; // TODO semantics??
    @XmlAttribute(name = "DummyDev") protected Boolean dummy;

    @XmlAttribute(name = "HwVersion") protected String hwVersion;
    @XmlAttribute(name = "Mac") protected String mac;
    @XmlAttribute(name = "Occupied") protected Boolean occupied;
    @XmlAttribute(name = "Interfered") protected Boolean interfered;
    @XmlElement(name = "AirDevice") @XmlJavaTypeAdapter(AirDeviceAdapter.class) protected List<AirDevice> airDevices;

    TreeDeviceBase() {}

    TreeDeviceBase(final String code,
                   final String name,
                   final String serialNumber,
                   final String place,
                   final String installation,
                   final String version,
                   final Boolean online,
                   final String lastReceived,
                   final Integer timeDiff,
                   final Boolean dummy,
                   final Boolean updating,
                   final Integer updateProgress,
                   final String hwVersion,
                   final String mac,
                   final Boolean occupied,
                   final Boolean interfered,
                   final List<AirDevice> airDevices) {
        super(code, name, serialNumber, updating, updateProgress);
        this.place = place;
        this.installation = installation;
        this.version = version;
        this.online = online;
        this.lastReceived = lastReceived;
        this.timeDiff = timeDiff;
        this.dummy = dummy;
        this.hwVersion = hwVersion;
        this.mac = mac;
        this.occupied = occupied;
        this.interfered = interfered;
        this.airDevices = airDevices;
    }
}
