package ua.kiev.vignatyev.vhome1.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import ua.kiev.vignatyev.vhome1.MainActivity;
import ua.kiev.vignatyev.vhome1.MotionDetectActivity;
import ua.kiev.vignatyev.vhome1.R;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        String motionDate = data.getString("motion_date",null);
        String vcamLocation = data.getString("vcam_location",null);
        String vcamName = data.getString("vcam_name",null);
        String vcam_token = data.getString("vcam_token","");

        Log.d("MyApp", "onMessageReceived vcam_token: " + vcam_token);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }
        SharedPreferences sp = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        if(vcam_token.length() == 32) {
            if( sp.getBoolean(vcam_token,true) ) {
                sendNotification(message, data);
            }
        } else {
            sendNotification(message, data);
        }
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message, Bundle data) {

        Intent intent = new Intent(this, MotionDetectActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(data);

        String iCustomerVcam = data.getString("i_customer_vcam", null);

        Log.d("MyApp", "sendNotification::iCustomerVcam = " + iCustomerVcam);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, Integer.parseInt(iCustomerVcam), intent,
            PendingIntent.FLAG_ONE_SHOT);
//            PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle(data.getString("vcam_name", "Детектор движения"))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(Integer.parseInt(iCustomerVcam), notificationBuilder.build());
    }
}
