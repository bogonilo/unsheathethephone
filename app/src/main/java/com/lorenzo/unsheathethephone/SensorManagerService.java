package com.lorenzo.unsheathethephone;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class SensorManagerService extends Service implements SensorEventListener{
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private AudioTrack at;
    MediaPlayer mediaPlayer;
    PowerManager pm;
    PowerManager.WakeLock wl;
    SharedPreferences shared;
    int sound;
    int acc;
    int sensibility;
    byte fileData[];
    int audioLength;
    AssetFileDescriptor afd;

    public SensorManagerService() {
    }

    @Override
    public void onCreate() {
        Log.e("SensorManagerService","onCreate chiamato");
        pm = (PowerManager)getApplicationContext().getSystemService(
                getApplicationContext().POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "pmlock" );
        shared = getSharedPreferences(UTPMainActivity.PREFS, MODE_PRIVATE);
        sound = UTPMainActivity.retSound(shared.getInt("sound", 0));
        acc = shared.getInt("acc", R.id.accNorm);
        sensibility = 950 - shared.getInt("sensibility",150);

      /*  switch(shared.getInt("sound", 0)){
            case 0:
                audioLength = UTPMainActivity.sword_length;
            case 1:
                audioLength = UTPMainActivity.fart_length;
            case 2:
                audioLength = UTPMainActivity.lightsaber_length;
            case 3:
                audioLength = UTPMainActivity.whip_length;
        }

       at = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
               AudioFormat.CHANNEL_OUT_MONO,
               AudioFormat.ENCODING_PCM_8BIT, audioLength,
               AudioTrack.MODE_STATIC);


     fileData = new byte[audioLength];
     try{
     //   InputStream inputStream = new BufferedInputStream(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/lightsaber.wav"));
         InputStream inputStream = getApplicationContext().getResources().openRawResource(sound);
         int lengthOfAudioClip = inputStream.read(fileData, 0, audioLength);
         at.write(fileData, 0, lengthOfAudioClip);

       }
      catch (FileNotFoundException e){
          Log.e("FileNotFoundException","!");
          e.printStackTrace();
      }
      catch (IOException e){
          Log.e("IOException","##");
          e.printStackTrace();
      }*/

        wl.acquire();
    }

    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //mediaPlayer = MediaPlayer.create(this, sound);
        afd = this.getResources().openRawResourceFd(sound);
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        } catch (IOException e) {
            e.printStackTrace();
        }

        mSensorManager = (SensorManager)this.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        int sensDelay = delay(acc);
        try {
            afd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mSensorManager.registerListener(this, mAccelerometer, sensDelay);

        Log.e("SensorManagerService", "service started, delay is " + sensDelay);
        Log.e("SensorManagerService", "sensibility is " + sensibility);

        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        mediaPlayer.release();
        mSensorManager.unregisterListener(this);
/*        at.flush();
        at.release();*/
        wl.release();
        Log.e("SensorManagerService", "###onDestroy chiamato###");
        super.onDestroy();
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1)
    {   }

    @Override
    public void onSensorChanged(SensorEvent event) {

        synchronized (this) {

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            if ((x * x + y * y + z * z) > sensibility) {
                mediaPlayer.start();
                //    at.play();
              //  Log.e("SENSOR", "*****************************");
              //  Log.e("SENSOR", "x value= " + x + ", y value= " + y + ", z value= " + z);
              //  Log.e("SENSOR", "x*x+y*y+z*z value= " + (x * x + y * y + z * z));
            }

//     if(at.getPlayState() == AudioTrack.PLAYSTATE_STOPPED) {
//         Log.e("ffee","feregegr");
//         at.stop();
//         at.reloadStaticData();
//     }

        }
    }

    public int delay(int id){
        if(id==R.id.accNorm)
            return SensorManager.SENSOR_DELAY_NORMAL;
        else
            return SensorManager.SENSOR_DELAY_GAME;

    }
}
