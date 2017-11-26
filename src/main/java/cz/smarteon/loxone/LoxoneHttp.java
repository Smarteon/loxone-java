package cz.smarteon.loxone;

import cz.smarteon.loxone.message.LoxoneMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

public class LoxoneHttp {

    private static final Logger log = LoggerFactory.getLogger(LoxoneHttp.class);

    private final Protocol protocol;
    private final LoxoneAuth loxoneAuth;

    public LoxoneHttp(Protocol protocol, LoxoneAuth loxoneAuth) {
        this.protocol = protocol;
        this.loxoneAuth = loxoneAuth;
    }

    public LoxoneMessage get(String command) {
        final String sanitizedUri = command.startsWith("/") ? command : "/" + command;
        log.debug("Get for JSON uri=" + sanitizedUri);

        return get(protocol.urlFromCommand(command), loxoneAuth.authHeaders());
    }

    static LoxoneMessage get(URL url) {
        return get(url, Collections.<String, String>emptyMap());
    }

    static LoxoneMessage get(URL url, Map<String, String> properties) {
        log.debug("Get for JSON url=" + url);
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            for (Map.Entry<String, String> property : properties.entrySet()) {
                connection.setRequestProperty(property.getKey(), property.getValue());
            }
            final int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (InputStream is = connection.getInputStream()) {
                    return Codec.readMessage(is);
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
}
