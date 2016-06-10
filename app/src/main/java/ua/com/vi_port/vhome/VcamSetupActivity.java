package ua.com.vi_port.vhome;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

public class VcamSetupActivity extends Activity {

    public static final String VCAM_TOKEN = "vcam_token";
    public static final String USER_TOKEN = "user_token";

    private String mVcamToken = null;
    private String mUserToken = null;

    OnActivityInteraction mListener = null;


    public interface OnActivityInteraction {
        public void onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vcam_setup);

        Intent intent = getIntent();
        mVcamToken = intent.getStringExtra(VCAM_TOKEN);
        mUserToken = intent.getStringExtra(USER_TOKEN);

        VcamSetupFragment fragment = VcamSetupFragment.newInstance(mVcamToken, mUserToken);
        mListener = fragment;
        getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();

    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Выход из настроек камеры")
                .setMessage("Сохранить изменения?")
                .setCancelable(true)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if( mListener != null ) {
                            mListener.onBackPressed();
                        }
                        finish();
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }
}
