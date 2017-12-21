package com.lorenzo.unsheathethephone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class UTPMainActivity extends AppCompatActivity {

    Switch switch_onoff;
    static final String PREFS="preference";
    boolean enable;
    int accuracy, sens_value;
    TextView sound_choose;
    RadioGroup acc;
    SeekBar sens;
    AlertDialog.Builder soundSelector;
    String[] sounds_name;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Intent intent;
    AdView adView;
    InterstitialAd ad;
 /*   static final int sword_length = 368728;
    static final int sword_freq = 44100;
    static final int lightsaber_length = 53826;
    static final int lightsaber_freq = 22050;
    static final int fart_length = 313432;
    static final int fart_freq = 44100;
    static final int whip_length = 244312;
    static final int whip_freq = 24000;  */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_utp);

        switch_onoff=(Switch)findViewById(R.id.checkbox);
        sound_choose = (TextView)findViewById(R.id.sound);
        acc = (RadioGroup)findViewById(R.id.acc_radio);
        sens = (SeekBar)findViewById(R.id.sensibility);
        sounds_name = getResources().getStringArray(R.array.sounds);
        adView = new AdView(this);
        ad = new InterstitialAd(this);
        intent=new Intent(this, UTPService.class);

        sharedPreferences = getSharedPreferences(PREFS, Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        dialogRingerMode();
        enable = sharedPreferences.getBoolean("checkbox_state", false);
        sens_value = sharedPreferences.getInt("sensibility", 150);

        //ACCURACY
        accuracy = sharedPreferences.getInt("acc",R.id.accNorm);
        if(accuracy == R.id.accNorm)
            acc.check(R.id.accNorm);
        else
            acc.check(R.id.accHigh);

        //ADS
        ad.setAdUnitId("ca-app-pub-7518908736641187/3484920351");
        adView=(AdView)findViewById(R.id.ad_view);
        requestNewInterstitial();

        //SENSIBILITY
        sens.setMax(300);
        sens.setProgress(sens_value);
        sens.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b){
                    editor.putInt("sensibility", i);
                }
                editor.apply();
                sens_value = sharedPreferences.getInt("sensibility",150);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (sharedPreferences.getBoolean("checkbox_state", false)) {
                    stopService(intent);
                    startService(intent);
                }

            }
        });

        //ACCURACY, RADIOGROUP
        acc.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    editor.putInt("acc",i);
                    editor.apply();
                if (sharedPreferences.getBoolean("checkbox_state", false)) {
                    stopService(intent);
                    startService(intent);
                }
            }
        });

        //SOUNDS
        sound_choose.setText(sounds_name[sharedPreferences.getInt("sound", 0)]);
        sound_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundSelector.show();
            }
        });

        //SWITCH ON/OFF
        switch_onoff.setChecked(enable);
        switch_onoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    editor.putBoolean("checkbox_state", b);
                    startService(intent);
                } else {
                    editor.putBoolean("checkbox_state", b);
                    stopService(intent);
                    Log.e("UTPMain", "Service should be stopped");
                }
                editor.apply();
            }
        });

        adView.loadAd(new AdRequest.Builder()
                .addTestDevice("E7C829D490857E50EA38ACBB72B40CB0")
                .build());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent intent=new Intent(this,InfoActivity.class);
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (ad.isLoaded()) {
            ad.show();
        }

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("E7C829D490857E50EA38ACBB72B40CB0")
                .build();

        ad.loadAd(adRequest);
    }

    private void dialogRingerMode(){
        soundSelector=new AlertDialog.Builder(this);
        soundSelector.setTitle(this.getResources().getString(R.string.sound_dialog_title));
        soundSelector.setItems(R.array.sounds, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                    sound_choose.setText(sounds_name[i]);
                    editor.putInt("sound", i);
                    editor.apply();
                if (sharedPreferences.getBoolean("checkbox_state", false)) {
                    stopService(intent);
                    startService(intent);
                }
            }

        });
        soundSelector.create();
    }

    public static int retSound(int i){
        switch (i){
            case 0:
                return R.raw.sword;
            case 1:
                return R.raw.fart;
            case 2:
                return R.raw.lightsaber;
            case 3:
                return R.raw.whip;
        }
        return 0;
    }
    
}
