package cz.smarteon.loxone.system.status;

import jakarta.xml.bind.annotation.XmlAttribute;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a one wire device.
 */
public class OneWireDevice extends Device {

    @XmlAttribute(name = "Family") private String family; // TODO semantics??
    @XmlAttribute(name = "LastReceived") private String lastReceived; // TODO time
    @XmlAttribute(name = "TimeDiff") private String timeDiff; // TODO semantics??

    OneWireDevice() { }

    @Nullable
    public String getFamily() {
        return family;
    }

    @Nullable
    public String getLastReceived() {
        return lastReceived;
    }

    @Nullable
    public String getTimeDiff() {
        return timeDiff;
    }
}
