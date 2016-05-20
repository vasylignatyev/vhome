package ua.kiev.vignatyev.vhome1;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;

public class VcamSetupActivity extends Activity {

    public static final String VCAM_TOKEN = "vcam_token";

    private static String mVcamToken = null;

    public static String getVcamToken() {
        return mVcamToken;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vcam_setup);

        Intent intent = getIntent();
        mVcamToken = intent.getStringExtra(VCAM_TOKEN);
        Log.d("MyApp", "VcamSetupActivity::onCreate: " + mVcamToken);

        ActionBar actionBar = getActionBar();

        actionBar.setTitle("TEST");
        actionBar.hide();
    }

}
