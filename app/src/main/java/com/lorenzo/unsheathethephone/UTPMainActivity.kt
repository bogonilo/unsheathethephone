package com.lorenzo.unsheathethephone

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class UTPMainActivity : AppCompatActivity() {
    var switch_onoff: Switch? = null
    var enable: Boolean = false
    var accuracy: Int = 0
    var sens_value: Int = 0
    var sound_choose: TextView? = null
    var acc: RadioGroup? = null
    var sens: SeekBar? = null
    var soundSelector: AlertDialog.Builder? = null
    var sounds_name: Array<String?> = emptyArray()
    var sharedPreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    var serviceIntent: Intent? = null
    var adView: AdView? = null
    var interstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_utp)

        switch_onoff = findViewById<Switch>(R.id.checkbox)
        sound_choose = findViewById<TextView>(R.id.sound)
        acc = findViewById<RadioGroup>(R.id.acc_radio)
        sens = findViewById<SeekBar>(R.id.sensibility)
        sounds_name = getResources().getStringArray(R.array.sounds)
        adView = findViewById<AdView>(R.id.ad_view)
        serviceIntent = Intent(this, UTPService::class.java)

        sharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE)
        editor = sharedPreferences!!.edit()
        dialogRingerMode()
        enable = sharedPreferences!!.getBoolean("checkbox_state", false)
        sens_value = sharedPreferences!!.getInt("sensibility", 150)

        // ACCURACY
        accuracy = sharedPreferences!!.getInt("acc", R.id.accNorm)
        if (accuracy == R.id.accNorm) acc!!.check(R.id.accNorm)
        else acc!!.check(R.id.accHigh)

        // ADS
        val adRequest = AdRequest.Builder().build()
        adView?.loadAd(adRequest)
        loadInterstitialAd()

        // SENSIBILITY
        sens!!.setMax(300)
        sens!!.setProgress(sens_value)
        sens!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, i: Int, b: Boolean) {
                if (b) {
                    editor!!.putInt("sensibility", i)
                }
                editor!!.apply()
                sens_value = sharedPreferences!!.getInt("sensibility", 150)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (sharedPreferences!!.getBoolean("checkbox_state", false)) {
                    stopService(serviceIntent)
                    startService(serviceIntent)
                }
            }
        })

        // ACCURACY, RADIOGROUP
        acc!!.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(radioGroup: RadioGroup?, i: Int) {
                editor!!.putInt("acc", i)
                editor!!.apply()
                if (sharedPreferences!!.getBoolean("checkbox_state", false)) {
                    stopService(serviceIntent)
                    startService(serviceIntent)
                }
            }
        })

        // SOUNDS
        sound_choose!!.setText(sounds_name[sharedPreferences!!.getInt("sound", 0)])
        sound_choose!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                soundSelector!!.show()
            }
        })

        // SWITCH ON/OFF
        switch_onoff!!.setChecked(enable)
        switch_onoff!!.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(compoundButton: CompoundButton?, b: Boolean) {
                if (b) {
                    editor!!.putBoolean("checkbox_state", b)
                    startService(serviceIntent)
                } else {
                    editor!!.putBoolean("checkbox_state", b)
                    stopService(serviceIntent)
                    Log.e("UTPMain", "Service should be stopped")
                }
                editor!!.apply()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()

        val intent = Intent(this, InfoActivity::class.java)
        if (id == R.id.action_settings) {
            startActivity(intent)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        if (interstitialAd != null) {
            interstitialAd!!.show(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this, "ca-app-pub-7518908736641187/3484920351", adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                }
            })
    }

    private fun dialogRingerMode() {
        soundSelector = AlertDialog.Builder(this)
        soundSelector!!.setTitle(this.getResources().getString(R.string.sound_dialog_title))
        soundSelector!!.setItems(R.array.sounds, object : DialogInterface.OnClickListener {
            override fun onClick(dialogInterface: DialogInterface?, i: Int) {
                sound_choose!!.setText(sounds_name[i])
                editor!!.putInt("sound", i)
                editor!!.apply()
                if (sharedPreferences!!.getBoolean("checkbox_state", false)) {
                    stopService(serviceIntent)
                    startService(serviceIntent)
                }
            }
        })
        soundSelector!!.create()
    }

    companion object {
        const val PREFS: String = "preference"

        @JvmStatic
        fun retSound(i: Int): Int {
            when (i) {
                0 -> return R.raw.sword
                1 -> return R.raw.fart
                2 -> return R.raw.lightsaber
                3 -> return R.raw.whip
            }
            return 0
        }
    }
}