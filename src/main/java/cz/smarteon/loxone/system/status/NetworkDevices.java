package cz.smarteon.loxone.system.status;

import jakarta.xml.bind.annotation.XmlElement;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NetworkDevices {

    @XmlElement(name = "GenericNetworkDevice")
    private List<GenericNetworkDevice> genericDevices;

    @Nullable
    public List<GenericNetworkDevice> getGenericDevices() {
        return genericDevices;
    }
}
