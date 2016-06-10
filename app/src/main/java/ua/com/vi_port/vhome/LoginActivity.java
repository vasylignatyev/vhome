package ua.com.vi_port.vhome;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;

import ua.com.vi_port.vhome.models.Credentials;

public class LoginActivity extends Activity
        implements LoginFragment.OnLoginFragmentInteractionListener  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);


    }

    @Override
    protected void onResume() {
        super.onResume();
        LoginFragment fragment = LoginFragment.newInstance();
        getFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
        fragment.setListener(this);

    }

    @Override
    public void loggedIn(String user_token, String user_name, String user_pass) {

        SharedPreferences sp = getSharedPreferences(MySharedPreferences.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(MySharedPreferences.USER_NAME, user_name);
        editor.putString(MySharedPreferences.USER_PASSWORD, user_pass);
        editor.putString(MySharedPreferences.USER_TOKEN, user_token);
        editor.apply();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MySharedPreferences.USER_NAME, user_name);
        intent.putExtra(MySharedPreferences.USER_PASSWORD, user_pass);
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
