package ua.com.vi_port.vhome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ShareVcamUserActivity extends Activity {

    public static final String USER_TOKEN = "user_token";
    public static final String VCAM_TOKEN = "vcam_token";

    private ShareVcamUserFragment mShareShareVcamFragment;

    private String mVcamToken;
    private String mUserToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_vcam);

        Intent intent = getIntent();
        mVcamToken = intent.getStringExtra(VCAM_TOKEN);
        mUserToken = intent.getStringExtra(USER_TOKEN);

        mShareShareVcamFragment = ShareVcamUserFragment.newInstance(mVcamToken, mUserToken);
        getFragmentManager().beginTransaction().add(R.id.container, mShareShareVcamFragment).commit();

    }

}
