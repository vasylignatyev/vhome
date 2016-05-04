package ua.kiev.vignatyev.vhome1;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import ua.kiev.vignatyev.vhome1.ajax.HTTPManager;
import ua.kiev.vignatyev.vhome1.ajax.RequestPackage;
import ua.kiev.vignatyev.vhome1.models.Credentials;


/**
 * A placeholder fragment containing a simple view.
 */
public class LoginFragment extends Fragment implements  View.OnClickListener {
    public static final String PREFS_NAME = "VhomeSharedPreferences";
    public static final String TAG = "LoginFragment";

    private EditText etEmail;
    private EditText etPassword;

    FragmentActivity mActivity = null;

    private String mUserName;
    private String mUserPass;

    private OnLoginFragmentInteractionListener mListener;

    public LoginFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof FragmentActivity) {
            mActivity = (FragmentActivity) context;
        }
        try {
            mListener = (OnLoginFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnLoginFragmentInteractionListener");
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        populateViewForOrientation(inflater, (ViewGroup) getView());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FrameLayout frameLayout = new FrameLayout(mActivity);

        Credentials credentials = mListener.getCredentials();
        mUserName = credentials.getUserName();
        mUserPass = credentials.getUserPass();

        populateViewForOrientation(inflater, frameLayout);

        return frameLayout;
    }

    private void populateViewForOrientation(LayoutInflater inflater, ViewGroup viewGroup) {
        viewGroup.removeAllViewsInLayout();

        View view = inflater.inflate(R.layout.fragment_login, viewGroup);

        etEmail = (EditText) view.findViewById(R.id.etEmail);
        etPassword = (EditText) view.findViewById(R.id.etPassword);
        Button btnLogin = (Button) view.findViewById(R.id.btnLogin);
        Button btnRegisterRecovery = (Button) view.findViewById(R.id.btnRegisterRecovery);

        etEmail.setText(mUserName);
        etPassword.setText(mUserPass);

        btnLogin.setOnClickListener(this);
        btnRegisterRecovery.setOnClickListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnLoginFragmentInteractionListener {
        void loggedIn(String user_token, String user_name, String user_pass);
        Credentials getCredentials();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnLogin :
                //RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "/ajax/createToken.php");
                RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "/ajax/ajax.php");
                rp.setMethod("GET");
                mUserName = etEmail.getText().toString();
                mUserPass = etPassword.getText().toString();
                if( (mUserPass != null) && (mUserPass.length() != 0) ) {
                    rp.setParam("functionName", "create_token");
                    rp.setParam("user_email", mUserName);
                    rp.setParam("user_pass", mUserPass);
                    GetToken task = new GetToken();
                    task.execute(rp);
                } else {
                    if(etPassword.requestFocus()) {
                        //fa.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                        InputMethodManager mgr =      (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.showSoftInput(etPassword, InputMethodManager.SHOW_IMPLICIT);
                        Toast.makeText(mActivity, "Enter password", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case  R.id.btnRegisterRecovery :
                Fragment newFragment = RegistrationFragment.newInstance(etEmail.getText().toString());
                FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, newFragment).commit();
                break;
        }

    }

    public class GetToken extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String replay = HTTPManager.getData(params[0]);
            return replay;
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp",TAG + ": " + s);
            if( s == null ) {
                Toast.makeText(mActivity.getApplicationContext(), R.string.noNetwork, Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                JSONObject obj = new JSONObject(s);
                Log.d("MyApp", obj.toString());
                if (obj.has("error")) {
                    Toast.makeText(mActivity.getApplicationContext(),"Wrong, password!!!", Toast.LENGTH_SHORT).show();
                } else if (obj.has("token")) {
                    if(mListener != null){
                        String userToken = obj.getString("token");
                        mListener.loggedIn(userToken, mUserName, mUserPass);
                    }
                }
            } catch(JSONException e) {
                e.printStackTrace();
                Toast.makeText(mActivity.getApplicationContext(),"SERVER CONNECTION ERROR!!!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
