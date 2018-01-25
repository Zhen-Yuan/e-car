package com.example.denis.ecar.StreckenView;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.example.denis.ecar.R;

public class ViewStrecken extends FragmentActivity implements PageFragment.PassData {
    ViewPager vp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_strecken);
        vp = (ViewPager) findViewById(R.id.Vpager);
        StreckenSwipeAdapter SwAd = new StreckenSwipeAdapter(getSupportFragmentManager());
        vp.setAdapter(SwAd);
    }


    @Override
    public void sendData(int sid, int min, int max) {
        EditFragment editFragment = (EditFragment) getSupportFragmentManager()
                .findFragmentById(R.id.editFragment);
        editFragment.displayReceivedData(sid, min, max);
    }

}
