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

                if(options.has("VCAM_PORT")) {
                    vcam.VCAM_PORT = options.getInt("VCAM_PORT");
                }
                if(options.has("VCAM_IP")) {
                    vcam.VCAM_IP = options.getString("VCAM_IP");
                }
                if(options.has("VCAM_LOCATION")) {
                    vcam.VCAM_LOCATION = options.getString("VCAM_LOCATION");
                }
                if(options.has("VCAM_VIDEO")) {
                    vcam.VCAM_VIDEO = options.getString("VCAM_VIDEO");
                }
                if(options.has("UTILITY_NAME")) {
                    vcam.UTILITY_NAME = options.getString("UTILITY_NAME");
                }
                if(options.has("UTIL_IN_ARGS")) {
                    vcam.UTIL_IN_ARGS = options.getString("UTIL_IN_ARGS");
                }
                if(options.has("VCAM_PROTOCOL")) {
                    vcam.VCAM_PROTOCOL = options.getString("VCAM_PROTOCOL");
                }
                if(options.has("VCAM_AUDIO")) {
                    vcam.VCAM_AUDIO = options.getString("VCAM_AUDIO");
                }
                if(options.has("ROD_START_TIME")) {
                    vcam.ROD_START_TIME = options.getInt("ROD_START_TIME");
                }
                if(options.has("ON_AIR")) {
                    vcam.ON_AIR = options.getInt("ON_AIR");
                }
                if(options.has("R_CHUNK_TIME")) {
                    vcam.R_CHUNK_TIME = options.getInt("R_CHUNK_TIME");
                }
            }
            return vcam;
        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
