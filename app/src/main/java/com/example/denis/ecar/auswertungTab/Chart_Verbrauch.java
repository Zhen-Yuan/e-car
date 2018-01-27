package com.example.denis.ecar.auswertungTab;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;

import com.example.denis.ecar.R;
import com.example.denis.ecar.datenbank.EcarCar;
import com.example.denis.ecar.fragmentAnimation.MoveAnimation;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
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
    private EcarCar car;


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
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return MoveAnimation.create(MoveAnimation.UP,enter,300);
    }
    private void init()
    {
        tv_titelElektro = (TextView) v.findViewById(R.id.tv_ecarTitel);
        tv_beschreibungElektro = (TextView) v.findViewById(R.id.tv_BeschreibungEcar);
        chart = (LineChart) v.findViewById(R.id.chart);
        barWidth = 0.3f;
        barSpace = 0f;
        groupSpace = 0.4f;
        tv_titelElektro.setText("\nVerbrauch");




    }

    public void chartBeispiel(ArrayList yVals, EcarCar car) {
        this.car=car;
        double cap = (car.getRange()/100)*car.getConsumption();
        tv_beschreibungElektro.setText("Gefahrene Zeit(s) und Batteriestatus(%)\n" +
                car.getName()+"\nVerbrauch: "+car.getConsumption()+"kWh/100km (ADAC)\nAkkukapazität: "+String.format("%.0f",cap)+"kWh\nReichweite ~ "+car.getRange()+"km\n");

        LineDataSet set1 = new LineDataSet(yVals, "Ladezustand");

        set1.setColor(Color.RED);
        set1.setDrawCircles(false);
        set1.setDrawValues(false);
        LineData data = new LineData(set1);
        chart.setData(data);
        chart.getDescription().setEnabled(true);
        chart.getDescription().setPosition(100,475);
        chart.getDescription().setText("Batterie [%]");
        //chart.setBackgroundColor(Color.WHITE);
        chart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        chart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        //chart.setVisibleYRange(100,0, YAxis.AxisDependency.LEFT);
        chart.getAxisLeft().setLabelCount(5);
        chart.getAxisLeft().setAxisMaxValue(100);
        chart.getAxisLeft().setAxisMinValue(0);
        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.invalidate();





    }
}
