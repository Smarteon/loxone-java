package cz.smarteon.loxone;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import static java.lang.String.format;

public class Protocol {

    public static final int HTTP_OK = 200;
    public static final int HTTP_AUTH_FAIL = 401;
    public static final int HTTP_NOT_AUTHENTICATED = 400;
    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_AUTH_TOO_LONG = 420;
    public static final int HTTP_UNAUTHORIZED = 500;

    public static final String JSON_ROOT_ID = "LL";
    public static final String JSON_COMMAND_ID = "control";
    public static final String JSON_VALUE_ID = "value";
    public static final String JSON_CODE_ID = "Code";

    public static final String C_JSON_API = "jdev/cfg/api";
    public static final String C_JSON_PUBLIC_KEY = "jdev/sys/getPublicKey";
    public static final String C_JSON_GET_KEY = "jdev/sys/getkey2";
    public static final String C_XML_GET_KEY = "dev/sys/getkey";
    public static final String C_SYS_ENC = "dev/sys/enc";
    public static final String C_JSON = "jdev/sps/io";
    public static final String C_XML = "dev/sps/io";
    public static final String CS_JSON = "jdev/sps/ios";
    public static final String C_JSON_INIT_STATUS = "jdev/sps/enablebinstatusupdate";
    public static final String C_AUTHENTICATE = "authenticate";
    public static final String C_JSON_KEY_EXCHANGE = "jdev/sys/keyexchange";
    public static final String C_JSON_GET_TOKEN = "jdev/sys/gettoken";
    public static final String C_JSON_GET_VISU_SALT = "jdev/sys/getvisusalt";
    public static final String C_GET_VISU_SALT = "dev/sys/getvisusalt";
    public static final String C_JSON_ENC = "jdev/sys/enc/";

    private static final String JSON_ALL_URI_TEMPLATE = C_JSON + "/%s/all";
    private static final String TEMPLATE_DEVICE_OFF = "/%s/off";
    private static final String TEMPLATE_DEVICE_ON = "/%s/on";
    private static final String TEMPLATE_XML = C_XML + "/%s";
    private static final String TEMPLATE_AUTHENTICATE = C_AUTHENTICATE + "/%s";

    private final String loxoneAddress;
    private final int port;

    public Protocol(String loxoneAddress, int port) {
        this.loxoneAddress = loxoneAddress;
        this.port = port;
    }

    public Protocol(String loxoneAddress) {
        this(loxoneAddress, 80);
    }

    public URL urlFromCommand(String command) {
        try {
            return new URL("http", loxoneAddress, port, command.startsWith("/") ? command : "/" + command);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Command " + command + " produces malformed URL");
        }
    }

    public URL api() {
        return urlFromCommand(C_JSON_API);
    }


    public URL publicKey() {
        return urlFromCommand(C_JSON_PUBLIC_KEY);
    }

    public static String jsonAlarmAll(String alarmDeviceId) {
        return alarmDeviceId + "/all";
    }

    public static String jsonAlarmOff(String alarmDeviceId) {
        return alarmDeviceId + "/off";
    }

    public static String jsonAlarmOn(String alarmDeviceId) {
        return alarmDeviceId + "/on";
    }

    public static boolean isCommandAlarmOn(String alarmDeviceId, String command) {
        return command.matches(".*" + C_XML + ".*/" + alarmDeviceId + "/on");
    }

    public static boolean isCommandAlarmOff(String alarmDeviceId, String command) {
        return command.matches(".*" + C_XML + ".*/" + alarmDeviceId + "/off");
    }


    public static boolean isCommandAlarmAll(String alarmDeviceId, String command) {
        return command.matches(".*" + C_XML + ".*/" + alarmDeviceId + "/all");
    }

    public static String xmlDevice(String deviceId) {
        return format(TEMPLATE_XML, deviceId);
    }

    public static String authentication(String hmac) {
        return format(TEMPLATE_AUTHENTICATE, hmac);
    }

    public static String jsonKeyExchange(String sessionKey) {
        return C_JSON_KEY_EXCHANGE + "/" + sessionKey;
    }

    public static String jsonGetKey(String user) {
        return C_JSON_GET_KEY + "/" + user;
    }


    public static String jsonGetToken(String tokenHash, String user, String clientUuid, String clientInfo) {
        return C_JSON_GET_TOKEN + "/" + tokenHash + "/" + user + "/2/" + clientUuid + "/" + clientInfo;
    }

    public static boolean isCommandGetToken(String command, String user) {
        return command.matches(C_JSON_GET_TOKEN + ".*/" + user + "/.*");
    }

    public static String jsonGetVisuSalt(String user) {
        return C_JSON_GET_VISU_SALT + "/" + user ;
    }

    public static boolean isCommandGetVisuSalt(String command, String user) {
        return command.matches(C_GET_VISU_SALT + "/" + user);
    }

    public static String jsonEncrypted(String encryptedCommand) {
        return C_JSON_ENC + encodeUrl(encryptedCommand);
    }

    public static String jsonSecured(String command, String visuHash) {
        return CS_JSON + "/" + visuHash + "/" + command;
    }

    private static String encodeUrl(String toEncode) {
        try {
            return URLEncoder.encode(toEncode, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 encoding should be present everywhere", e);
        }
    }
}
