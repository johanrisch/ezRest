package evertsson.risch.ezrest.Responses;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by johanrisch on 17/02/15.
 */
public class JsonResponse implements RestResponse {
    private String mRawBody;

    /**
     * Converts {@link android.content.res.Resources.Theme} supplied {@link java.io.InputStream} to a {@link org.json.JSONObject}.
     *
     * @param instream
     * @throws java.io.IOException
     * @throws org.json.JSONException
     */
    @Override
    public void convertStream(InputStream instream) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    instream));
            StringBuilder builder = new StringBuilder();
            JSONObject result = null;

            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
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

    @Override
    public String getAccept() {
        return "application/json";
    }

}
