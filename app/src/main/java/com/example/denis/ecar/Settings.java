package com.example.denis.ecar;

/**
 * Created by Norbert on 01.10.2017.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;


public class Settings extends AppCompatActivity {
    public static final String LOG_TAG = "settings";
    private SeekBar seekBar;
    private TextView tv3;
    SharedPreferences sp;
    SharedPreferences.Editor spe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        sp = getSharedPreferences("bla", Context.MODE_PRIVATE);
        spe = sp.edit();
        init();//Aufruf der Initalisierungsmethode
    }


    public void init(){
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        tv3 = (TextView) findViewById(R.id.textView3);
        seekBar.setMax(60);
        seekBar.setProgress(sp.getInt("interval", 30));
        tv3.setText(String.valueOf(seekBar.getProgress()));
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv3.setText(String.valueOf(progress));
                spe.putInt("interval", progress);
                spe.commit();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


    }




}
