package com.fyp.motorcyclefix.Services;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SendNotificationService {

    //Constants
    private static final String  FCM_API = "https://fcm.googleapis.com/fcm/send";
    private static final String serverKey = "key=" + "AAAAkzPIZqg:APA91bHGefpHFuCN9DFHP_NMq9Xqfgg29JTjhelCpIniwj0ZROv_i51eFDLdZkAAgWhl3oEEKU9ItVrudjzDPvEYl5wF6ty7rFGGUDWfjy47TGcd68_-h1YCodyCA4lx8sKtUYcgo714";
    private static final String contentType = "application/json";
    private static final String TAG = "NOTIFICATION TAG";

    //Method creates a json type method body
    public static void sendNotification(Context context, String topic, String title, String message){
        //instance of json Object
        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", title);
            notifcationBody.put("body", message);

            notification.put("to", "/topics/"+topic);
            notification.put("notification", notifcationBody);
        } catch (JSONException e) {
            Log.e(TAG, "onCreate: " + e.getMessage() );
        }
        notificationWrapper(notification, context);
    }

    //Method creates a jason based post request to firebase cloud messsaging service and gets a response back
    private static void notificationWrapper(JSONObject notification, final Context context) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Request error", Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onErrorResponse: Didn't work");
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //json request header
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        //c
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
}
