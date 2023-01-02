package cz.smarteon.loxone;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * HTTP protocol implementation to communicate with Loxone miniserver.
 */
public class LoxoneHttp {

    private static final Logger LOG = LoggerFactory.getLogger(LoxoneHttp.class);
    private static final int MAX_REDIRECTS = 5;

    // temporarily relaxed visibility to allow deprecated LoxoneAuth constructor
    @SuppressWarnings("checkstyle:VisibilityModifier")
    final LoxoneEndpoint endpoint;
    private int connectionTimeout = 5000;

    private final ThreadLocal<RequestContext> requestContext = new ThreadLocal<>();

    public LoxoneHttp(@NotNull final LoxoneEndpoint endpoint) {
        this.endpoint = requireNonNull(endpoint, "endpoint can't be null");
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * Get the last URL called within this thread.
     * @return last called URL within this thread.
     */
    public URL getLastUrl() {
        return requestContext.get().lastUrl;
    }

    public <T> T get(Command<T> command) {
        return get(command, Collections.emptyMap());
    }

    public <T> T get(Command<T> command, LoxoneAuth loxoneAuth) {
        return get(
                command,
                loxoneAuth != null ? loxoneAuth.authHeaders() : Collections.emptyMap());
    }

    <T> T get(Command<T> command, Map<String, String> properties) {
        final URL url = urlFromCommand(command.getCommand());
        requestContext.set(new RequestContext(url));
        return get(url, command.getType(), properties, command.getResponseType());
    }

    @SuppressWarnings({"checkstyle:CyclomaticComplexity", "checkstyle:ReturnCount"})
    <T> T get(URL url, Command.Type type, Map<String, String> properties, Class<T> responseType) {
        LOG.debug("Trigger command url=" + url);
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(connectionTimeout);
            for (Map.Entry<String, String> property : properties.entrySet()) {
                connection.setRequestProperty(property.getKey(), property.getValue());
            }
            final int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                requestContext.get().lastUrl = connection.getURL();
                try (InputStream is = connection.getInputStream()) {
                    switch (type) {
                        case JSON:
                            return Codec.readMessage(is, responseType);
                        case XML:
                            return Codec.readXml(is, responseType);
                        default:
                            throw new IllegalStateException("Unknown command type " + type);
                    }
                }
            } else if (responseCode == HttpURLConnection.HTTP_MOVED_PERM
                    || responseCode == HttpURLConnection.HTTP_MOVED_TEMP
                    || responseCode == HttpURLConnection.HTTP_SEE_OTHER 
                    || responseCode == 307) {
                if (requestContext.get().redirects > MAX_REDIRECTS) {
                    throw new IllegalStateException("Too many redirects!");
                }
                final URL location = new URL(connection.getHeaderField("Location"));
                requestContext.get().redirect(location);
                return get(location, type, properties, responseType);
            } else {
                throw new LoxoneException("Loxone command responded by status " + responseCode);
            }
        } catch (IOException e) {
            LOG.error("Can't trigger command on url=" + url, e);
            throw new LoxoneException("Error while triggering loxone command", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private URL urlFromCommand(String command) {
        try {
            return endpoint.httpUrl(command);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Command " + command + " produces malformed URL");
        }
    }

    private static class RequestContext {
        private int redirects;
        private URL lastUrl;

        RequestContext(final URL lastUrl) {
            this.lastUrl = lastUrl;
        }

        public void redirect(final URL location) {
            redirects++;
            this.lastUrl = location;
        }
    }
}
