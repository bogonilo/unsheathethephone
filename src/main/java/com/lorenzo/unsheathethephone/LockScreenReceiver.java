package com.lorenzo.unsheathethephone;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class LockScreenReceiver extends WakefulBroadcastReceiver {

    Intent i;

    public LockScreenReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        i=new Intent(context,SensorManagerService.class);

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Log.e("LockScreenReceiver", "Broadcast Received, Screen OFF");
            startWakefulService(context, i);
        }
        else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Log.e("LockScreenReceiver","Broadcast Received, Screen ON");
            context.stopService(i);
            completeWakefulIntent(i);
        }

    }




}
