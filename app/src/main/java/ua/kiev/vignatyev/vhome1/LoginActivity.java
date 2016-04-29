package ua.kiev.vignatyev.vhome1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import ua.kiev.vignatyev.vhome1.models.Credentials;

public class LoginActivity extends FragmentActivity
        implements LoginFragment.OnLoginFragmentInteractionListener  {

    private String mUserToken = null;
    private String mUserPass = null;
    private String mUserName = null;
    private boolean mLoggedIn = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
    }

    @Override
    public void loggedIn(String user_token, String user_name, String user_pass) {

        mUserToken = user_token;
        mLoggedIn = true;
        mUserName = user_name;
        mUserPass = user_pass;

        SharedPreferences sp = getSharedPreferences(MySharedPreferences.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(MySharedPreferences.USER_NAME, mUserName);
        editor.putString(MySharedPreferences.USER_PASSWORD, mUserPass);
        editor.putString(MySharedPreferences.USER_TOKEN, user_token);
        editor.apply();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MySharedPreferences.USER_NAME, mUserName);
        intent.putExtra(MySharedPreferences.USER_PASSWORD, mUserPass);
        intent.putExtra(MySharedPreferences.USER_TOKEN, user_token);
        startActivity(intent);

        finish();
    }

    @Override
    public Credentials getCredentials() {
        SharedPreferences sp = getSharedPreferences(MySharedPreferences.PREFS_NAME, MODE_PRIVATE);
        String userName = sp.getString(MySharedPreferences.USER_NAME, null);
        String userPass = sp.getString(MySharedPreferences.USER_PASSWORD, null);
        Boolean savePassword = sp.getBoolean(MySharedPreferences.SAVE_PASSWORD, true);
        return new Credentials(userName, userPass, savePassword);
    }
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }


}
