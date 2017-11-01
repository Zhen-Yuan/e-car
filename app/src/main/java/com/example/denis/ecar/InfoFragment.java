package com.example.denis.ecar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.text.method.ScrollingMovementMethod;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;

import com.example.denis.ecar.fragmentAnimation.MoveAnimation;
import com.example.denis.ecar.fuellmethoden.DataGenerator;

import javax.xml.datatype.Duration;

/**
 * Created by denis on 29.07.2017.
 */

public class InfoFragment extends Fragment{
    private View v;
    private TextView tv_info;
    private DataGenerator dataGenerator;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        v = inflater.inflate(R.layout.fragment_info,container,false);
        init();
        //TODO: Infoausgabe
        tv_info.setText(dataGenerator.teslaModelSinfo());
        return v;
    }
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return MoveAnimation.create(MoveAnimation.UP,enter,300);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    private void init()
    {
        tv_info = (TextView)v.findViewById(R.id.tv_Info);
        tv_info.setMovementMethod(new ScrollingMovementMethod());//Macht tv_info scrollable
        dataGenerator = new DataGenerator();
    }

}
