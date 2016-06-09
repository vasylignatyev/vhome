package ua.com.vi_port.vhome;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import ua.com.vi_port.vhome.models.ShareVcamUser;

public class ShareScheduleActivity extends Activity {

    public static final String SCHEDULE = "schedule";

    private String mShedule;
    private ScheduleFragment mScheduleFragment;


    private static ShareVcamUser mShareVcamUser ;

    public static void setShareVcamUser( final ShareVcamUser shareVcamUser) {
        mShareVcamUser = shareVcamUser;
    }

    @Override
    protected void onDestroy() {
        mShareVcamUser = null;
        super.onDestroy();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_schedule);

        Intent intent = getIntent();
        mShedule = intent.getStringExtra(SCHEDULE);

        mScheduleFragment = ScheduleFragment.newInstance(mShedule);
        getFragmentManager().beginTransaction().add(R.id.container, mScheduleFragment).commit();

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Выход из настроек расписания")
                .setMessage("Сохранить изменения?")
                .setCancelable(true)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(mShareVcamUser != null) {
                            mShareVcamUser.SCHEDULE = mScheduleFragment.getSchedule();
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
