package evertsson.risch.ezrest.Responses;

import java.io.InputStream;

/**
 * Created by johanrisch on 17/02/15.
 */
public interface RestResponse {
    public String getAccept();
    public void convertStream(InputStream instream);
    public String getBody();
}
