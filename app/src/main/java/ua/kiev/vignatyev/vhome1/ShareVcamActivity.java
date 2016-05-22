package ua.kiev.vignatyev.vhome1;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;

public class ShareVcamActivity extends Activity {

    public static final String USER_TOKEN = "user_token";
    public static final String VCAM_TOKEN = "vcam_token";

    private  ShareVcamFragment mShareShareVcamFragment;

    private String mVcamToken;
    private String mUserToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_vcam);

        Intent intent = getIntent();
        mVcamToken = intent.getStringExtra(VCAM_TOKEN);
        mUserToken = intent.getStringExtra(USER_TOKEN);

        mShareShareVcamFragment = ShareVcamFragment.newInstance(mVcamToken, mUserToken);
        getFragmentManager().beginTransaction().add(R.id.container, mShareShareVcamFragment).commit();

    }

}
