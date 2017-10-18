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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
/**
 * Created by Denis on 16.10.2017.
 */

public class UebersichtFragment extends Fragment
{
    private View v;
    float barWidth;
    float barSpace;
    float groupSpace;

    private TextView tv_titelBenzin, tv_titelElektro, tv_beschreibungBenzin, tv_beschreibungElektro, tv_PrognoseAuswertung;
    BarChart chart;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_uebersicht,container,false);
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
        tv_titelBenzin = (TextView) v.findViewById(R.id.tv_benzinautoTitel);
        tv_titelElektro = (TextView) v.findViewById(R.id.tv_ecarTitel);
        tv_beschreibungBenzin = (TextView) v.findViewById(R.id.tv_BeschreibungBenziner);
        tv_beschreibungElektro = (TextView) v.findViewById(R.id.tv_BeschreibungEcar);
        chart = (BarChart) v.findViewById(R.id.chart);
        barWidth = 0.3f;
        barSpace = 0f;
        groupSpace = 0.4f;
        chartBeispiel();
        tv_titelElektro.setText("Tesla Model S");
        tv_beschreibungElektro.setText("Verbrauch 20.5 kWh/100km (ADAC)\nAkkukapazität: 85kWh\nReichweite ~ 415km");
        tv_titelBenzin.setText("BMW 750i xDrive");
        tv_beschreibungBenzin.setText("Verbrauch 11.9 l/100km (ADAC)\nTankkapazität: 78l\nReichweite ~ 655km");

    }

    public void setTv_titelBenzin(TextView tv_titelBenzin) {
        this.tv_titelBenzin = tv_titelBenzin;
    }

    public void setTv_titelElektro(TextView tv_titelElektro) {
        this.tv_titelElektro = tv_titelElektro;
    }

    public void setTv_beschreibungBenzin(TextView tv_beschreibungBenzin) {
        this.tv_beschreibungBenzin = tv_beschreibungBenzin;
    }

    public void setTv_beschreibungElektro(TextView tv_beschreibungElektro) {
        this.tv_beschreibungElektro = tv_beschreibungElektro;
    }

    public void setTv_PrognoseAuswertung(TextView tv_PrognoseAuswertung) {
        this.tv_PrognoseAuswertung = tv_PrognoseAuswertung;
    }

    private void chartBeispiel() {
        int groupCount = 6;

        chart.setPinchZoom(false);
        chart.setScaleEnabled(false);
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);

        ArrayList xVals = new ArrayList();

        xVals.add("Jan");
        xVals.add("Feb");
        xVals.add("Mar");
        xVals.add("Apr");
        xVals.add("May");
        xVals.add("Jun");

        ArrayList yBenzin = new ArrayList();
        ArrayList yElektro = new ArrayList();
        for (int i = 1;i<=6;i++)
        {
            yBenzin.add(new BarEntry(i, (float) (i*11.5*5*1.30)));
            yElektro.add(new BarEntry(i, (float) (i*20.9*5*0.2916))); // https://www.strompreise.de/strompreis-kwh/
        }

        BarDataSet set1, set2;
        set1 = new BarDataSet(yBenzin, "750i xDrive");
        set1.setColor(Color.RED);
        set2 = new BarDataSet(yElektro, "Model S");
        set2.setColor(Color.BLUE);
        BarData data = new BarData(set1, set2);
        data.setValueFormatter(new LargeValueFormatter());
        chart.setData(data);
        chart.getBarData().setBarWidth(barWidth);
        chart.getXAxis().setAxisMinimum(0);
        chart.getXAxis().setAxisMaximum(0 + chart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
        chart.groupBars(0, groupSpace, barSpace);
        chart.getData().setHighlightEnabled(false);
        chart.getDescription().setPosition(200,50);
        chart.getDescription().setText("Spritkosten für 500km/Monat");
        chart.invalidate();

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(true);
        l.setYOffset(20f);
        l.setXOffset(0f);
        l.setYEntrySpace(0f);
        l.setTextSize(9f);

        //X-Achse
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMaximum(6);
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
}
