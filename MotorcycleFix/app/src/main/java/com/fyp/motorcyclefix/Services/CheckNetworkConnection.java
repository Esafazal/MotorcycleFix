package com.fyp.motorcyclefix.Services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckNetworkConnection {

    public static boolean isNetworkAvailable(Context context) {
        //creating a new conectivityManager Object
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //getting netork infomation via Networkinfo class
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        //returning connection status as a boolean
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
