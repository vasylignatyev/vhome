package ua.com.vi_port.vhome;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import ua.com.vi_port.vhome.ajax.HTTPManager;
import ua.com.vi_port.vhome.ajax.InstallTask;
import ua.com.vi_port.vhome.ajax.RequestPackage;
import ua.com.vi_port.vhome.gcm.QuickstartPreferences;
import ua.com.vi_port.vhome.gcm.RegistrationIntentService;

public class MainActivity extends FragmentActivity
//public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    /**
     * STATIC VARS
     */
    public static final String SERVER_URL = "http://vhome.dev.vi-port.com.ua/";

    //public static final String SERVER_URL = "http://vhome.vi-port.com.ua/";
    public static final String PREFS_NAME = "VhomeSharedPreferences";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static String mUserToken = null;
    private Context context = this;
    boolean doubleBackToExitPressedOnce = false;

    private boolean mLoggedIn = true;
    private boolean mCheckUpdate = true;
    private int mVersionCode;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private String mUserName, mUserPass;

    private String mGcmToken = null;

    private GoogleApiClient client;
    //private BroadcastReceiver mRegistrationBroadcastReceiver;

    public static String getUserToken() {
        return mUserToken;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try { Class.forName("android.os.AsyncTask"); } catch(Throwable ignore) { }
        super.onCreate(savedInstanceState);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mUserName = extras.getString(MySharedPreferences.USER_NAME, null);
            mUserPass = extras.getString(MySharedPreferences.USER_PASSWORD, null);
            mUserToken = extras.getString(MySharedPreferences.USER_TOKEN, null);
            mLoggedIn = true;
        }

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mConnectionStatusReceiver, new IntentFilter(QuickstartPreferences.NETWORK_CONNECTION_STATUS));
/*
        sp = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (savedInstanceState == null) {
            mUserName = sp.getString("userName", null);
            mUserPass = sp.getString("userPass", null);
        } else {
            mUserToken = savedInstanceState.getString("userToken");
            mUserName = savedInstanceState.getString("userName");
            mUserPass = savedInstanceState.getString("userPass", null);
        }

        if( (mUserName!=null) && (mUserPass!=null) ) {
            loggedIn(mUserToken, mUserName, mUserPass);
            //confirmAuthentication();
        }
*/


        // Set up the drawer.
        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        if (null != mNavigationDrawerFragment) {
            mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
                    (DrawerLayout) findViewById(R.id.drawer_layout));
        }

        if ( checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            //intent.putExtra( RegistrationIntentService.ARG_NUMBER, mNumber);
            startService(intent);
        }

    }

    @Override
    protected void onStart() {
        if(mCheckUpdate) {
            mCheckUpdate = false;
            getCurrentVersion();
        }
        super.onStart();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            mGcmToken = intent.getStringExtra(RegistrationIntentService.GCM_TOKEN);

            Log.d("MyApp", "MainActivity::mMessageReceiver action =" + action + ", mGcmToken =" + mGcmToken);
        }
    };

    private BroadcastReceiver mConnectionStatusReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //String action = intent.getAction();
            //Boolean ns = intent.getBooleanExtra(ConnectionChangeReceiver.NETWORK_STATUS, false);
            onNavigationDrawerItemSelected(0);
        }
    };

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.d("MyApp", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("userToken", mUserToken);
        outState.putString("userName", mUserName);
        outState.putString("userPass", mUserPass);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        Log.d("MyApp", "onNavigationDrawerItemSelected position: " + position);

        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment newFragment = null;

        if(!isNetworkConnected()) {
            newFragment = new NoNetworkFragment();
        }  else if (!mLoggedIn) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (mUserToken!=null) {
            switch (position) {
                case 0:
                    newFragment = VcamFragment.newInstance(mUserToken);
                    break;
                case 1:
                    newFragment = ScamFragment.newInstance(mUserToken);
                    break;
                case 2:
                    newFragment = MotionDetectFragment.newInstance(mUserToken, 0);
                    break;
                case 3:
                    newFragment = SharedMDFragment.newInstance(mUserToken, 0);
                    break;
                case 4:
                    Intent intent = new Intent(this, VcamSetupActivity.class);
                    intent.putExtra( VcamSetupActivity.USER_TOKEN, mUserToken);
                    startActivity(intent);

                    //newFragment = VcamSetupFragment.newInstance(null, null);
                    break;
                case 5:
                    logout();
                    break;
                default:
                    newFragment = VcamFragment.newInstance(mUserToken);
                    break;
            }
        }
        if (null != newFragment)
            fragmentManager.beginTransaction().replace(R.id.container, newFragment).commit();
    }

    private void logout() {
        mLoggedIn = false;
        //sp = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        //SharedPreferences.Editor editor = sp.edit();
        //editor.remove("userPass");
        //editor.apply();
        onNavigationDrawerItemSelected(0);
        unsetGcmRegistrationToken();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        finish();
    }

    public void shitHappens() {
        Toast.makeText(this, "Your account was used by another device. You should login again.", Toast.LENGTH_LONG).show();
        onNavigationDrawerItemSelected(4);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if ((null != mNavigationDrawerFragment) && (!mNavigationDrawerFragment.isDrawerOpen())) {
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return (id == R.id.action_settings) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            int backStackEntryCount = fragmentManager.getBackStackEntryCount();
            Log.d("MyApp", "backStackEntryCount = " + backStackEntryCount);
            if (backStackEntryCount != 0) {
                fragmentManager.popBackStack();
                return;
            }
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }
    public void doPositiveClick() {
        // Do stuff here.
        Log.i("MyApp", "Positive click!");
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void unsetGcmRegistrationToken() {
        //pd.show();
        //************************
        if ((mUserToken == null) || (mGcmToken == null))
            return;
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "ajax/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "unsetGcmRegistrationToken");
        rp.setParam("customer_token", mUserToken);
        rp.setParam("registration_token", mGcmToken);

        unsetGcmRegistrationTokenAsyncTask task = new unsetGcmRegistrationTokenAsyncTask();
        task.execute(rp);
    }

    public class unsetGcmRegistrationTokenAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String replay = HTTPManager.getData(params[0]);
            return replay;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp", "unsetGcmRegistrationTokenAsyncTask :" + s);
        }
    }

    public void getCurrentVersion() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            Log.d("MyApp", "versionName:" + packageInfo.versionName + " versionCode:" + packageInfo.versionCode);
            mVersionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "ajax/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "getCurrentVersion");

        GetCurrentVersionAsyncTask task = new GetCurrentVersionAsyncTask();
        task.execute(rp);
    }

    public class GetCurrentVersionAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String replay = HTTPManager.getData(params[0]);
            return replay;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp", "GetCurrentVersionAsyncTask :" + s);

            try {
                if( s != null) {
                    JSONObject obj = new JSONObject(s);
                    if (obj.has("versionCode")) {
                        int versionCode = obj.getInt("versionCode");
                        if (versionCode > mVersionCode) {
                            InstallTask installTask = new InstallTask(context, SERVER_URL + "downloads/vhome.apk");
                            installTask.execute();
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
