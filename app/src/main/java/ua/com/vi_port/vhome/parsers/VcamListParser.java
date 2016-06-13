package ua.com.vi_port.vhome.parsers;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ua.com.vi_port.vhome.models.Vcam;

/**
 * Created by vignatyev on 01.07.2015.
 */
public class VcamListParser {
    public static List<Vcam> parseFeed(String JSONString){
        if(JSONString == null) {
            return null;
        }
        JSONObject obj;
        try {
            obj = new JSONObject(JSONString);
            return null;
        } catch (JSONException e) {
            try {
                JSONArray vcamArray = new JSONArray(JSONString);
                //Log.d("MyApp", "Parsed: " + vcamArray.length());
                List<Vcam> vcamList = new ArrayList<>();
                for (int i = 0; i < vcamArray.length(); i++) {
                    obj = vcamArray.getJSONObject(i);
                    vcamList.add( VcamParser.parseFeed(obj.toString()) );
                }
                Log.d("MyApp", "VcamList length: " + vcamArray.length());
                return vcamList;
            } catch (JSONException ex) {
                Log.d("MyApp", "VcamListParser error: " + ex.getMessage());
                return null;
            }
        }
    }
}
