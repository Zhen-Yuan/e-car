package com.example.denis.ecar.auswertungTab;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.denis.ecar.MapsEval;
import com.example.denis.ecar.fragment_Uebersicht.Chart_Woche;
import com.example.denis.ecar.fragment_Uebersicht.Chart_Woche_Kosten;

/**
 * Created by Denis on 19.12.2017.
 */

public class TabFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public TabFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    // Fragment für jedes Tab
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new Chart_Woche();
        } else if (position == 1){
            return new Chart_Woche_Kosten();
        } else if (position == 2){
            return new FragmentTab4();
        } else {
            return new FragmentTab4();
        }
    }

    // Anzahl der Tabs
    @Override
    public int getCount() {
        return 4;
    }

    // Titel für die jeweiligen Tabs
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return "Tab1";
            case 1:
                return "Tab2";
            case 2:
                return "Tab3";
            case 3:
                return "Tab4";
            default:
                return null;
        }
    }

}
