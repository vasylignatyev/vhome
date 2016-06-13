package ua.com.vi_port.vhome.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by vignatyev on 22.05.2016.
 */
public class ShareVcamUser {
    private static final SimpleDateFormat mMysqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public int RESTRICTION = 1;
    public String SCHEDULE;
    public Date EXPIRATION = null;
    public Date ISSUE_DATE = null;
    public String NAME;
    public String TYPE;

    public String toString(){
        JSONObject obj = new JSONObject();
        try {
            obj.put("RESTRICTION", RESTRICTION);
            obj.put("SCHEDULE", SCHEDULE);
            obj.put("EXPIRATION", (EXPIRATION == null) ? null : mMysqlDateFormat.format(EXPIRATION));
            obj.put("ISSUE_DATE", (ISSUE_DATE == null) ? null : mMysqlDateFormat.format(ISSUE_DATE));
            obj.put("NAME", NAME);
            obj.put("TYPE", TYPE);
            return obj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
