package evertsson.risch.ezrest.Entities;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by johanrisch on 17/02/15.
 */
public class JsonEntity implements RestEntity {
    private final String mBody;

    public JsonEntity(String json){
        this.mBody = json;
    }
    public JsonEntity(JSONObject jsonObject){
        this.mBody = jsonObject.toString();
    }
    public JsonEntity(JSONArray jsonArray){
        this.mBody = jsonArray.toString();
    }


    @Override
    public String getBody(){
        return mBody;
    }

    @Override
    public String getContentType() {
        return "application/json";
    }
}
