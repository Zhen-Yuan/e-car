package com.example.denis.ecar.StreckenView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Raja on 17.12.2017.
 */

public class StreckenSwipeAdapter extends FragmentStatePagerAdapter  {
    public StreckenSwipeAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = new PageFragment();
        Bundle bundle = new Bundle();
        int day = 0;
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd 00:00:00 zzz");
            Date date = new Date();
            date = dateFormat.parse(dateFormat.format(date));
            long epoch = date.getTime()/1000;
            day = (int)epoch;
        }catch (Exception ex){}

        bundle.putInt("day", day-86400*position);
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public int getCount() {
        return 30;
    }
}
