package ua.com.vi_port.vhome.parsers;

import org.json.JSONException;
import org.json.JSONObject;

import ua.com.vi_port.vhome.models.Vcam;

/**
 * Created by vignatyev on 01.07.2015.
 */
public class VcamParser {
    public static Vcam parseFeed(String JSONString){
        if(JSONString == null) {
            return null;
        }
        try {
            JSONObject obj = new JSONObject(JSONString);
            Vcam vcam = new Vcam();
            if(obj.has("URL")) {
                vcam.URL = obj.getString("URL");
            }
            if(obj.has("HLS")) {
                vcam.HLS = obj.getInt("HLS");
            }
            if(obj.has("RTMP")) {
                vcam.RTMP = obj.getInt("RTMP");
            }
            if(obj.has("I_CUSTOMER")) {
                vcam.I_CUSTOMER = obj.getInt("I_CUSTOMER");
            }
            if(obj.has("I_CUSTOMER_VCAM")) {
                vcam.I_CUSTOMER_VCAM = obj.getInt("I_CUSTOMER_VCAM");
            }
            if(obj.has("ROD")) {
                vcam.ROD = obj.getInt("ROD");
            }
            if(obj.has("ROS")) {
                vcam.ROS = obj.getInt("ROS");
            }
            if(obj.has("ON_AIR")) {
                vcam.ON_AIR = obj.getInt("ON_AIR");
            }
            if(obj.has("RESTRICTION")) {
                vcam.RESTRICTION = obj.getInt("RESTRICTION");
            }
            if(obj.has("EMAIL")) {
                vcam.EMAIL = obj.getString("EMAIL");
            }
            if(obj.has("VENDOR_NAME")) {
                vcam.VENDOR_NAME = obj.getString("VENDOR_NAME");
            }
            if(obj.has("VCAM_NAME")) {
                vcam.VCAM_NAME = obj.getString("VCAM_NAME");
            }
            if(obj.has("SCHEDULE")) {
                vcam.SCHEDULE = obj.getString("SCHEDULE");
            }
            if(obj.has("CUSTOMER_VCAM_NAME")) {
                vcam.CUSTOMER_VCAM_NAME = obj.getString("CUSTOMER_VCAM_NAME");
            }
            if(obj.has("CUSTOMER_VCAM_LOGIN")) {
                vcam.CUSTOMER_VCAM_LOGIN = obj.getString("CUSTOMER_VCAM_LOGIN");
            }
            if(obj.has("CUSTOMER_VCAM_PASSWORD")) {
                vcam.CUSTOMER_VCAM_PASSWORD = obj.getString("CUSTOMER_VCAM_PASSWORD");
            }
            if(obj.has("TOKEN")) {
                vcam.TOKEN = obj.getString("TOKEN");
            }
            if(obj.has("TYPE")) {
                vcam.TYPE = obj.getString("TYPE");
            }
            /* SET OPTIONS */
            if(obj.has("OPTIONS")) {
                JSONObject options = obj.getJSONObject("OPTIONS");

                vcam.VCAM_PORT      =  options.optInt("VCAM_PORT");
                vcam.VCAM_IP        = options.optString("VCAM_IP");
                vcam.VCAM_LOCATION  = options.optString("VCAM_LOCATION");
                vcam.VCAM_VIDEO     = options.optString("VCAM_VIDEO");
                vcam.UTILITY_NAME   = options.optString("UTILITY_NAME");
                vcam.UTIL_IN_ARGS   = options.optString("UTIL_IN_ARGS");
                vcam.VCAM_PROTOCOL  = options.optString("VCAM_PROTOCOL");
                vcam.VCAM_AUDIO     = options.optString("VCAM_AUDIO");
                vcam.ROD_START_TIME = options.optInt("ROD_START_TIME");
                vcam.ON_AIR         = options.optInt("ON_AIR");
                vcam.R_CHUNK_TIME   = options.optInt("R_CHUNK_TIME");
                vcam.CONFIG_PORT     = options.optInt("CONFIG_PORT");
                vcam.VCAM_DNAME     = options.optString("VCAM_DNAME");
                vcam.EVENT_STATUS   = options.optInt("EVENT_STATUS");
                vcam.DEL_E_VIDEO    = options.optInt("DEL_E_VIDEO");
                vcam.SEND_EMAIL     = options.optInt("SEND_EMAIL");
                vcam.SEND_SMS       = options.optInt("SEND_SMS");
                vcam.SHARING_FOR_ALL = options.optInt("SHARING_FOR_ALL");
                vcam.LAT             = options.optString("LAT");
                vcam.LNG             = options.optString("LNG");
                vcam.SEND_EMAIL_ATTACH = options.optInt("SEND_EMAIL_ATTACH");
            }
            return vcam;
        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
