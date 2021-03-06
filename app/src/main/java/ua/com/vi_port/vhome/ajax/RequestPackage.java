package ua.com.vi_port.vhome.ajax;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vignatyev on 26.06.2015.
 */
public class RequestPackage {
    private String uri;
    private String method = "GET";
    private Map<String,String> params = new HashMap<>();

    public RequestPackage(String uri){
        this.uri = uri;
    }
    public String getUri() {
        return uri;
    }
    public void setUri(String uri) {
        this.uri = uri;
    }
    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }
    public Map<String, String> getParams() {
        return params;
    }
    public void setParams(Map<String, String> params) {
        this.params = params;
    }
    public void setParam(String key, String value){
        params.put(key,value);
    }
    public String getEncodedParams(){
        StringBuilder sb = new StringBuilder();
        for(String key : params.keySet()){
            String value = null;
            String encodeKey = null;
            try {
                if(params.get(key) != null) {
                    value = URLEncoder.encode(params.get(key), "UTF-8");
                    encodeKey = URLEncoder.encode(key, "UTF-8");
                    if (sb.length() > 0) {
                        sb.append("&");
                    }
                    sb.append(encodeKey + "=" + value);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}