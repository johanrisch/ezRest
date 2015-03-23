package evertsson.risch.ezrest;

import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;

public class RestClient {

    private static RestClient sInstance;
    private static DefaultHttpClient sHttpClient;
    private static BasicCookieStore sCookieStore;
    private static BasicHttpContext sLocalContext;
    private static boolean sIsInitialized = false;

    public static BasicCookieStore getCookieStore() {
        return sCookieStore;
    }

    public static BasicHttpContext getHttpContext() {
        return sLocalContext;
    }

    public static DefaultHttpClient getHttpClient() {
        return sHttpClient;
    }

    public static void init() {
        if(!sIsInitialized) {
            sHttpClient = new CustomHttpClient();

            sHttpClient.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);


            // Create an instance of cookie store
            sCookieStore = new BasicCookieStore();

            // Create local HTTP context
            sLocalContext = new BasicHttpContext();
            // Bind custom cookie store to the context
            sLocalContext.setAttribute(ClientContext.COOKIE_STORE, sCookieStore);
            sIsInitialized = true;
        }
    }

    public static void clearCookies() {
        if (sCookieStore != null) {
            sCookieStore.clear();
        }
    }
}