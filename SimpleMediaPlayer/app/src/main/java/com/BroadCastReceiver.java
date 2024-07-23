package com;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BroadCastReceiver extends BroadcastReceiver {
    public final String TAG = "TAD-ZEILA";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Do some thing
        Log.d(TAG, "Received message:" + intent.getAction());
        MainActivity.getInstance().onHandleBroadcast();
    }
}
