package org.ethp.codepath.twitterclient.support.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Helper class to support connectivity checks
 * as documented in http://guides.codepath.com/android/Sending-and-Managing-Network-Requests#checking-for-network-connectivity
 */
public abstract class ConnectivityHelper {

    private static final String LOG_TAG = "ConnectivityHelper";

    public static boolean isNetworkAvailableAndOnline(Context context) {
        return (isNetworkAvailable(context) && isOnline());
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (Exception e)          {
            Log.d(LOG_TAG, "Connectivity check failure", e);
        }
        return false;
    }
}
