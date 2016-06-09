package ua.com.vi_port.vhome;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ua.com.vi_port.vhome.ajax.HTTPManager;
import ua.com.vi_port.vhome.ajax.RequestPackage;
import ua.com.vi_port.vhome.models.Vcam;
import ua.com.vi_port.vhome.parsers.VcamParser;

import static android.content.Context.MODE_PRIVATE;

public class VcamSetupFragment extends Fragment
    implements VcamSetupActivity.OnActivityInteraction {

    public static final String VCAM_TOKEN = "vcam_token";
    public static final String USER_TOKEN = "user_token";

    private VcamSetupActivity.OnActivityInteraction mListener;

    public void setListener(VcamSetupActivity.OnActivityInteraction listener ) {
        mListener = listener;
    }


    private VcamSetupActivity mVcamSetupActivity;

    private Spinner spinManufacturer, spinModel, spinProtocol;
    private Switch swAudioStream, swNotifications;
    private EditText etVcamIP, etConnectionPort, etVcamLogin, etVcamPassword, etVcamLocation, etVcamName, etRecordDuration;

    private String mUserToken = null;
    //Vcam info
    private String mVcamToken = null;
    private String mVendorName = null;
    private String mVcamModel = null;
    private Boolean mVcamAudio = false;
    private String mVcamProtocol;
    private String mVcamIP;
    private String mVcamLocation;
    private String mCustomerVcamLogin;
    private String mCustomerVcamPassword;
    private String mCustomerVcamName;
    private int mVcamPort;
    private int mRecordDuration;

    public VcamSetupFragment() {
    }

    public static VcamSetupFragment newInstance(String vcamToken, String userToken) {
        VcamSetupFragment fragment = new VcamSetupFragment();
        Bundle args = new Bundle();
        args.putString( VCAM_TOKEN, vcamToken);
        args.putString( USER_TOKEN, userToken);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if( context instanceof VcamSetupActivity) {
            mVcamSetupActivity = (VcamSetupActivity)context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_vcam_setup, container, false);

        if (getArguments() != null) {
            mVcamToken = getArguments().getString(VCAM_TOKEN, null);
            mUserToken = getArguments().getString(USER_TOKEN, null);
        }
        Log.d("MyApp", "VcamSetupFragment::onCreateView: " + mVcamToken);

        spinManufacturer = (Spinner) view.findViewById(R.id.spinManufacturer);
        spinModel = (Spinner) view.findViewById(R.id.spinModel);
        swAudioStream = (Switch) view.findViewById(R.id.swAudioStream);
        swNotifications = (Switch) view.findViewById(R.id.swNotifications);
        etVcamIP = (EditText) view.findViewById(R.id.etVcamIP);
        etConnectionPort = (EditText) view.findViewById(R.id.etConnectionPort);
        etVcamLogin = (EditText) view.findViewById(R.id.etVcamLogin);
        etVcamPassword = (EditText) view.findViewById(R.id.etVcamPassword);
        etVcamLocation = (EditText) view.findViewById(R.id.etVcamLocation);
        etVcamName = (EditText) view.findViewById(R.id.etVcamName);
        etRecordDuration  = (EditText) view.findViewById(R.id.etRecordDuration);

        if(mVcamToken != null) {
            spinManufacturer.setEnabled(false);
            spinManufacturer.setClickable(false);
            spinModel.setEnabled(false);
            spinModel.setClickable(false);
        }
        SharedPreferences sp = getActivity().getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        swNotifications.setChecked(sp.getBoolean(mVcamToken,true));

        swNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getActivity().getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE).edit();
                editor.putBoolean(mVcamToken,isChecked);
                editor.apply();
            }
        });

        spinManufacturer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String vendorName = (String) parent.getItemAtPosition(position);
                Log.d("MyApp", "Spinner click: " + vendorName);
                getVCamsByVendor(vendorName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String model = (String) parent.getItemAtPosition(position);
                Log.d("MyApp", "Spinner click: " + model);
                getOptionsByVcamName(model, false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinProtocol = (Spinner) view.findViewById(R.id.spinProtocol);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("MyApp", "VcamSetupFragment::onActivityCreated: " + mVcamToken);
        getCustomerVcamByToken(mVcamToken);
    }

    /**
     * REST Request for Hash String
     */
    public void getCustomerVcamByToken(String camToken) {
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "ajax/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "getCustomerVcamByToken");
        rp.setParam("vcam_token", camToken);

        getCustomerVcamByTokenAsyncTask task = new getCustomerVcamByTokenAsyncTask(camToken);
        task.execute(rp);
    }
    public class getCustomerVcamByTokenAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        private String camToken;

        public getCustomerVcamByTokenAsyncTask(String camToken) {
            this.camToken = camToken;
        }

        @Override
        protected String doInBackground(RequestPackage... params) {
            return HTTPManager.getData(params[0]);
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp", "getCustomerVcamByToken:" + s);
            Vcam vcam = VcamParser.parseFeed(s);
            mVendorName = vcam.VENDOR_NAME;
            if(vcam.VCAM_AUDIO != null) {
                Log.d("MyApp", "VCAM_AUDIO: " + vcam.VCAM_AUDIO);
                mVcamAudio = vcam.VCAM_AUDIO.equals("AUDIO_ON");
            }
            Log.d("MyApp", "mVcamModel: " + vcam.VCAM_NAME);
            mVcamModel = vcam.VCAM_NAME;
            Log.d("MyApp", "mVcamProtocol: " + vcam.UTIL_IN_ARGS);
            mVcamProtocol = vcam.UTIL_IN_ARGS;
            mVcamIP = vcam.VCAM_IP;
            mVcamLocation = vcam.VCAM_LOCATION;
            mCustomerVcamLogin = vcam.CUSTOMER_VCAM_LOGIN;
            mCustomerVcamPassword = vcam.CUSTOMER_VCAM_PASSWORD;
            mVcamPort = vcam.VCAM_PORT;
            mCustomerVcamName = vcam.CUSTOMER_VCAM_NAME;
            mRecordDuration = vcam.R_CHUNK_TIME;

            etVcamIP.setText(mVcamIP);
            etConnectionPort.setText(Integer.toString(mVcamPort));
            etVcamLogin.setText(mCustomerVcamLogin);
            etVcamPassword.setText(mCustomerVcamPassword);
            etVcamLocation.setText(mVcamLocation);
            etVcamName.setText(mCustomerVcamName);
            etRecordDuration.setText(Integer.toString(mRecordDuration));

            getVendors(mVendorName, false);
        }
    }
    public void getVendors(String vendorName, Boolean readOnly) {
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "ajax/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "getVendors");

        getVendorsAsyncTask task = new getVendorsAsyncTask(vendorName, readOnly);
        task.execute(rp);
    }
    public class getVendorsAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        private String vendorName;
        private Boolean readOnly;
        private int index = -1;

        public getVendorsAsyncTask(String vendorName, Boolean readOnly) {
            this.vendorName = vendorName;
            this.readOnly =readOnly;
        }
        @Override
        protected String doInBackground(RequestPackage... params) {
            return HTTPManager.getData(params[0]);
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp", "getVendors:" + s);
            if( s == null) {
                return;
            }
            try {
                List<String> vendorList = new ArrayList<String>();
                JSONArray jsonArray = new JSONArray(s);
                for(int i = 0 ; i < jsonArray.length() ; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if(jsonObject.has("NAME")) {
                        String vName = jsonObject.getString("NAME");
                        vendorList.add(vName);
                        if((vendorName != null) && (vendorName.equals(vName))) {
                            index = i ;
                        }
                    }
                }

                ArrayAdapter<String> vendorArray = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, vendorList);
                if( vendorArray != null ) {
                    vendorArray.setDropDownViewResource(android.R.layout.simple_spinner_item);
                    spinManufacturer.setAdapter(vendorArray);
                    spinManufacturer.setClickable(!readOnly);
                    if(index != -1) {
                        spinManufacturer.setSelection(index);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    public void getVCamsByVendor(String vendor) {
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "ajax/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "getVCamsByVendor");
        rp.setParam("vendor", vendor);

        getVCamsByVendorAsyncTask task = new getVCamsByVendorAsyncTask();
        task.execute(rp);
    }
    public class getVCamsByVendorAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        int index = -1;

        @Override
        protected String doInBackground(RequestPackage... params) {
            return HTTPManager.getData(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp", "getVCamsByVendor:" + s);
            if(s == null) {
                return;
            }
            try {
                List<String> modelList = new ArrayList<String>();
                JSONArray jsonArray = new JSONArray(s);
                for(int i = 0 ; i < jsonArray.length() ; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if(jsonObject.has("NAME")) {
                        String name =  jsonObject.getString("NAME");
                        modelList.add(name);
                        if((mVcamModel != null) && (mVcamModel.equals(name))) {
                            index = i;
                        }
                    }
                }

                ArrayAdapter<String> modelArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, modelList);
                if( modelArrayAdapter != null ) {
                    modelArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                    spinModel.setAdapter(modelArrayAdapter);
                    //spinModel.setClickable(!readOnly);
                    if(index != -1) {
                        spinModel.setSelection(index);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void getOptionsByVcamName(String vcamName, Boolean readOnly) {
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "ajax/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "getOptionsByVcamName");
        rp.setParam("vcam_name", vcamName);

        getOptionsByVcamNameAsyncTask task = new getOptionsByVcamNameAsyncTask();
        task.execute(rp);
    }
    public class getOptionsByVcamNameAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        int index = -1;

        @Override
        protected String doInBackground(RequestPackage... params) {
            return HTTPManager.getData(params[0]);
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp", "getOptionsByVcamName:" + s);
            if(s == null) {
                return;
            }

            try {
                JSONObject jsonObj = new JSONObject(s);
                if( jsonObj.has("PROTOCOL")) {
                    List<String> protocolList = new ArrayList<String>();
                    JSONArray jsonArray = jsonObj.getJSONArray("PROTOCOL");
                    for( int i =0 ; i < jsonArray.length() ; i++ ) {
                        JSONObject protocol = jsonArray.getJSONObject(i);
                        if( protocol.has("NAME")) {
                            String protocolName = protocol.getString("NAME");
                            String protocolVaue = protocol.getString("VALUE");
                            protocolList.add(protocolName);
                            if(protocolVaue.equals(mVcamProtocol)) {
                                index = i;
                            }
                        }
                    }
                    Log.d("MyApp", "protocolList: " + protocolList.size() );
                    ArrayAdapter<String> protocolArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, protocolList);
                    if( protocolArrayAdapter != null ) {
                        protocolArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                        spinProtocol.setAdapter(protocolArrayAdapter);
                        if(index != -1) {
                            spinProtocol.setSelection(index);
                        }
                    }
                    if( jsonObj.has("AUDIO")) {
                        String audio = jsonObj.getString("AUDIO");
                        if( audio.equals("AUDIO_ON")) {
                            swAudioStream.setClickable(true);
                            swAudioStream.setChecked(mVcamAudio);
                        } else {
                            swAudioStream.setClickable(true);
                            swAudioStream.setChecked(false);
                        }
                    }

                } else {
                    Log.d("MyApp", "getOptionsByVcamName has no PROTOCOL");
                }
            } catch (JSONException e) {
                Log.d("MyApp", e.getMessage());
                e.printStackTrace();
            }
        }
    }
    /*
    private String mVendorName = null;
    private String mVcamModel = null;
    private Boolean mVcamAudio = false;
    private String mVcamProtocol;
    private String mVcamIP;
    private String mVcamLocation;
    private String mCustomerVcamName;
    private int mVcamPort;
    private int mRecordDuration;
*/
    @Override
    public void onBackPressed() {
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "ajax/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "updateCustomerVcam");
        rp.setParam("cam_token", mVcamToken);
        rp.setParam("camLogin", mCustomerVcamLogin);
        rp.setParam("camPass", mCustomerVcamPassword);
        rp.setParam("camName", mCustomerVcamName);
        rp.setParam("user_token", mUserToken);

        updateCustomerVcamAsyncTask task = new updateCustomerVcamAsyncTask();
        task.execute(rp);
    }
    public class updateCustomerVcamAsyncTask extends AsyncTask<RequestPackage, Void, String> {

        @Override
        protected String doInBackground(RequestPackage... params) {
            return HTTPManager.getData(params[0]);
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp", "updateCustomerVcam: " + s);
        }
    }

}
