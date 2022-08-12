package cz.smarteon.loxone;

import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Represents loxone endpoint.
 */
public final class LoxoneEndpoint {

    private final String address;
    private final Integer port;
    private final boolean useSsl;
    private boolean hasPath;
    private String host;
    private String path;
    private static final String SLASH = "/";

    private static final String WS_TEMPLATE = "%s://%s:%d/ws/rfc6455";
    private static final String WS_TEMPLATE_PATH = "%s://%s/ws/rfc6455";

    /**
     * Create new instance of given address (only the address without protocol or port part is expected) and default port
     * @param address loxone address
     */
    public LoxoneEndpoint(@NotNull final String address) {
        if(address.contains("://")) throw new IllegalArgumentException("Address cannot contain protocol");

        int slashIndex = address.indexOf(SLASH);
        if(slashIndex != -1){
            this.host = address.substring(0, slashIndex);
            this.path = address.substring(slashIndex);
            this.hasPath = true;
            this.useSsl = true;
            this.port = null;
        } else {
            this.hasPath = false;
            this.port = 80;
            this.useSsl = false;
        }
        this.address = address;
    }

    /**
     * Create new instance of given address (only the address without protocol or port part is expected) and port
     * @param address loxone address
     * @param port loxone port
     */
    public LoxoneEndpoint(@NotNull final String address, final int port) {
        this(address, port, false);
    }

    /**
     * Create new instance of given address (only the address without protocol or port part is expected), port
     * and sets whether to use SSL.
     * BEWARE: Loxone miniserver (in version 10) doesn't support SSL natively. So the {@code useSsl} make sense only
     * when accessing miniserver through some reverse HTTPS proxy.
     * @param address loxone address
     * @param port loxone port
     * @param useSsl whether to use SSL
     */
    public LoxoneEndpoint(@NotNull final String address, final int port, final boolean useSsl) {
        this.address = address;
        this.port = port;
        this.useSsl = useSsl;
    }

    /**
     * Creates websocket URI of this endpoint.
     * @return websocket URI
     */
    @NotNull
    URI webSocketUri() {
        if(hasPath){
            return URI.create(String.format(WS_TEMPLATE_PATH, useSsl ? "wss" : "ws", address));
        } else {
            return URI.create(String.format(WS_TEMPLATE, useSsl ? "wss" : "ws", address, port));
        }
    }

    /**
     * Creates HTTP URL of this endpoint and given path.
     * @param path path
     * @return http url
     * @throws MalformedURLException in case the given path produces invalid URL
     */
    @NotNull
    URL httpUrl(@NotNull final String path) throws MalformedURLException {
        final String pathPart = requireNonNull(path, "path can't be null");
        if(hasPath){
            return new URL("https", host, this.path);
        } else {
            return new URL(useSsl ? "https" : "http", address, port, pathPart.startsWith("/") ? pathPart : "/" + pathPart);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoxoneEndpoint that = (LoxoneEndpoint) o;
        return Objects.equals(port, that.port) && useSsl == that.useSsl && Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        if(hasPath){
            return Objects.hash(address, useSsl);
        } else {
            return Objects.hash(address, port, useSsl);
        }
    }

    @Override
    public String toString() {
        if(hasPath){
            return address + " " + "(secured)";
        } else {
            return address + ":" + port + " " + (useSsl ? "(secured)" : "(unsecured)");
        }
    }
}
