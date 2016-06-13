package ua.com.vi_port.vhome;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import ua.com.vi_port.vhome.ajax.HTTPManager;
import ua.com.vi_port.vhome.ajax.RequestPackage;
import ua.com.vi_port.vhome.models.Credentials;


public class LoginFragment extends Fragment implements  View.OnClickListener {
    private static final String TAG = "LoginFragment";

    private EditText etEmail;
    private EditText etPassword;

    private String mUserName;
    private String mUserPass;

    private OnLoginFragmentInteractionListener mListener;
    public void setListener( OnLoginFragmentInteractionListener listener) {
        mListener = listener;
    }

    public LoginFragment() {
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (OnLoginFragmentInteractionListener) context;
        //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        populateViewForOrientation(inflater, (ViewGroup) getView());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FrameLayout frameLayout = new FrameLayout(getActivity());
        if(mListener != null) {
            Credentials credentials = mListener.getCredentials();
            mUserName = credentials.getUserName();
            mUserPass = credentials.getUserPass();
        }
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
                RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "/ajax/ajax.php");
                rp.setMethod("GET");
                mUserName = etEmail.getText().toString();
                mUserPass = etPassword.getText().toString();
                if( mUserPass.length() != 0 ) {
                    rp.setParam("functionName", "create_token");
                    rp.setParam("user_email", mUserName);
                    rp.setParam("user_pass", mUserPass);
                    GetToken task = new GetToken();
                    task.execute(rp);
                } else {
                    if(etPassword.requestFocus()) {
                        /*
                        //fa.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                        InputMethodManager mgr = (getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.showSoftInput(etPassword, InputMethodManager.SHOW_IMPLICIT);
                        Toast.makeText(mActivity, "Enter password", Toast.LENGTH_SHORT).show();
                        */
                    }
                }
                break;
            case  R.id.btnRegisterRecovery :
                RegistrationFragment newFragment = RegistrationFragment.newInstance(etEmail.getText().toString());
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, newFragment).commit();
                break;
        }
    }
    private class GetToken extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            return HTTPManager.getData(params[0]);
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp",TAG + ": " + s);
            if( s == null ) {
                Toast.makeText(getActivity(), R.string.noNetwork, Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                JSONObject obj = new JSONObject(s);
                Log.d("MyApp", obj.toString());
                if (obj.has("error")) {
                    Toast.makeText(getActivity().getApplicationContext(),"Wrong, password!!!", Toast.LENGTH_SHORT).show();
                } else if (obj.has("token")) {
                    if(mListener != null) {
                        String userToken = obj.getString("token");
                        mListener.loggedIn(userToken, mUserName, mUserPass);
                    }
                }
            } catch(JSONException e) {
                e.printStackTrace();
                Toast.makeText(getActivity().getApplicationContext(),"SERVER CONNECTION ERROR!!!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
