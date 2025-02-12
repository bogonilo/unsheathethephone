package com.lorenzo.unsheathethephone

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import androidx.legacy.content.WakefulBroadcastReceiver

class UTPService : Service() {
    var receiver: WakefulBroadcastReceiver? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)

        filter.addAction(Intent.ACTION_SCREEN_OFF)
        filter.addAction(Intent.ACTION_USER_PRESENT)

        receiver = LockScreenReceiver()
        registerReceiver(receiver, filter)

        Log.e("UTPService", "Service Started!")

        return START_STICKY
    }


    override fun onDestroy() {
        unregisterReceiver(receiver)
        super.onDestroy()
        Log.e("UTPService", "###onDestroy chiamato###")
    }
}
