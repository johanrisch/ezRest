package evertsson.risch.ezrest;

/**
 * Created by johanrisch on 17/02/15.
 */
public class RestFinals {
    /**
     * Error code. Indicates that no errors occurred while executing the request or parsing the result.
     */
    public static final int NO_ERRORS = 0;

    /**
     * Error code. Indicates that there is an error in the HTTP protocol.
     */
    public static final int CLIENT_PROTOCOL_ERROR = -1;

    /**
     * Error code. Indicates that the {@link java.io.InputStream} in the response could not be read correctly.
     */
    public static final int IO_ERROR = -2;

    /**
     * Error code. Indicates that the response string could not be parsed to a valid {@link org.json.JSONObject}.
     */
    public static final int JSON_ERROR = -3;
    public static final int RESPONSE_OK = 200;
    public static final int RESPONSE_NOT_FOUND = 404;
    public static final int RESPONSE_INTERNAL_SERVER_ERROR = 500;
    public static final int RESPONSE_NOT_AUTHORIZED = 403;
}
