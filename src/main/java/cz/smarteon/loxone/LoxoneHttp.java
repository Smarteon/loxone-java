package cz.smarteon.loxone;

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
        final URL url = urlFromCommand(command.getCommand());
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
                try (InputStream is = connection.getInputStream()) {
                    switch (command.getType()) {
                        case JSON:
                            return Codec.readMessage(is, command.getResponseType());
                        case XML:
                            return Codec.readXml(is, command.getResponseType());
                        default:
                            throw new IllegalStateException("Unknown command type " + command.getType());
                    }
                }
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
            return new URL("http", loxoneAddress, port, command.startsWith("/") ? command : "/" + command);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Command " + command + " produces malformed URL");
        }
    }


}
