package cz.smarteon.loxone.system.status;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import cz.smarteon.loxone.config.MiniserverType;

import java.util.List;
import java.util.stream.Collectors;

@JacksonXmlRootElement(localName = "Status")
public class MiniserverStatus {

    private final String modified; // TODO migrate to datetime
    private final Content content;

    @JsonCreator
    MiniserverStatus(@JsonProperty("Modified") final String modified,
                     @JsonProperty("Miniserver") final Content content) {
        this.modified = modified;
        this.content = content;
    }

    public String getModified() {
        return modified;
    }

    public MiniserverType getType() {
        return MiniserverType.fromValue(content.type);
    }

    public String getName() {
        return content.name;
    }

    public String getIp() {
        return content.ip;
    }

    public String getMask() {
        return content.mask;
    }

    public String getGateway() {
        return content.gateway;
    }

    public boolean usesDhcp() {
        return Boolean.TRUE.equals(content.dhcp);
    }

    public String getDns1() {
        return content.dns1;
    }

    public String getDns2() {
        return content.dns2;
    }

    public String getMac() {
        return content.mac;
    }

    public String getDevice() {
        return content.device;
    }

    public String getVersion() {
        return content.version;
    }

    public String getLanErrorsPercent() {
        return content.lanErrorsPercent;
    }

    public Integer getLinkErrorsCount() {
        return content.linkErrorsCount;
    }

    public List<Extension> getExtensions() {
        return content.extensions;
    }

    public <T extends Extension> List<T> getExtensions(Class<T> extensionType) {
        return getExtensions().stream().filter(e -> extensionType.isAssignableFrom(e.getClass()))
                .map(extensionType::cast).collect(Collectors.toList());
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
        private final String lanErrorsPercent; // TODO float
        private final Integer linkErrorsCount;
        private final List<Extension> extensions;

        @JsonCreator
        Content(@JsonProperty("Type") final Integer type, @JsonProperty("Name") final String name,
                @JsonProperty("IP") final String ip, @JsonProperty("Mask") final String mask,
                @JsonProperty("Gateway") final String gateway, @JsonProperty("DHCP") final Boolean dhcp,
                @JsonProperty("DNS1") final String dns1, @JsonProperty("DNS2") final String dns2,
                @JsonProperty("MAC") final String mac, @JsonProperty("Device") final String device,
                @JsonProperty("Version") final String version, @JsonProperty("LANErrors") final String lanErrorsPercent,
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
