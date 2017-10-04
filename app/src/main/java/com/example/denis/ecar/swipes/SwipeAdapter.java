package com.example.denis.ecar.swipes;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.denis.ecar.R;

import java.util.ArrayList;

/**
 * Created by Denis on 03.10.2017.
 */
/*
 int images[] = {R.drawable.tesla3,R.drawable.tesla4}; //TODO: Bilder besorgen, Modellen zuordnen, aus der DB erhalten.
 Durch das obere Array (MainActivity) definierte Ressourcen werden angezeigt und sind "swipeable".
 */
public class SwipeAdapter extends PagerAdapter {

    private ArrayList<Integer> bilder;
    private LayoutInflater inflater;
    private Context context;

    public SwipeAdapter(Context context, ArrayList<Integer> bilder) {
        this.context = context;
        this.bilder=bilder;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return bilder.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View myImageLayout = inflater.inflate(R.layout.imageview, view, false);
        ImageView myImage = (ImageView) myImageLayout
                .findViewById(R.id.image);
        myImage.setImageResource(bilder.get(position));
        view.addView(myImageLayout, 0);
        return myImageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}