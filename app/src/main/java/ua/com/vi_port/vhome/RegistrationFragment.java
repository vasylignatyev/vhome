package ua.com.vi_port.vhome;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import ua.com.vi_port.vhome.ajax.HTTPManager;
import ua.com.vi_port.vhome.ajax.RequestPackage;
import ua.com.vi_port.vhome.validator.TextValidator;

public class RegistrationFragment extends Fragment implements View.OnFocusChangeListener {

    private static final String TAG = "RegistrationFragment";
    private static final String ARG_EMAIL = "eMail";


    private String mEMail;
    private EditText mEtEmail;
    private EditText mEtPass1;
    private Button btRegistration;

    Drawable mOriginalBackground;

    public static RegistrationFragment newInstance(String eMail) {
        RegistrationFragment fragment = new RegistrationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, eMail);
        fragment.setArguments(args);
        return fragment;
    }

    public RegistrationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEMail = getArguments().getString(ARG_EMAIL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        mEtEmail = (EditText)view.findViewById(R.id.etEmail);
        mEtPass1 = (EditText)view.findViewById(R.id.etPass1);
        btRegistration = (Button)view.findViewById(R.id.btRegistration);

        mEtEmail.addTextChangedListener(new TextValidator(mEtEmail) {
            @Override
            public void validate(TextView textView, String text) {
                Log.d("MyApp", text);
            }
        });

        mEtEmail.setText(mEMail);

        mEtEmail.setOnFocusChangeListener(this);
        mEtPass1.setOnFocusChangeListener(this);
        btRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCustomer();
            }
        });

        return view;
    }

    /**
     * REST Request for Vcam List
     */
    private void addCustomer(){
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "/ajax/addCustomer.php");
        rp.setMethod("GET");
        //rp.setParam("functionName", "addCustomer");
        rp.setParam("userEmail", mEtEmail.getText().toString() );
        rp.setParam("userPassword", mEtPass1.getText().toString());

        addCustomerAsyncTask task = new addCustomerAsyncTask();
        task.execute(rp);

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if( !hasFocus ) {
            switch (v.getId()) {
                case R.id.etEmail:
                    isEmailUniq(((EditText)v).getText().toString());
                    break;
            }
        }
    }

    /**
     * Async taskfor Vcam List
     */
    private class addCustomerAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String replay = HTTPManager.getData(params[0]);
            return replay;
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp", s);
         }
    }
    /**
     * REST Request for Vcam List
     */
    private void validatePassword(View view){
        TextView v  = (TextView) view;
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "/php/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "validatePassword");
        rp.setParam("password", v.getText().toString() );

        validatePasswordAsyncTask task = new validatePasswordAsyncTask();
        task.execute(rp);

    }
    /**
     * Async taskfor Vcam List
     */
    private class validatePasswordAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String replay = HTTPManager.getData(params[0]);
            return replay;
        }
        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject obj = new JSONObject(s);
                if(obj.has("error")){
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),obj.getString("error"), Toast.LENGTH_LONG);
                    toast.show();
                    mEtPass1.setError(obj.getString("error"));
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
    }
    /**
     * REST Request for Vcam List
     */
    private void isEmailUniq(String userEmail) {
        //pd.show();
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "/ajax/isEmailUniq.php");
        rp.setMethod("GET");
        rp.setParam("userEmail", userEmail);

        isEmailUniqAsyncTask task = new isEmailUniqAsyncTask();
        task.execute(rp);
    }
    /**
     * Async taskfor Vcam List
     */
    private class isEmailUniqAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String replay = HTTPManager.getData(params[0]);
            return replay;
        }
        @Override
        protected void onPostExecute(String s) {
            s = s.trim();
            if(s.equals("false")){
                mEtEmail.setError(getString(R.string.already_in_use));
                Toast toast = Toast.makeText(getActivity().getApplicationContext(),getString(R.string.already_in_use), Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }
}