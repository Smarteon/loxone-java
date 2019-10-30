package cz.smarteon.loxone.system.status;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import cz.smarteon.loxone.PercentDoubleDeserializer;
import cz.smarteon.loxone.config.MiniserverType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Represents {@link cz.smarteon.loxone.Command#DATA_STATUS} payload. Only intended for deserialization.
 */
@JacksonXmlRootElement(localName = "Status")
public class MiniserverStatus {

    private final String modified; // TODO migrate to datetime
    private final Content content;

    @JsonCreator
    MiniserverStatus(@JsonProperty("Modified") final String modified,
                     @JsonProperty("Miniserver") final Content content) {
        this.modified = modified;
        this.content = requireNonNull(content, "content can't be null");
    }

    @Nullable
    public String getModified() {
        return modified;
    }

    /**
     * Miniserver type
     * @return type of miniserver
     */
    @NotNull
    public MiniserverType getType() {
        return MiniserverType.fromValue(content.type);
    }

    /**
     * Miniserver name
     * @return name
     */
    @Nullable
    public String getName() {
        return content.name;
    }

    /**
     * Miniserver IP address
     * @return IP address
     */
    @Nullable
    public String getIp() {
        return content.ip;
    }

    /**
     * Miniserver network address mask
     * @return network mask
     */
    @Nullable
    public String getMask() {
        return content.mask;
    }

    /**
     * Network gateway miniserver uses
     * @return gateway
     */
    @Nullable
    public String getGateway() {
        return content.gateway;
    }

    /**
     * Whether the network was set by DHCP
     * @return true if DHCP used, false otherwise
     */
    public boolean usesDhcp() {
        return Boolean.TRUE.equals(content.dhcp);
    }

    /**
     * Assigned DNS server 1
     * @return DNS server address
     */
    @Nullable
    public String getDns1() {
        return content.dns1;
    }

    /**
     * Assigned DNS server 2
     * @return DNS server address
     */
    @Nullable
    public String getDns2() {
        return content.dns2;
    }

    /**
     * Miniserver's MAC address
     * @return mac address
     */
    @Nullable
    public String getMac() {
        return content.mac;
    }

    /**
     * Miniserver device label
     * @return device
     */
    @Nullable
    public String getDevice() {
        return content.device;
    }

    /**
     * Miniserver version
     * @return version
     */
    @Nullable
    public String getVersion() {
        return content.version;
    }

    /**
     * Percentage of LAN errors.
     * @return LAN errors
     */
    @Nullable
    public Double getLanErrorsPercent() {
        return content.lanErrorsPercent;
    }

    /**
     * Count of loxone link errors
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
        return content.extensions != null ? content.extensions : Collections.emptyList();
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


    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Content {

        private final Integer type;
        private final String name;
        private final String ip;
        private final String mask;
        private final String gateway;
        private final Boolean dhcp;
        private final String dns1;
        private final String dns2;
        private final String mac;
        private final String device;
        private final String version;
        private final Double lanErrorsPercent;
        private final Integer linkErrorsCount;
        private final List<Extension> extensions;

        @JsonCreator
        Content(@JsonProperty("Type") final Integer type, @JsonProperty("Name") final String name,
                @JsonProperty("IP") final String ip, @JsonProperty("Mask") final String mask,
                @JsonProperty("Gateway") final String gateway, @JsonProperty("DHCP") final Boolean dhcp,
                @JsonProperty("DNS1") final String dns1, @JsonProperty("DNS2") final String dns2,
                @JsonProperty("MAC") final String mac, @JsonProperty("Device") final String device,
                @JsonProperty("Version") final String version,
                @JsonProperty("LANErrors") @JsonDeserialize(using = PercentDoubleDeserializer.class) final Double lanErrorsPercent,
                @JsonProperty("LinkErrors") final Integer linkErrorsCount,
                @JsonProperty("Extension") final List<Extension> extensions) {
            this.type = type;
            this.name = name;
            this.ip = ip;
            this.mask = mask;
            this.gateway = gateway;
            this.dhcp = dhcp;
            this.dns1 = dns1;
            this.dns2 = dns2;
            this.mac = mac;
            this.device = device;
            this.version = version;
            this.lanErrorsPercent = lanErrorsPercent;
            this.linkErrorsCount = linkErrorsCount;
            this.extensions = extensions;
        }
    }

}
