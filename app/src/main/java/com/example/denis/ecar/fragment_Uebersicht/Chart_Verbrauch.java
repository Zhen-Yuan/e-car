package com.example.denis.ecar.fragment_Uebersicht;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.denis.ecar.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

/**
 * Created by Denis on 16.10.2017.
 */

public class Chart_Verbrauch extends Fragment
{
    private View v;
    float barWidth;
    float barSpace;
    float groupSpace;


    private TextView tv_titelElektro, tv_beschreibungElektro;
    LineChart chart;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.chart_verbrauch,container,false);
        init();
        //TODO: Infoausgabe
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    private void init()
    {
        tv_titelElektro = (TextView) v.findViewById(R.id.tv_ecarTitel);
        tv_beschreibungElektro = (TextView) v.findViewById(R.id.tv_BeschreibungEcar);
        chart = (LineChart) v.findViewById(R.id.chart);
        barWidth = 0.3f;
        barSpace = 0f;
        groupSpace = 0.4f;
        tv_titelElektro.setText("Verbrauch");
        tv_beschreibungElektro.setText("Gefahrene Zeit(s) und Batteriestatus(%)\nTesla Model S\nVerbrauch: 20.5 kWh/100km (ADAC)\nAkkukapazität: 85kWh\nReichweite ~ 415km");



    }

    public void chartBeispiel(ArrayList yVals) {

        LineDataSet set1 = new LineDataSet(yVals, "Ladezustand");
        set1.setColor(Color.RED);
        LineData data = new LineData(set1);
        chart.setData(data);
        chart.getDescription().setEnabled(true);
        chart.getDescription().setPosition(100,20);
        chart.getDescription().setText("Batterie [%]");
        chart.invalidate();




    }
}
