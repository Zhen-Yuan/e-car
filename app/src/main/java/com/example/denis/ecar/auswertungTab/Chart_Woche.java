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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Denis on 16.10.2017.
 */

public class Chart_Woche extends Fragment
{
    private View v;
    float barWidth;
    float barSpace;
    float groupSpace;
    private EcarCar car;


    private TextView tv_titelElektro, tv_beschreibungElektro;
    BarChart chart;

    public Chart_Woche(){

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.chart_woche,container,false);
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

        chart = (BarChart) v.findViewById(R.id.chart);
        barWidth = 0.3f;
        barSpace = 0f;
        groupSpace = 0.4f;
        tv_titelElektro.setText("\nWochen-Statistik");




    }

    public void chartBeispiel(ArrayList<Double> yVals, EcarCar car, ArrayList<Integer> colours) {
        this.car = car;
        double cap = (car.getRange()/100)*car.getConsumption();


        if(car.getFid()==3) {
            tv_beschreibungElektro.setText("Gefahrene Strecken der letzten 7-Tage\n" +
                    car.getName()+"\nVerbrauch: "+car.getConsumption()+" kWh/100km (ADAC)\nAkkukapazität: "+String.format("%.0f",cap)+" kWh\nReichweite ~ "+car.getRange()+" km");
        }else if(car.getFid()==1){
            tv_beschreibungElektro.setText("Gefahrene Strecken der letzten 7-Tage\n" +
                    car.getName()+"\nVerbrauch: "+car.getConsumption()+" L/100km\nTankvolumen: " + car.getPowerstore()+" Liter\nReichweite ~ "+car.getRange()+" km");
        }else {
            tv_beschreibungElektro.setText("Gefahrene Strecken der letzten 7-Tage\n" +
                    car.getName() + "\nVerbrauch: " + car.getConsumption() + " L/100km\nTankvolumen: " + car.getPowerstore()+ " Liter\nReichweite ~ " + car.getRange() + " km");
        }

        chart.setPinchZoom(false);
        chart.setScaleEnabled(false);
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);

        ArrayList xVals = new ArrayList();

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE");


        xVals.add("");
        cal.add(Calendar.DATE, -6);
        xVals.add(sdf.format(cal.getTime()));
        cal.add(Calendar.DATE, 1);
        xVals.add(sdf.format(cal.getTime()));
        cal.add(Calendar.DATE, 1);
        xVals.add(sdf.format(cal.getTime()));
        cal.add(Calendar.DATE, 1);
        xVals.add(sdf.format(cal.getTime()));
        cal.add(Calendar.DATE, 1);
        xVals.add(sdf.format(cal.getTime()));
        cal.add(Calendar.DATE, 1);
        xVals.add(sdf.format(cal.getTime()));
        cal.add(Calendar.DATE, 1);
        xVals.add(sdf.format(cal.getTime()));

        ArrayList yElektro = new ArrayList();
        for (int i = 1;i<=7;i++)
        {
            yElektro.add(new BarEntry(i, yVals.get(i-1).floatValue())); // https://www.strompreise.de/strompreis-kwh/
        }

        BarDataSet set2;
        set2 = new BarDataSet(yElektro, "");
        set2.setColor(Color.BLUE);
        set2.setColors(colours);
        BarData data = new BarData(set2);
        data.setValueFormatter(new MyValueFormatter());
        chart.setData(data);
        chart.getBarData().setBarWidth(barWidth);
        chart.getXAxis().setAxisMinimum(0);
        //chart.getXAxis().setAxisMaximum(0 + chart.getBarData().getGroupWidth(groupSpace, barSpace));
        chart.getData().setHighlightEnabled(false);
        //chart.setDescription(null);
        //chart.getDescription().setPosition(15,10);
        chart.getDescription().setText("");
        chart.invalidate();

        Legend l = chart.getLegend();
        l.setEnabled(false);
        /*l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(true);
        l.setYOffset(20f);
        l.setXOffset(0f);
        l.setYEntrySpace(0f);
        l.setTextSize(9f);*/

        //X-Achse
        XAxis xAxis = chart.getXAxis();
        //xAxis.setGranularity(0f);
        //xAxis.setGranularityEnabled(true);
        //xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMaximum(8);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xVals));

        //Y-Achse
        chart.getAxisRight().setEnabled(false);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(true);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f);




    }


    private class MyValueFormatter implements IValueFormatter {
        private DecimalFormat mFormat;
        public MyValueFormatter() {
            mFormat = new DecimalFormat("###,###,##0"); // use one decimal
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return mFormat.format(value) + "km";
        }
    }
}

