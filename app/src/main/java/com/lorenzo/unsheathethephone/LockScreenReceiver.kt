package com.lorenzo.unsheathethephone

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.legacy.content.WakefulBroadcastReceiver

class LockScreenReceiver : WakefulBroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        var i = Intent(context, SensorManagerService::class.java)

        if (intent.getAction() == Intent.ACTION_SCREEN_OFF) {
            Log.e("LockScreenReceiver", "Broadcast Received, Screen OFF")
            startWakefulService(context, i)
        } else if (intent.getAction() == Intent.ACTION_SCREEN_ON) {
            Log.e("LockScreenReceiver", "Broadcast Received, Screen ON")
            context.stopService(i)
            completeWakefulIntent(i)
        }
    }
}
