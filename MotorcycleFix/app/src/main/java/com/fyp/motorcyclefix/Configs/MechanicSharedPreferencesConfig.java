package com.fyp.motorcyclefix.Configs;

import android.content.Context;
import android.content.SharedPreferences;

import com.fyp.motorcyclefix.R;

public class MechanicSharedPreferencesConfig {

    private SharedPreferences preferences;
    private Context context;

    public MechanicSharedPreferencesConfig(Context context) {
        this.context = context;

        preferences = context.getSharedPreferences(context.getResources()
                .getString(R.string.mechanic_login_preference), Context.MODE_PRIVATE);
    }

    public void writeMechanicLoginStatus(boolean status){

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(context.getResources().getString(R.string.mechanic_login_status_preference), status);
        editor.commit();
    }

    public boolean readMechanicLoginStatus(){

        boolean status = false;
        status = preferences.getBoolean(context.getResources().getString(R.string.mechanic_login_status_preference), false);
        return status;
    }
}
