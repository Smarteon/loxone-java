package cz.smarteon.loxone;

import cz.smarteon.loxone.message.LoxoneMessage;
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

    private final String loxoneAddress;
    private final int port;
    private int connectionTimeout = 5000;

    public LoxoneHttp(String loxoneAddress) {
        this(loxoneAddress, 80);
    }

    public LoxoneHttp(String loxoneAddress, int port) {
        this.loxoneAddress = requireNonNull(loxoneAddress);
        this.port = port;
    }

    public LoxoneMessage get(String command) {
        return get(command, null, LoxoneMessage.class);
    }

    public LoxoneMessage get(String command, LoxoneAuth loxoneAuth) {
        return get(command, loxoneAuth, LoxoneMessage.class);
    }

    public <T> T get(String command, Class<T> clazz) {
        return get(command, null, clazz);
    }

    public <T> T get(String command, LoxoneAuth loxoneAuth, Class<T> clazz) {
        final String sanitizedUri = command.startsWith("/") ? command : "/" + command;
        log.debug("Get for JSON uri=" + sanitizedUri);

        return get(
                urlFromCommand(command),
                loxoneAuth != null ? loxoneAuth.authHeaders() : Collections.<String, String>emptyMap(),
                clazz);
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    LoxoneMessage get(URL url) {
        return get(url, Collections.<String, String>emptyMap(), LoxoneMessage.class);
    }

    <T> T get(URL url, Class<T> clazz) {
        return get(url, Collections.<String, String>emptyMap(), clazz);
    }

    LoxoneMessage get(URL url, Map<String, String> properties) {
        return get(url, properties, LoxoneMessage.class);
    }

    <T> T get(URL url, Map<String, String> properties, Class<T> clazz) {
        log.debug("Get for JSON url=" + url);
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(connectionTimeout);
            for (Map.Entry<String, String> property : properties.entrySet()) {
                connection.setRequestProperty(property.getKey(), property.getValue());
            }
            final int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (InputStream is = connection.getInputStream()) {
                    return Codec.readMessage(is, clazz);
                }
            } else {
                throw new LoxoneException("Get for loxone json responded by status " + responseCode);
            }
        } catch (IOException e) {
            log.error("Can't get for JSON on url=" + url, e);
            throw new LoxoneException("Error while requesting loxone json", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private URL urlFromCommand(String command) {
        try {
            return new URL("http", loxoneAddress, port, command.startsWith("/") ? command : "/" + command);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Command " + command + " produces malformed URL");
        }
    }


}
