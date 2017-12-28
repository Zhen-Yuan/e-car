package com.example.denis.ecar.auswertungTab;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.denis.ecar.R;

public class AuswertungMain extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Layout
        setContentView(R.layout.activity_auswertungmain);

        // Ermöglicht "swipes" zwischen den Tabs
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        // Erstellt Adapter, um den Aktuellen Tab zu ermitteln
        TabFragmentPagerAdapter adapter = new TabFragmentPagerAdapter(this, getSupportFragmentManager());

        // Verbindung zwischen Viewpager und Adapter
        viewPager.setAdapter(adapter);

        // Tablayout zum Viewpager hinzufügen
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }
}