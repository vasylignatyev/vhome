package ua.com.vi_port.vhome;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
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

import static android.content.Context.MODE_PRIVATE;

public class RegistrationFragment extends Fragment implements View.OnFocusChangeListener {

    private static final String TAG = "RegistrationFragment";
    private static final String ARG_EMAIL = "eMail";


    private String mEMail;
    private EditText mEtEmail;
    private EditText mEtPass1;
    private Button btRegistration;

    private EditText etName, etSurename, etCountry, etCity, etStreetHouse, etPhone;

    private Boolean mResumed = false;

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
        getActivity().getActionBar().setTitle(getResources().getString(R.string.registration));
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
        etName = (EditText) view.findViewById(R.id.etName);
        etSurename = (EditText) view.findViewById(R.id.etSurename);
        etCountry = (EditText) view.findViewById(R.id.etCountry);
        etCity = (EditText) view.findViewById(R.id.etCity);
        etStreetHouse = (EditText) view.findViewById(R.id.etStreetHouse);
        etPhone = (EditText) view.findViewById(R.id.etPhone);


        mEtEmail.setOnFocusChangeListener(this);
        mEtPass1.setOnFocusChangeListener(this);
        btRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean error = false;
                if(etPhone.getText().length() == 0) {
                    etPhone.setError("Обязательное поле");
                    error = true;
                }
                if(etName.getText().length() == 0){
                    etName.setError("Обязательное поле");
                    error = true;
                }
                if(!error) {
                    addCustomer();
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mResumed = true;

    }

    @Override
    public void onPause() {
        mResumed = false;
        super.onPause();
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if( !hasFocus ) {
            switch (v.getId()) {
                case R.id.etEmail:
                    isEmailUniq(((EditText)v).getText().toString());
                    break;
                case R.id.etPass1:
                    validatePassword(v);
                    break;
            }
        }
    }

    /**
     * REST Request for Vcam List
     */
    private void addCustomer(){
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "ajax/ajax.php");
        rp.setMethod("GET");
        rp.setParam("user_email", mEtEmail.getText().toString() );
        rp.setParam("user_password", mEtPass1.getText().toString());

        if( etName.getText().length() >0  ) {
            rp.setParam("firstname", etName.getText().toString());
        }
        if( etSurename.getText().length() >0  ) {
            rp.setParam("lastname", etSurename.getText().toString());
        }
        if( etCountry.getText().length() >0  ) {
            rp.setParam("country", etCountry.getText().toString());
        }
        if( etCity.getText().length() >0  ) {
            rp.setParam("city]", etCity.getText().toString());
        }
        if( etStreetHouse.getText().length() >0  ) {
            rp.setParam("address", etStreetHouse.getText().toString());
        }
        if( etPhone.getText().length() >0  ) {
            rp.setParam("telephone", etPhone.getText().toString());
        }
        rp.setParam("functionName", "addCustomer");
        addCustomerAsyncTask task = new addCustomerAsyncTask();
        task.execute(rp);
    }

    private class addCustomerAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String replay = HTTPManager.getData(params[0]);
            return replay;
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp", s);
            try {
                JSONObject object = new JSONObject(s);
                if(object.has("i_customer")) {
                    SharedPreferences sp = getActivity().getSharedPreferences(MySharedPreferences.PREFS_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor et = sp.edit();
                    et.putString(MySharedPreferences.USER_NAME, mEtEmail.getText().toString());
                    et.putString(MySharedPreferences.USER_PASSWORD, mEtPass1.getText().toString());
                    et.commit();

                    FragmentManager fm = getFragmentManager();
                    for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                        fm.popBackStack();
                    }

                    LoginFragment fragment = LoginFragment.newInstance();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, fragment);
                    fragment.setListener((LoginActivity)getActivity());
                    transaction.commit();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * REST Request for Vcam List
     */
    private void validatePassword(View view){
        TextView v  = (TextView) view;
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "ajax/ajax.php");
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
                    mEtPass1.setError(obj.getString("error"));
                } else {
                    mEtPass1.setError(null);
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
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "ajax/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "isEmailUniq");
        rp.setParam("user_email", userEmail);

        if(mResumed) {
            new isEmailUniqAsyncTask().execute(rp);
        }
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
            if(s == null)    {
                return;
            }
            s = s.trim();
            if(s.equals("false")){
                mEtEmail.setError(getString(R.string.already_in_use));
            } else {
                mEtEmail.setError(null);
            }
        }
    }
}