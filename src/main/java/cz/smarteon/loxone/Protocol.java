package cz.smarteon.loxone;

import static java.lang.String.format;

public abstract class Protocol {

    public static final int HTTP_OK = 200;
    public static final int HTTP_AUTH_FAIL = 401;
    public static final int HTTP_NOT_AUTHENTICATED = 400;
    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_AUTH_TOO_LONG = 420;
    public static final int HTTP_UNAUTHORIZED = 500;

    public static final String C_SYS_ENC = "dev/sys/enc";
    public static final String C_APP = "data/LoxAPP3.json";
    public static final String C_JSON_APP_VERSION = "jdev/sps/LoxAPPversion3";



}
