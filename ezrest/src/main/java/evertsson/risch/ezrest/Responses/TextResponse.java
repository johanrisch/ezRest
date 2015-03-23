package evertsson.risch.ezrest.Responses;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by johanrisch on 17/02/15.
 */
public class TextResponse implements RestResponse {
    private String mRawBody;

    @Override
    public String getAccept() {
        return "test/html";
    }

    /**
     * Converts the supplied {@link InputStream} to a string.
     *
     * @param inStream
     * @return
     * @throws java.io.IOException
     */
    public void convertStream(InputStream inStream) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inStream));
            StringBuilder builder = new StringBuilder();
            String result = null;

            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line + "\n");
            }
            this.mRawBody = builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBody() {
        return mRawBody;
    }
}
