package evertsson.risch.ezrest;


public interface ResultReceiver {
    public void onResult(RestMethod rm);
    public void onError(RestMethod rm, int errorCode);
}
