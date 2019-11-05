package cz.smarteon.loxone;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static java.lang.String.format;

public abstract class Protocol {

    public static final int HTTP_OK = 200;
    public static final int HTTP_AUTH_FAIL = 401;
    public static final int HTTP_NOT_AUTHENTICATED = 400;
    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_AUTH_TOO_LONG = 420;
    public static final int HTTP_UNAUTHORIZED = 500;

    public static final String C_JSON_GET_KEY = "jdev/sys/getkey2";
    public static final String C_XML_GET_KEY = "dev/sys/getkey";
    public static final String C_SYS_ENC = "dev/sys/enc";
    public static final String C_JSON = "jdev/sps/io";
    public static final String C_XML = "dev/sps/io";
    public static final String CS_JSON = "jdev/sps/ios";
    public static final String C_JSON_INIT_STATUS = "jdev/sps/enablebinstatusupdate";
    public static final String C_AUTHENTICATE = "authenticate";
    public static final String C_JSON_GET_TOKEN = "jdev/sys/gettoken";
    public static final String C_JSON_GET_VISU_SALT = "jdev/sys/getvisusalt";
    public static final String C_GET_VISU_SALT = "dev/sys/getvisusalt";
    public static final String C_JSON_ENC = "jdev/sys/enc/";
    public static final String C_APP = "data/LoxAPP3.json";
    public static final String C_JSON_APP_VERSION = "jdev/sps/LoxAPPversion3";
    public static final String C_APP_VERSION = "dev/sps/LoxAPPversion3";

    private static final String JSON_ALL_URI_TEMPLATE = C_JSON + "/%s/all";
    private static final String TEMPLATE_DEVICE_OFF = "/%s/off";
    private static final String TEMPLATE_DEVICE_ON = "/%s/on";
    private static final String TEMPLATE_XML = C_XML + "/%s";
    private static final String TEMPLATE_AUTHENTICATE = C_AUTHENTICATE + "/%s";

    public static String controlAll(String controlId) {
        return controlCmd(controlId, "all");
    }

    public static String jsonControlAll(String controlId) {
        return C_JSON + "/" + controlAll(controlId);
    }

    public static String controlOff(String controlId) {
        return controlCmd(controlId, "off");
    }

    public static String jsonControlOff(String controlId) {
        return C_JSON + "/" + controlOff(controlId);
    }

    public static String controlOn(String controlId) {
        return controlCmd(controlId, "on");
    }

    public static String jsonControlOn(String controlId) {
        return C_JSON + "/" + controlOn(controlId);
    }

    public static String controlCmd(final String controlId, final String cmd) {
        return controlId + "/" + cmd;
    }

    public static String jsonControlCmd(final String controlId, final String cmd) {
        return C_JSON + "/" + controlCmd(controlId, cmd);
    }

    public static String jsonControl(final String controlId) {
        return C_JSON + "/" + controlId;
    }

    public static boolean isCommandControlOn(String controlId, String command) {
        return command.matches(".*" + C_XML + ".*/" + controlId + "/on");
    }

    public static boolean isCommandControlOff(String controlId, String command) {
        return command.matches(".*" + C_XML + ".*/" + controlId + "/off");
    }


    public static boolean isCommandControlAll(String controlId, String command) {
        return command.matches(".*" + C_XML + ".*/" + controlId + "/all");
    }

    public static String xmlDevice(String deviceId) {
        return format(TEMPLATE_XML, deviceId);
    }

    public static String authentication(String hmac) {
        return format(TEMPLATE_AUTHENTICATE, hmac);
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
