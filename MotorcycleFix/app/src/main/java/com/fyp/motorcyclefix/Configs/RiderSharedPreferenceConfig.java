package com.fyp.motorcyclefix.Configs;

import android.content.Context;
import android.content.SharedPreferences;

import com.fyp.motorcyclefix.R;

public class RiderSharedPreferenceConfig {

    private SharedPreferences preferences;
    private Context context;

    public RiderSharedPreferenceConfig(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(context.getResources()
                .getString(R.string.rider_login_preference), Context.MODE_PRIVATE);

    }

    public void writeRiderLoginStatus(boolean status){

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(context.getResources().getString(R.string.rider_login_status_preference), status);
        editor.commit();
    }

    public boolean readRiderLoginStatus(){

        boolean status = false;
        status = preferences.getBoolean(context.getResources().getString(R.string.rider_login_status_preference), false);
        return status;
    }
}
