package cz.smarteon.loxone.system.status;

import cz.smarteon.loxone.LoxoneNotDocumented;
import cz.smarteon.loxone.PercentDoubleAdapter;
import cz.smarteon.loxone.app.MiniserverType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents {@link cz.smarteon.loxone.Command#DATA_STATUS} payload. Only intended for deserialization.
 */
@LoxoneNotDocumented
@XmlRootElement(name = "Status")
@XmlAccessorType(XmlAccessType.FIELD)
public class MiniserverStatus {

    @XmlAttribute(name = "Modified")
    private String modified; // TODO migrate to datetime

    @XmlElement(name = "Miniserver")
    private Content content;

    @XmlElement(name = "NetworkDevices")
    private List<NetworkDevices> networkDevices;

    MiniserverStatus() { }

    @Nullable
    public String getModified() {
        return modified;
    }

    /**
     * Miniserver type.
     * @return type of miniserver
     */
    @NotNull
    public MiniserverType getType() {
        return MiniserverType.fromValue(content.type);
    }

    /**
     * Miniserver name.
     * @return name
     */
    @Nullable
    public String getName() {
        return content.name;
    }

    /**
     * Miniserver IP address.
     * @return IP address
     */
    @Nullable
    public String getIp() {
        return content.ip;
    }

    /**
     * Miniserver network address mask.
     * @return network mask
     */
    @Nullable
    public String getMask() {
        return content.mask;
    }

    /**
     * Network gateway miniserver uses.
     * @return gateway
     */
    @Nullable
    public String getGateway() {
        return content.gateway;
    }

    /**
     * Whether the network was set by DHCP.
     * @return true if DHCP used, false otherwise
     */
    public boolean usesDhcp() {
        return Boolean.TRUE.equals(content.dhcp);
    }

    /**
     * Assigned DNS server 1.
     * @return DNS server address
     */
    @Nullable
    public String getDns1() {
        return content.dns1;
    }

    /**
     * Assigned DNS server 2.
     * @return DNS server address
     */
    @Nullable
    public String getDns2() {
        return content.dns2;
    }

    /**
     * Miniserver's MAC address.
     * @return mac address
     */
    @Nullable
    public String getMac() {
        return content.mac;
    }

    /**
     * Miniserver device label.
     * @return device
     */
    @Nullable
    public String getDevice() {
        return content.device;
    }

    /**
     * Miniserver version.
     * @return version
     */
    @Nullable
    public String getVersion() {
        return content.version;
    }

    /**
     * Percentage of LAN errors.
     * <ul>
     *     <li>Miniserver Gen1 and Go always return 0 - it's not measured at all</li>
     *     <li>Miniserver Gen2 always returns 100 - it's reported BUG to be fixed by Loxone</li>
     * </ul>
     * @return LAN errors
     */
    @Nullable
    public Double getLanErrorsPercent() {
        return content.lanErrorsPercent;
    }

    /**
     * Count of loxone link errors.
     * @return loxone link errors
     */
    @Nullable
    public Integer getLinkErrorsCount() {
        return content.linkErrorsCount;
    }

    /**
     * List of configured extensions.
     * @return configured extensions or empty list
     */
    @NotNull
    public List<Extension> getExtensions() {
        final List<Extension> allExtensions = content.getExtensions();
        getNetworkDevices().forEach(nd -> {
            if (nd.getGenericDevices() != null) {
                nd.getGenericDevices().stream()
                        .map(GenericNetworkDevice::getExtensions)
                        .forEach(allExtensions::addAll);

            }
        });
        return allExtensions;
    }

    /**
     * List of extensions of given type.
     * @param extensionType extension type class
     * @param <T> extension type
     * @return configured extensions of given type or empty list
     */
    @NotNull
    public <T extends Extension> List<T> getExtensions(Class<T> extensionType) {
        return getExtensions().stream()
                .filter(e -> extensionType.isAssignableFrom(e.getClass()))
                .map(extensionType::cast)
                .collect(Collectors.toList());
    }

    /**
     * List of configured network devices.
     * @return configured network devices
     */
    @NotNull
    public List<NetworkDevices> getNetworkDevices() {
        final List<NetworkDevices> result =
                networkDevices != null ? new ArrayList<>(networkDevices) : new ArrayList<>();
        if (content.networkDevices != null) {
            result.addAll(content.networkDevices);
        }
        return result;
    }


    @XmlAccessorType(XmlAccessType.FIELD)
    private static class Content {

        @XmlAttribute(name = "Type")
        private Integer type;
        @XmlAttribute(name = "Name")
        private String name;
        @XmlAttribute(name = "IP")
        private String ip;
        @XmlAttribute(name = "Mask")
        private String mask;
        @XmlAttribute(name = "Gateway")
        private String gateway;
        @XmlAttribute(name = "DHCP")
        private Boolean dhcp;
        @XmlAttribute(name = "DNS1")
        private String dns1;
        @XmlAttribute(name = "DNS2")
        private String dns2;
        @XmlAttribute(name = "MAC")
        private String mac;
        @XmlAttribute(name = "Device")
        private String device;
        @XmlAttribute(name = "Version")
        private String version;
        @XmlAttribute(name = "LANErrors") @XmlJavaTypeAdapter(PercentDoubleAdapter.class)
        private Double lanErrorsPercent;
        @XmlAttribute(name = "LinkErrors")
        private Integer linkErrorsCount;
        @XmlElement(name = "Extension") @XmlJavaTypeAdapter(ExtensionAdapter.class)
        private List<Extension> extensions;

        @XmlElement(name = "NetworkDevices")
        private List<NetworkDevices> networkDevices;

        @XmlElement(name = "Link")
        private Link link;

        @XmlElement(name = "TreeBranch")
        private List<TreeBranch> treeBranches;

        Content() { }

        @NotNull List<Extension> getExtensions() {
            final List<Extension> result = new ArrayList<>();
            if (extensions != null) {
                result.addAll(extensions);
            }
            if (link != null && link.extensions != null) {
                result.addAll(link.extensions);
            }
            if (treeBranches != null) {
                result.add(new TreeExtension(treeBranches));
            }
            return result;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    private static class Link {
        @XmlElement(name = "Extension") @XmlJavaTypeAdapter(ExtensionAdapter.class)
        private List<Extension> extensions;
    }
}
