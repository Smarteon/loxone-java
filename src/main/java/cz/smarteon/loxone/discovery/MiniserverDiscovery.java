package cz.smarteon.loxone.discovery;

import cz.smarteon.loxone.LoxoneEndpoint;
import cz.smarteon.loxone.app.MiniserverType;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents the discovered miniserver instance.
 */
public final class MiniserverDiscovery {

    private static final Pattern RESPONSE_PATTERN =
            Pattern.compile(
                    "^LoxLIVE: (.+) " // name
                            + "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}):(\\d+) " // address:port
                            + "([A-Z\\d]{12}) "  // MAC address
                            + "(\\d[.\\d]*) "  // firmware version
                            + "Prog:([\\d-\\s:]+) "  // last configuration
                            + "Type:(\\d).*$"); // miniserver type

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String name;
    private final String address;
    private final int port;
    private final String mac;
    private final MiniserverType type;
    private final String firmwareVersion;
    private final LocalDateTime lastConfig;

    private MiniserverDiscovery(final String name, final String address, final int port,
                                final String mac, final MiniserverType type,
                                final String firmwareVersion, final LocalDateTime lastConfig) {
        this.name = name;
        this.address = address;
        this.port = port;
        this.mac = mac;
        this.type = type;
        this.firmwareVersion = firmwareVersion;
        this.lastConfig = lastConfig;
    }

    @NotNull
    static MiniserverDiscovery fromResponse(final @NotNull String discoveryResponse) {
        final Matcher matcher = RESPONSE_PATTERN.matcher(discoveryResponse);
        if (matcher.find() && matcher.groupCount() > 6) {
            return new MiniserverDiscovery(
                    matcher.group(1),
                    matcher.group(2),
                    Integer.parseInt(matcher.group(3)),
                    matcher.group(4),
                    MiniserverType.fromValue(Integer.parseInt(matcher.group(7))),
                    matcher.group(5),
                    LocalDateTime.parse(matcher.group(6), FORMATTER)
            );
        } else {
            throw new LoxoneDiscoveryException("Can't parse miniserver discovery response");
        }
    }

    @NotNull
    public LoxoneEndpoint getLoxoneEndpoint() {
        return new LoxoneEndpoint(address, port, false);
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String getMac() {
        return mac;
    }

    public MiniserverType getType() {
        return type;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public LocalDateTime getLastConfig() {
        return lastConfig;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MiniserverDiscovery that = (MiniserverDiscovery) o;
        return port == that.port
                && Objects.equals(name, that.name)
                && Objects.equals(address, that.address)
                && Objects.equals(mac, that.mac)
                && type == that.type
                && Objects.equals(firmwareVersion, that.firmwareVersion)
                && Objects.equals(lastConfig, that.lastConfig);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, address, port, mac, type, firmwareVersion, lastConfig);
    }

    @Override
    public String toString() {
        return "MiniserverDiscovery{"
                + "name='" + name + '\''
                + ", address='" + address + '\''
                + ", port=" + port
                + ", mac='" + mac + '\''
                + ", type=" + type
                + ", firmwareVersion='" + firmwareVersion + '\''
                + ", lastConfig=" + lastConfig
                + '}';
    }
}
