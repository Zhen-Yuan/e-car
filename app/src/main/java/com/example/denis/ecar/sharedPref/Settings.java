package com.example.denis.ecar.sharedPref;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.denis.ecar.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by Norbert on 01.10.2017.
 * Modified by Shinmei on 17.10.2017.
 */


public class Settings extends AppCompatActivity {

    public static final String TAG = "Settings";

    private SeekBar seekBar;
    private TextView tv3;
    SharedPreferences sp;
    SharedPreferences.Editor spe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sp = getSharedPreferences("bla", Context.MODE_PRIVATE);
        spe = sp.edit();

        init();//Aufruf der Initalisierungsmethode
    }


    public void init(){
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(60);
        seekBar.setProgress(sp.getInt("interval", 30));
        tv3 = (TextView) findViewById(R.id.textView3);
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

        // leitet weiter an die RemoveUserActivity
        findViewById(R.id.tvDeleteAcc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.this, RemoveUserActivity.class));
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // uebergibt das Ergebnis des onActivityResult an das LinkAccountsFragment weiter
        LinkAccountsFragment linkFragment = (LinkAccountsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentLinkAcc);
        linkFragment.onActivityResult(requestCode, resultCode, data);

        // uebergibt das Ergebnis des onActivityResult an das ProfileFragment weiter
        ProfileFragment profileFragment = (ProfileFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentProfile);
        profileFragment.onActivityResult(requestCode, resultCode, data);
    }
}