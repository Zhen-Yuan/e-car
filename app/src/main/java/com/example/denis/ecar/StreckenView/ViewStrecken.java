package com.example.denis.ecar.StreckenView;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.example.denis.ecar.R;

public class ViewStrecken extends FragmentActivity{
    ViewPager vp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_strecken);
        vp = (ViewPager) findViewById(R.id.Vpager);
        StreckenSwipeAdapter SwAd = new StreckenSwipeAdapter(getSupportFragmentManager());
        vp.setAdapter(SwAd);

    }


}
