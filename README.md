# ezRest
ezRest is an Anroid library that allows for easy http requests and background tasking. It is a library that has been developed during mine (Johan) and Simon Evertssons (https://github.com/SimonEvertsson) different undertakings. It has been gradually improved to fit new needs over time.

## The idea
As with my other ezLibraries ezRest is supposed to be super simple to use in it's most basic funtions, while still allowing for full customization when nessecary. 

So... let's get started!

### Using ezRest - a simple GET method
````
this.mWebView = (WebView)findViewById(R.id.webview);
RestMethod rm = new RestMethod("http://www.google.com",RestMethod.Method.GET, new TextResponse(), new ResultReceiver() {
            @Override
            public void onResult(final RestMethod rm) {
                //Take care of the result
                mWebView.loadData(rm.mResponseEntity.getBody(),"text/html","");
            }
            @Override
            public void onError(RestMethod rm, int errorCode) {
                //Handle the error.
            }
});
rm.executeAsync();
```
### Using ezRest - a basic POST method.
```
RestMethod rm = new RestMethod(
                "http://api.trafikinfo.trafikverket.se/v1/data.json",
                RestMethod.Method.POST,
                new JsonEntity(mRequestObject), new JsonResponse(), new ResultReceiver() {
                @Override
            public void onResult(final RestMethod rm) {
                //Take care of the result (i've ignored the try catch to make it look prettier ;)
                JSONObject jObj = new JSONObject(rm.mResponseEntity.getBody());
            }
            @Override
            public void onError(RestMethod rm, int errorCode) {
                //Handle the error.
            }
});
```
There are two implemented RequestEntities (JsonEntity and Xml entity), however, they are very simple, they just set the content type and content given the arguments. These can be extended for each REST request for instance. Each model gets a JsonEntity that generated the body given the model. And the same goes for the Responses. 

The executeAsync utilizes ezDispatch (former ICDispatch). ezDispatch is a framework included in ezRest and is used to easily queue tasks on a background threads.

### Using ezDispatch - parsing a large response entity
```
public void onResult(final RestMethod rm) {
  ezDispatch.getInstance().executeOn(ezDispatch.HIGH, new Callable<Void>() {
    @Override
    public Void call() {
        //Pars data that takes a looooooooooong time
        for(int i = 0; i < 10000000; i++){
          ((i%2)%3)%4;
        }
        //Done, now we can post it back to the main thread 
        ezDispatch.getInstance().executeOn(ezDispatch.MAIN, new Callable<Void>() {
           @Override
          public Void call() {
            // Update views.
          }
        }
    }
 });
}
```
Note that each of the different Priorities (LOW, NORMAL, HIGH and MAIN) all different threadpools. Each priority guarantee an internal sequential execution. I.e. if block A,B and C is posted in that order on HIGH then ezDispatch guarantees that A will be done before B is executed and B will be done before C is executed. 





to be continued in a few day (2015-03-26) ;)
