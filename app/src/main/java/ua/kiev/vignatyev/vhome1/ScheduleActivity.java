package ua.kiev.vignatyev.vhome1;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import ua.kiev.vignatyev.vhome1.ajax.HTTPManager;
import ua.kiev.vignatyev.vhome1.ajax.RequestPackage;

public class ScheduleActivity extends Activity {

    public static final String USER_TOKEN = "user_token";
    public static final String VCAM_TOKEN = "vcam_token";


    private  ScheduleFragment mScheduleFragment;


    private String mVcamToken;
    private String mUserToken;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_schedule);

        Intent intent = getIntent();
        mVcamToken = intent.getStringExtra(VCAM_TOKEN);
        mUserToken = intent.getStringExtra(USER_TOKEN);

        getCustomerVcamSchedule(mVcamToken);
/*
        mScheduleFragment = ScheduleFragment.newInstance(mVcamToken, mUserToken);
        getFragmentManager().beginTransaction().add(R.id.container, mScheduleFragment).commit();
*/
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
                        updateCustomerVcamSchedule(mVcamToken, mUserToken, mScheduleFragment.getSchedule());
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
    /**
     * REST Request for Hash String
     */
    private void getCustomerVcamSchedule(String camToken) {
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "ajax/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "getCustomerVcamSchedule");
        rp.setParam("cam_token", camToken);

        getCustomerVcamScheduleAsyncTask task = new getCustomerVcamScheduleAsyncTask(camToken);
        task.execute(rp);
    }
    public class getCustomerVcamScheduleAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        private String camToken;

        public getCustomerVcamScheduleAsyncTask(String camToken) {
            this.camToken = camToken;
        }

        @Override
        protected String doInBackground(RequestPackage... params) {
            return HTTPManager.getData(params[0]);
        }
        @Override
        protected void onPostExecute(String schedule) {
            Log.d("MyApp", "getCustomerVcamSchedule:" + schedule);
            mScheduleFragment = ScheduleFragment.newInstance(mVcamToken, mUserToken, schedule);
            getFragmentManager().beginTransaction().add(R.id.container, mScheduleFragment).commit();

            //setSchedule(s);
        }
    }

    public void updateCustomerVcamSchedule(String camToken, String userToken, String scheduel) {
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "ajax/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "updateCustomerVcamSchedule");
        rp.setParam("cam_token", camToken);
        rp.setParam("user_token", userToken);
        rp.setParam("schedule", scheduel);

        getCustomerVcamScheduleAsyncTask task = new getCustomerVcamScheduleAsyncTask(camToken);
        task.execute(rp);
    }
    public class updateCustomerVcamScheduleAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        private String camToken;

        public updateCustomerVcamScheduleAsyncTask(String camToken) {
            this.camToken = camToken;
        }

        @Override
        protected String doInBackground(RequestPackage... params) {
            return HTTPManager.getData(params[0]);
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp", "updateCustomerVcamScheduleAsyncTask:" + s);

        }
    }
}
