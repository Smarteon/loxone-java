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

public class LoxoneHttp {

    private static final Logger log = LoggerFactory.getLogger(LoxoneHttp.class);
    private static final int MAX_REDIRECTS = 5;

    private final LoxoneEndpoint endpoint;
    private int connectionTimeout = 5000;

    private ThreadLocal<Integer> redirects;

    public LoxoneHttp(@NotNull final LoxoneEndpoint endpoint) {
        this.endpoint = requireNonNull(endpoint, "endpoint can't be null");
    }

    public <T> T get(Command<T> command) {
        return get(command, Collections.emptyMap());
    }

    public <T> T get(Command<T> command, LoxoneAuth loxoneAuth) {
        return get(
                command,
                loxoneAuth != null ? loxoneAuth.authHeaders() : Collections.<String, String>emptyMap());
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    <T> T get(Command<T> command, Map<String, String> properties) {
        return get(urlFromCommand(command.getCommand()), command.getType(), properties, command.getResponseType());
    }

    <T> T get(URL url, Command.Type type, Map<String, String> properties, Class<T> responseType) {
        log.debug("Trigger command url=" + url);
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(connectionTimeout);
            for (Map.Entry<String, String> property : properties.entrySet()) {
                connection.setRequestProperty(property.getKey(), property.getValue());
            }
            final int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                redirects = null;
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
                if (redirects == null) {
                    redirects = new ThreadLocal<>();
                    redirects.set(0);
                } else if (redirects.get() > MAX_REDIRECTS){
                    throw new IllegalStateException("Too many redirects!");
                }
                redirects.set(redirects.get() + 1);
                return get(new URL(connection.getHeaderField("Location")), type, properties, responseType);
            } else {
                throw new LoxoneException("Loxone command responded by status " + responseCode);
            }
        } catch (IOException e) {
            log.error("Can't trigger command on url=" + url, e);
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


}
