package cz.smarteon.loxone;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Represents loxone endpoint.
 */
public final class LoxoneEndpoint {

    private final String host;
    private final Integer port;
    private final String path;
    private final boolean useSsl;
    private static final String SLASH = "/";

    private static final String WS_TEMPLATE = "%s://%s:%d/ws/rfc6455";
    private static final String WS_TEMPLATE_PATH = "%s://%s/ws/rfc6455";

    /**
     * Create new instance of given address (only the address without protocol or port part is expected) and default port
     * @param address loxone address
     */
    public LoxoneEndpoint(@NotNull final String address) {
        this(
                checkAndParseHost(requireNonNull(address, "address can't be null")),
                address.contains(SLASH) ? null : 80,
                address.contains(SLASH),
                address.contains(SLASH) ? address.substring(address.indexOf(SLASH)) : ""
        );
    }

    /**
     * Create new instance of given host (only the host without protocol or port part is expected) and port
     * @param host loxone address host
     * @param port loxone address port
     */
    public LoxoneEndpoint(@NotNull final String host, final int port) {
        this(host, port, false);
    }

    /**
     * Create new instance of given host (only the host without protocol or port part is expected), port
     * and sets whether to use SSL.
     * BEWARE: Loxone miniserver (in version 10) doesn't support SSL natively. So the {@code useSsl} make sense only
     * when accessing miniserver through some reverse HTTPS proxy.
     * @param host loxone address host
     * @param port loxone address port
     * @param useSsl whether to use SSL
     */
    public LoxoneEndpoint(@NotNull final String host, final int port, final boolean useSsl) {
        this(host, port, useSsl, "");
    }

    /**
     * Create new instance of given host (only the host without protocol or port part is expected), port
     * and sets whether to use SSL.
     * BEWARE: Loxone miniserver (in version 10) doesn't support SSL natively. So the {@code useSsl} make sense only
     * when accessing miniserver through some reverse HTTPS proxy.
     * @param host loxone address host
     * @param port loxone address port
     * @param useSsl whether to use SSL
     * @param path loxone address path
     */
    public LoxoneEndpoint(@NotNull final String host, @Nullable final Integer port, final boolean useSsl, @NotNull final String path) {
        this.host = requireNonNull(host, "host can't be null");
        this.port = port;
        this.useSsl = useSsl;
        this.path = sanitizePath(requireNonNull(path, "path can't be null"));
    }

    /**
     * Creates websocket URI of this endpoint.
     * @return websocket URI
     */
    @NotNull
    URI webSocketUri() {
        if(hasPath()){
            return URI.create(String.format(WS_TEMPLATE_PATH, useSsl ? "wss" : "ws", host));
        } else {
            return URI.create(String.format(WS_TEMPLATE, useSsl ? "wss" : "ws", host, port));
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
        if(hasPath()){
            return new URL("https", host, this.path + sanitizePath(pathPart));
        } else {
            return new URL(useSsl ? "https" : "http", host, port, sanitizePath(pathPart));
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoxoneEndpoint that = (LoxoneEndpoint) o;
        return useSsl == that.useSsl && host.equals(that.host) && Objects.equals(port, that.port) && path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, path, useSsl);
    }

    @Override
    public String toString() {
        if(hasPath()){
            return host + SLASH + path + " " + "(secured)";
        } else {
            return host + ":" + port + " " + (useSsl ? "(secured)" : "(unsecured)");
        }
    }

    private static String sanitizePath(final String path) {
        return path.startsWith(SLASH) || path.isEmpty() ? path : SLASH + path;
    }

    private boolean hasPath() {
        return !path.isEmpty();
    }

    private static String checkAndParseHost(final String address) {
        if(address.contains("://")) throw new IllegalArgumentException("Address cannot contain protocol");
        return address.contains(SLASH) ? address.substring(0, address.indexOf(SLASH)) : address;
    }

}
