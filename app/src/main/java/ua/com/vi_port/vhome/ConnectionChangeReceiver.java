package ua.com.vi_port.vhome;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;

import ua.com.vi_port.vhome.gcm.QuickstartPreferences;

/**
 * Created by vignatyev on 19.04.2016.
 */
public class ConnectionChangeReceiver extends BroadcastReceiver {

    public static final String NETWORK_STATUS = "network_status";

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            int an = activeNetwork.getType();
            if (an == ConnectivityManager.TYPE_WIFI || an == ConnectivityManager.TYPE_MOBILE) {
                Intent connectionStatus = new Intent(QuickstartPreferences.NETWORK_CONNECTION_STATUS);
                connectionStatus.putExtra(NETWORK_STATUS, true);
                LocalBroadcastManager.getInstance(context).sendBroadcast(connectionStatus);
            }
        } else {
            Intent connectionStatus = new Intent(QuickstartPreferences.NETWORK_CONNECTION_STATUS);
            connectionStatus.putExtra(NETWORK_STATUS, false);
            LocalBroadcastManager.getInstance(context).sendBroadcast(connectionStatus);
        }

    }
}
