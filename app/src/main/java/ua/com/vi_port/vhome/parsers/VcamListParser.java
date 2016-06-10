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
                            String VCAM_PORT =  options.getString("VCAM_PORT");
                            if((VCAM_PORT!=null) && !VCAM_PORT.equals("null")) {
                                vcam.VCAM_PORT = Integer.parseInt(VCAM_PORT);
                            }
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
                            String ROD_START_TIME =  options.getString("ROD_START_TIME");
                            if((ROD_START_TIME!=null) && !ROD_START_TIME.equals("null")) {
                                vcam.ROD_START_TIME = Integer.parseInt(ROD_START_TIME);
                            }
                        }
                        if(options.has("ON_AIR")) {
                            String ON_AIR =  options.getString("ON_AIR");
                            if((ON_AIR!=null) && !ON_AIR.equals("null")) {
                                vcam.ON_AIR = Integer.parseInt(ON_AIR);
                            }
                        }
                        if(options.has("R_CHUNK_TIME")) {
                            String R_CHUNK_TIME =  options.getString("R_CHUNK_TIME");
                            if((R_CHUNK_TIME!=null) && !R_CHUNK_TIME.equals("null")) {
                                vcam.R_CHUNK_TIME = Integer.parseInt(R_CHUNK_TIME);
                            }
                        }
                    }
                    /* ADD */
                    vcamList.add(vcam);
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
