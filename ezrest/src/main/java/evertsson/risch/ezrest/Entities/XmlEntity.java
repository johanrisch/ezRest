package evertsson.risch.ezrest.Entities;

/**
 * Created by johanrisch on 17/02/15.
 */
public class XmlEntity implements RestEntity {
    private final String mBody;

    public XmlEntity(String entity){
        mBody = entity;
    }
    @Override
    public String getBody() {
        return mBody;
    }

    @Override
    public String getContentType() {
        return "text/xml";
    }
}
