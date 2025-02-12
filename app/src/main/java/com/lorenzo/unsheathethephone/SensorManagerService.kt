package com.lorenzo.unsheathethephone

import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.AssetFileDescriptor
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioTrack
import android.media.MediaPlayer
import android.os.IBinder
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.util.Log
import com.lorenzo.unsheathethephone.UTPMainActivity.Companion.retSound
import java.io.IOException

class SensorManagerService : Service(), SensorEventListener {
    private var mSensorManager: SensorManager? = null
    private var mAccelerometer: Sensor? = null
    private val at: AudioTrack? = null
    var mediaPlayer: MediaPlayer? = null
    var pm: PowerManager? = null
    var wl: WakeLock? = null
    var shared: SharedPreferences? = null
    var sound: Int = 0
    var acc: Int = 0
    var sensibility: Int = 0
    var fileData: ByteArray? = null
    var audioLength: Int = 0
    var afd: AssetFileDescriptor? = null

    override fun onCreate() {
        Log.e("SensorManagerService", "onCreate chiamato")
        pm = getApplicationContext().getSystemService(
            POWER_SERVICE
        ) as PowerManager
        wl = pm?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "com.lorenzo.unsheathethephone:pmlock")
        shared = getSharedPreferences(UTPMainActivity.PREFS, MODE_PRIVATE)
        sound = shared?.let { retSound(it.getInt("sound", 0)) } ?: 0
        acc = shared?.getInt("acc", R.id.accNorm) ?: R.id.accNorm
        sensibility = 950 - (shared?.getInt("sensibility", 150) ?: 150)

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
        wl?.acquire(1000)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //mediaPlayer = MediaPlayer.create(this, sound);
        afd = this.getResources().openRawResourceFd(sound)
        mediaPlayer = MediaPlayer()
        try {
            afd?.let {
                mediaPlayer?.setDataSource(
                    it.getFileDescriptor(),
                    it.getStartOffset(),
                    it.getLength()
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        mSensorManager = this.getSystemService(SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val sensDelay = delay(acc)
        try {
            afd?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            mediaPlayer?.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        mSensorManager?.registerListener(this, mAccelerometer, sensDelay)

        Log.e("SensorManagerService", "service started, delay is " + sensDelay)
        Log.e("SensorManagerService", "sensibility is " + sensibility)

        return START_STICKY
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mSensorManager?.unregisterListener(this)
        /*        at.flush();
        at.release();*/
        wl?.release()
        Log.e("SensorManagerService", "###onDestroy chiamato###")
        super.onDestroy()
    }

    override fun onAccuracyChanged(arg0: Sensor?, arg1: Int) {}

    override fun onSensorChanged(event: SensorEvent) {
        synchronized(this) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            if ((x * x + y * y + z * z) > sensibility) {
                mediaPlayer?.start()
                //    at.play();
                //  Log.e("SENSOR", "*****************************");
                //  Log.e("SENSOR", "x value= " + x + ", y value= " + y + ", z value= " + z);
                //  Log.e("SENSOR", "x*x+y*y+z*z value= " + (x * x + y * y + z * z));
            }
        }
    }

    fun delay(id: Int): Int {
        if (id == R.id.accNorm) return SensorManager.SENSOR_DELAY_NORMAL
        else return SensorManager.SENSOR_DELAY_GAME
    }
}
