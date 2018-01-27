package com.example.denis.ecar.auswertungTab;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Denis on 19.12.2017.
 */

public class TabFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private Tab_Map tabMap;
    private Chart_Verbrauch chart_verbrauch;
    private Chart_Woche chart_woche;
    private Chart_Woche_Kosten chart_woche_kosten;
    private Tab_Uebersicht tabUebersicht;

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public TabFragmentPagerAdapter(Context context, FragmentManager fm) {

        super(fm);
        mContext = context;

        tabMap = new Tab_Map();
        chart_verbrauch = new Chart_Verbrauch();
        chart_woche = new Chart_Woche();
        chart_woche_kosten = new Chart_Woche_Kosten();
        tabUebersicht = new Tab_Uebersicht();

        mFragmentList.add(tabUebersicht);
        mFragmentList.add(tabMap);
        mFragmentList.add(chart_verbrauch);
        mFragmentList.add(chart_woche);
        mFragmentList.add(chart_woche_kosten);

        mFragmentTitleList.add("Übersicht");
        mFragmentTitleList.add("Karte");
        mFragmentTitleList.add("Verbrauch");
        mFragmentTitleList.add("Woche");
        mFragmentTitleList.add("Kosten");
    }



    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}


