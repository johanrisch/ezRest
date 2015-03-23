package evertsson.risch.ezrest;


import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import evertsson.risch.ezDispatch.ezDispatch;
import evertsson.risch.ezrest.Entities.RestEntity;
import evertsson.risch.ezrest.Responses.RestResponse;

/**
 * This class is used to make HTTP requests. The requests can be executed with either a blocking way or in an {@link AsyncTask}.
 *
 * @author Simon Evertsson
 */
public class RestMethod {
    /**
     * The requested URL, without parameters.
     */
    public String mUrl;
    /**
     * The chosen request method.
     */
    public RestMethod.Method mMethod;
    /**
     * The list of request parameters.
     */
    public ArrayList<String[]> mQueryParameters;
    /**
     * The response status code of the executed reqeust.
     */
    public int mResponseCode;
    /**
     * The response status message of the executed reqeust.
     */
    public String mMessage;
    /**
     * The list of header name-value fields.
     */
    public ArrayList<String[]> mQueryHeaderFields;
    /**
     * The callback method which is called when the result of the request has been acquired.
     */
    public ResultReceiver mResultReceiver;
    /**
     * The raw response.
     */
    public HttpResponse mHttpResponse;

    /**
     * The final encoded URL, with parameters.
     */
    private String mFinalUrl;
    /**
     * The request which will be executed.
     */
    private HttpUriRequest mRequest;
    /**
     * The chosen response type.
     */
    public RestResponse mResponseEntity;

    /**
     * The http entity for the request
     */
    private RestEntity mEntity;

    /**
     * Constructs a new RestMethod using the supplied parameters.
     *
     * @param url          The URL which the reqeust will be sent.
     * @param method       The request method.
     * @param body         A {@link JSONObject} which will be set as the request entity. Set this to null if the reqesut should not contain an entity.
     * @param responseType The format in which the response will be converted to.
     * @param receiver     The request listener.
     */
    public RestMethod(String url, Method method, RestEntity body, RestResponse responseType,
                      ResultReceiver receiver) {
        //mUrl = ROOT_URL + url.replaceAll("\\.", "%2e");
        mUrl = url;
        if (body != null) {
            mEntity = body;
        }
        mMethod = method;
        mResponseEntity = responseType;
        mResultReceiver = receiver;
        switch (method) {
            case GET:
                mRequest = new HttpGet();
                break;
            case POST:
                mRequest = new HttpPost();
                break;
            case PUT:
                mRequest = new HttpPut();
                break;
            case DELETE:
                mRequest = new HttpDelete();
                break;
        }
    }

    /**
     * Constructs a new RestMethod using the supplied parameters.
     *
     * @param url          The URL which the reqeust will be sent.
     * @param method       The request method.
     * @param responseType The format in which the response will be converted to.
     * @param receiver     The request listener.
     */
    public RestMethod(String url, Method method, RestResponse responseType,
                      ResultReceiver receiver) {
        mUrl = url;
        mMethod = method;
        mResponseEntity = responseType;
        mResultReceiver = receiver;
        switch (method) {
            case GET:
                mRequest = new HttpGet();
                break;
            case POST:
                mRequest = new HttpPost();
                break;
            case PUT:
                mRequest = new HttpPut();
                break;
            case DELETE:
                mRequest = new HttpDelete();
                break;
        }
    }
    /**
     * Sets URL.
     *
     * @param url the url
     */
    public void setUrl(String url) {
        if (!url.startsWith("/")) {
            this.mUrl = "/" + url;
        } else {
            this.mUrl = url;
        }
    }

    /**
     * Adds a name-value pair to the list of query parameters.
     *
     * @param name  The name of the query parameter
     * @param value The value of the query parameter
     */
    public void appendQueryParameter(String name, String value) {
        if (mQueryParameters == null) {
            mQueryParameters = new ArrayList<>();
        }
        String[] keyValuePair = new String[]{name, value};
        mQueryParameters.add(keyValuePair);
    }

    /**
     * Takes a {@link HashMap} containing name-value pairs and adds them to the list of query parameters.
     *
     * @param queryParams The {@link HashMap} containing query parameters as name-value pairs.
     */
    public void setQueryParameter(HashMap<String, String> queryParams) {
        for (Entry<String, String> entry : queryParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            appendQueryParameter(key, value);
        }
    }

    /**
     * Takes a {@link HashMap} containing name-value pairs and adds them to the list of header fields.
     *
     * @param headerFields the Hashmap of header fields to be used in the request.
     */
    public void setHeaderFields(HashMap<String, String> headerFields) {
        for (Entry<String, String> entry : headerFields.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            appendHeaderField(key, value);
        }
    }

    /**
     * Adds a name-value pair to the list of header fields.
     *
     * @param name  The name of the header field
     * @param value The value of the header field
     */
    public void appendHeaderField(String name, String value) {
        if (mQueryHeaderFields == null) {
            mQueryHeaderFields = new ArrayList<>();
        }
        String[] keyValuePair = new String[]{name, value};
        mQueryHeaderFields.add(keyValuePair);
    }

    /**
     * Executes the request. This method is blocking. For non-blocking request execution, use the {@link #executeAsync()} method.
     * Calls the {@link ResultReceiver} when the execution is finished.
     */
    public void execute() {
        createRequest();
        int error = RestFinals.NO_ERRORS;
        try {
            executeRequest(mRequest);
        } catch (ClientProtocolException e) {
            error = RestFinals.CLIENT_PROTOCOL_ERROR;
        } catch (IOException e) {
            error = RestFinals.IO_ERROR;
        } catch (JSONException e) {
            error = RestFinals.JSON_ERROR;
        }
        if (error != RestFinals.NO_ERRORS) {
            mResultReceiver.onError(this, error);
        } else {
            mResultReceiver.onResult(this);
        }
    }

    /**
     * Executes the request in a non-blocking way. To execute the request in a blocking way, use the {@link #execute()} method.
     * Calls the {@link ResultReceiver} on the UI thread when the execution is finished.
     */
    public void executeAsync() {
        createRequest();
        ezDispatch dispatch = ezDispatch.getInstance();
        dispatch.executeOn(ezDispatch.NORMAL, requestBlock);

    }
    /**
     * Creates the final url using the supplied query parameters.
     */
    private void createFinalUrl() {
        mFinalUrl = mUrl;
        if (mMethod == Method.GET) {
            if (mQueryParameters != null) {
                for (int i = 0; i < mQueryParameters.size(); i++) {
                    if (i == 0) {
                        mFinalUrl += "?";
                    } else {
                        mFinalUrl += "&";
                    }

                    String paramName = mQueryParameters.get(i)[0];
                    String paramValue = mQueryParameters.get(i)[1];

                    mFinalUrl += paramName + "=" + paramValue;
                }
            }
        }
    }

    /**
     * Adds the supplied header fields to the request.
     *
     * @param request The request to which the header fields should be added.
     */
    private void addHeaders(HttpUriRequest request) {
        if (mQueryHeaderFields != null) {
            for (int i = 0; i < mQueryHeaderFields.size(); i++) {
                String fieldName = mQueryHeaderFields.get(i)[0];
                String fieldValue = mQueryHeaderFields.get(i)[1];
                request.addHeader(fieldName, fieldValue);
            }

        }
    }



    /**
     * Adds the supplied {@link evertsson.risch.ezrest.Entities.RestEntity} as entity to the request
     *
     * @param request The request to which the supplied {@link evertsson.risch.ezrest.Entities.RestEntity} should be added.
     */
    private void addEntity(HttpPost request) {
        if (mEntity != null) {
            try {
                request.setEntity(new StringEntity(mEntity.getBody()));
                request.setHeader("Accept", mResponseEntity.getAccept());
                request.setHeader("Content-type", mEntity.getContentType());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Finalizes the request and prepares it for execution.
     */
    private void createRequest() {
        createFinalUrl();
        addHeaders(mRequest);
        try {
            switch (mMethod) {
                case GET:
                    ((HttpGet) mRequest).setURI(new URI(mFinalUrl));
                    break;
                case POST:
                    ((HttpPost) mRequest).setURI(new URI(mFinalUrl));
                    if (mEntity != null) {
                        addEntity((HttpPost)mRequest);
                    }
                    break;
                case PUT:
                /* TBI */
                    break;
                case DELETE:
                /* TBI */
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Creates a new {@link HttpClient} which will be used to execute the request.
     */

 /*   private HttpClient createHttpClient() {
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params,
                HTTP.DEFAULT_CONTENT_CHARSET);
        HttpProtocolParams.setUseExpectContinue(params, true);

        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory
                .getSocketFactory(), 80));
        schReg.register(new Scheme("https",
                SSLSocketFactory.getSocketFactory(), 443));
        ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
                params, schReg);

        return new DefaultHttpClient(conMgr, params);
    }*/

    /**
     * Executes the supplied requests and parses it to the correct {@link RestResponse}
     *
     * @param request The request that will be executed.
     * @throws ClientProtocolException
     * @throws IOException
     * @throws JSONException
     */
    private void executeRequest(HttpUriRequest request)
            throws IOException, JSONException {
        RestClient.init();
        DefaultHttpClient client = RestClient.getHttpClient();

        try {
            mHttpResponse = client.execute(request);
            mResponseCode = mHttpResponse.getStatusLine().getStatusCode();
            mMessage = mHttpResponse.getStatusLine().getReasonPhrase();
            HttpEntity mResponseEntity = mHttpResponse.getEntity();

            if (mResponseEntity != null) {
                InputStream inStream = new BufferedInputStream(
                        mResponseEntity.getContent());
                this.mResponseEntity.convertStream(inStream);
                inStream.close();
            }
        } catch (ClientProtocolException e) {
            client.getConnectionManager().shutdown();
            throw e;
        }
    }
    /**
     * The different request methods
     *
     * @author simon
     */
    public enum Method {
        GET, PUT, POST, DELETE
    }
    /**
     * The {@link evertsson.risch.ezDispatch.ezBlock} that performs the execution
     * in a separate thread. When the request is finished the {@link ResultReceiver} is called.
     */
    private Callable<Integer> requestBlock = new Callable<Integer>() {
        @Override
        public Integer call() {
            int error = RestFinals.NO_ERRORS;
            try {
                executeRequest(mRequest);
            } catch (ClientProtocolException e) {
                error = RestFinals.CLIENT_PROTOCOL_ERROR;
            } catch (IOException e) {
                error = RestFinals.IO_ERROR;
            } catch (JSONException e) {
                error = RestFinals.JSON_ERROR;
            }
            final int finalError = error;
            ezDispatch.getInstance().executeOn(ezDispatch.MAIN, new Callable<Integer>() {
                @Override
                public Integer call() {
                    if (finalError != RestFinals.NO_ERRORS) {
                        mResultReceiver.onError(RestMethod.this, finalError);
                    } else {
                        mResultReceiver.onResult(RestMethod.this);
                    }
                    return finalError;
                }
            });
            return finalError;
        }
    };
}