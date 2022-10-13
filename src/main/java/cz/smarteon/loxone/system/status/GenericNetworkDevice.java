package cz.smarteon.loxone.system.status;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class GenericNetworkDevice {

    @XmlAttribute(name = "Type") private String type;
    @XmlAttribute(name = "Name") private String name;
    @XmlAttribute(name = "SubType") private String subType;
    @XmlAttribute(name = "Place") private String place;
    @XmlAttribute(name = "Online") private Boolean online;
    @XmlAttribute(name = "MAC") private String mac;
    @XmlAttribute(name = "Version") private String version;

    @XmlElement(name = "Extension") @XmlJavaTypeAdapter(ExtensionAdapter.class)
    private List<Extension> extensions;

    @Nullable
    public String getType() {
        return type;
    }

    @Nullable
    public String getName() {
        return name;
    }

    @Nullable
    public String getSubType() {
        return subType;
    }

    @Nullable
    public String getPlace() {
        return place;
    }

    public boolean isOnline() {
        return Boolean.TRUE.equals(online);
    }

    @Nullable
    public String getMac() {
        return mac;
    }

    @Nullable
    public String getVersion() {
        return version;
    }

    @NotNull
    public List<Extension> getExtensions() {
        return extensions != null ? extensions : Collections.emptyList();
    }
}
