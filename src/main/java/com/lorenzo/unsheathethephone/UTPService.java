package com.lorenzo.unsheathethephone;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class UTPService extends Service {
    WakefulBroadcastReceiver mReceiver;
    public UTPService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return  null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);

        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);

        mReceiver = new LockScreenReceiver();
        registerReceiver(mReceiver, filter);

        Log.e("UTPService","Service Started!");
        return START_STICKY;
    }



    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mReceiver);
        Log.e("UTPService","###onDestroy chiamato###");
    }

}
