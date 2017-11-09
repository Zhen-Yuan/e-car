package com.example.denis.ecar.liveAuswertung;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.denis.ecar.R;

/**
 * Created by Denis on 09.11.2017.
 */

public class LiveAuswertung extends Activity
{

    TextView tvLiveAuswertung;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_auswertung);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public void setContentView(View view)
    {
        super.setContentView(view);
    }

    public LiveAuswertung()
    {
        //Initialisierung der Platzhalter
        //tvLiveAuswertung = (TextView) findViewById(R.id.tvLiveauswertung);
        //Button bttnPlatzhalter0 = (Button) findViewById(R.id.PLATZHALTER1);
        //Button bttnPlatzhalter1 = (Button) findViewById(R.id.PLATZHALTER2);
    }
}
