package com.boolan.news.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by SpaceRover on 2016/9/30.
 */

public class NetworkState {

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService
                (Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] infos = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo info : infos) {
            if (info.isConnected()) {
                return true;
            }
        }
        return false;
    }
}
