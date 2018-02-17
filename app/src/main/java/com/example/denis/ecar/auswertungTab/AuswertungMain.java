package com.example.denis.ecar.auswertungTab;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.denis.ecar.auswertung.AuswertungBenzin;
import com.example.denis.ecar.auswertung.AuswertungElektro;
import com.example.denis.ecar.auswertung.Kalkulationen;
import com.example.denis.ecar.datenbank.EcarCar;
import com.example.denis.ecar.datenbank.EcarData;
import com.example.denis.ecar.datenbank.EcarDataSource;
import com.example.denis.ecar.datenbank.EcarSession;
import com.github.mikephil.charting.data.BarEntry;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.example.denis.ecar.R;

public class AuswertungMain extends AppCompatActivity {

    public static final String LOG_TAG = "AuswertungMain";
    private EcarDataSource dataSource;
    private Spinner ddmenu;
    private Spinner ddmenu2;
    private TextView tv_disp;
    private List<EcarSession> ecarSessionList;
    private EcarSession session_strecke;
    private List<EcarData> ecarLatList;
    private List<EcarData> ecarLongList;
    private List<EcarCar> ecarCars;
    private EcarCar ecarCar;
    private double ddist = 0;
    private String strSpeed;
    private Context con = this;
    private Tab_Uebersicht tabUebersicht;
    private Tab_Map tabMap;
    private Chart_Verbrauch tabVerbrauch;
    private Chart_Woche tabWoche;
    private Chart_Woche_Kosten tabKosten;
    private double str_batterie, str_stromverbrauch, str_stromkosten, str_strecke, str_co2;
    AuswertungElektro auswElektro;
    AuswertungBenzin auswBenzin;
    public Kalkulationen kalk;
    private int fID;
    private int lastSelected = -1;
    DecimalFormat f = new DecimalFormat("#0.00");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Layout
        setContentView(R.layout.activity_auswertungmain);
        // Ermöglicht "swipes" zwischen den Tabs
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(5);
        // Erstellt Adapter, um den Aktuellen Tab zu ermitteln
        TabFragmentPagerAdapter adapter = new TabFragmentPagerAdapter(this, getSupportFragmentManager());
        // Verbindung zwischen Viewpager und Adapter
        viewPager.setAdapter(adapter);
        tabUebersicht = (Tab_Uebersicht) adapter.getItem(0);
        tabMap = (Tab_Map) adapter.getItem(1);
        tabVerbrauch = (Chart_Verbrauch) adapter.getItem(2);
        tabWoche = (Chart_Woche) adapter.getItem(3);
        tabKosten = (Chart_Woche_Kosten) adapter.getItem(4);

        auswElektro = new AuswertungElektro();
        auswBenzin = new AuswertungBenzin(0,0,0);


        // Tablayout zum Viewpager hinzufügen
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        dataSource = new EcarDataSource(this);
        dataSource.open();
        ecarSessionList = dataSource.getAllEcarSession();
        dataSource.close();


        if(ecarSessionList!=null) {
            ddmenu2 = (Spinner) findViewById(R.id.spinner_auto);
            ddmenu = (Spinner) findViewById(R.id.spinner_strecke);
            initDB();
            //Spinner für E-Autos, suche nach dem ausgewählten Element
            ecarCar = ecarCars.get(0);
            ddmenu2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                    dataSource.open();
                    for (int i = 0; i < ecarCars.size(); i++) {
                        if (ddmenu2.getSelectedItem().toString().equals(ecarCars.get(i).getName())) {
                            ecarCar = ecarCars.get(i);
                            fID = ecarCar.getFid();

                            chartVerbrauch();
                            chartWoche();
                            chartKosten();
                            tabUebersicht();
                            break;
                        }
                    }
                    dataSource.close();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });


            //Spinner für Strecken, suche nach dem ausgewählten Element
            ddmenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                    dataSource.open();
                    ecarSessionList = dataSource.getAllEcarSession();
                    String carname = ddmenu.getSelectedItem().toString();

                    for (int u = 0; u < ecarSessionList.size(); u++) {
                        if (ecarSessionList.get(u).getName().equals(carname)) {
                            Log.d("SesID", "" + ecarSessionList.get(u).getSesid());
                            session_strecke = ecarSessionList.get(u);

                                ecarLatList = dataSource.getSpecificEcarData((ecarSessionList.get(u).getSesid()), 1);
                                ecarLongList = dataSource.getSpecificEcarData((ecarSessionList.get(u).getSesid()), 2);

                        }
                    }
                    dataSource.close();
                    try {
                    if (ecarLatList.size() > 0) {
                        tabMap.setStrecke(ecarLatList,ecarLongList);
                        tabMap.setColor(true);
                        tabMap.initMarker();
                        chartVerbrauch();
                        chartWoche();
                        chartKosten();
                        str_strecke = tabMap.ddist/1000;
                        tabUebersicht();
                        lastSelected = pos;
                    }
                    }catch(Exception e){
                        Log.d("Fehler", "keine Streckendaten!");
                        Toast toast = Toast.makeText(getApplicationContext(),"Fehler, keine Streckendaten!", Toast.LENGTH_SHORT);
                        toast.show();
                        if(lastSelected != -1) {
                            ddmenu.setSelection(lastSelected);
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
            initDB();
        }

    }


    //Initialisierung der Datenbank
    //Füllen der Spinner
    public void initDB() {
        Log.d(LOG_TAG, "Das Datenquellen-Objekt wird angelegt.");
        dataSource.open();
        ecarSessionList = dataSource.getAllEcarSession();
        ecarLatList = dataSource.getSpecificEcarData(1, 1);
        ecarLongList = dataSource.getSpecificEcarData(1, 2);
        ecarCars = dataSource.getAllCar();

        dataSource.close();

        if(ecarSessionList!=null) {

            String[] items = new String[ecarSessionList.size()];

            for (int i = 0; i < ecarSessionList.size(); i++) {
                items[i] = ecarSessionList.get(i).getName();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
            ddmenu.setAdapter(adapter);


            String[] itemsCar = new String[ecarCars.size()];
            for (int i = 0; i < ecarCars.size(); i++) {
                itemsCar[i] = ecarCars.get(i).getName();
            }
            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, itemsCar);
            ddmenu2.setAdapter(adapter2);

            tabMap.setStrecke(ecarLatList,ecarLongList);

        }else{
            //tv_disp.setText("Bitte legen Sie erst\neine Strecke an...");

            ddmenu.setAdapter(null);
            ddmenu2.setAdapter(null);

        }
    }

    //Methode um den Übersichtstab zu füllen
    private void tabUebersicht(){
        if(ecarCar.getFid() == 3) {
            tabUebersicht.txt1.setText("Batterieverbrauch in %:");
            tabUebersicht.txt2.setText("Stromverbrauch in kwh:");
            tabUebersicht.txt3.setText("Stromkosten in €:");
            tabUebersicht.txt4.setText("zurückgelegte Strecke:");
            tabUebersicht.txt5.setText("CO2-Einsparung:");
            tabUebersicht.txt_stromverbrauch.setText(f.format(str_stromverbrauch) + " kwh");
        }
        else{
            tabUebersicht.txt1.setText("Spritverbrauch in %:");
            tabUebersicht.txt2.setText("Spritverbrauch in l:");
            tabUebersicht.txt3.setText("Spritkosten in €:");
            tabUebersicht.txt4.setText("zurückgelegte Strecke:");
            tabUebersicht.txt5.setText("CO2-Einsparung:");
            tabUebersicht.txt_stromverbrauch.setText(f.format(str_stromverbrauch) + " l");
        }
        tabUebersicht.txt_batterie.setText(f.format(str_batterie) + " %");

        tabUebersicht.txt_stromkosten.setText(f.format(str_stromkosten) + " €");
        tabUebersicht.txt_strecke.setText(f.format(str_strecke) + " km");
        tabUebersicht.txt_co2einsparung.setText(f.format(str_co2) + " kg");

    }


    //Methode um den Verbrauchs Chart zu errechnen
    //Gefahrene Zeit in Sekunden und Batteriestatus in %
    private void chartVerbrauch(){
        ArrayList y = new ArrayList();


        double dist = 0;
        double time = 0;
        double bat = 100;
        double verbr = 0;
        double reich = ecarCar.getRange()*1000;
        for (int i = 1; i<ecarLatList.size(); i++){
            dist = dist + kalk.calcDist(ecarLatList.get(i-1).getData(),ecarLongList.get(i-1).getData(),ecarLatList.get(i).getData(),ecarLongList.get(i).getData());
            time = time + ecarLatList.get(i).getTime()-ecarLatList.get(i-1).getTime();
            bat = 100 - (dist/reich*100);
            verbr = (dist/reich*100);
            y.add(new BarEntry((float)time/60,(float)bat));
        }
        if(ecarCar.getFid() == 3) {
            auswElektro.setdStrecke(dist / 1000);
            auswElektro.setdStrompreis(0.2916);
            str_stromkosten = auswElektro.getKosten(ecarCar.getConsumption());
            str_co2 = (dist / 1000) * 0.12;       //ToDo gescheite Berechnung
        }
        else if (ecarCar.getFid() == 1){
            auswBenzin.setdStrecke(dist / 1000);
            auswBenzin.setdSpritpreis(1.30);
            str_stromkosten = auswBenzin.getKosten(ecarCar.getConsumption(), 1.3);
            str_co2 = (dist / 1000) * -0.14;

        }
        else if (ecarCar.getFid() == 2){
            auswBenzin.setdStrecke(dist / 1000);
            auswBenzin.setdSpritpreis(1.10);
            str_stromkosten = auswBenzin.getKosten(ecarCar.getConsumption(), 1.1);
            str_co2 = (dist / 1000) * -0.18;
        }
        str_stromverbrauch = ((dist / 1000) / 100) * ecarCar.getConsumption();
        str_batterie = verbr;


        tabVerbrauch.chartBeispiel(y, ecarCar);
    }


    //Berechnet und erstellt die Auswertung zu den gefahrenen Strecken der letzten 7-Tage
    private void chartWoche(){
        ArrayList<Double> y = new ArrayList<Double>();
        y.add(0.0);
        y.add(0.0);
        y.add(0.0);
        y.add(0.0);
        y.add(0.0);
        y.add(0.0);
        y.add(0.0);
        ArrayList<Integer> colour = new ArrayList<Integer>();
        colour.add(Color.GREEN);
        colour.add(Color.GREEN);
        colour.add(Color.GREEN);
        colour.add(Color.GREEN);
        colour.add(Color.GREEN);
        colour.add(Color.GREEN);
        colour.add(Color.GREEN);
        double dist = 0;
        List<EcarData> allData;
        int aktSess = 0;
        dataSource.open();
        allData = dataSource.getAllEcarData();  //lat->long->lat->long....
        dataSource.close();
        Calendar calnow = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < allData.size() - 2; i = i + 2) {
            cal.setTimeInMillis((long) allData.get(i).getTime() * 1000);
            //calendar.add(Calendar.DAY_OF_YEAR, -90);
            dist = 0;
            if (calnow.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && calnow.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && calnow.get(Calendar.DATE) == cal.get(Calendar.DATE)) {
                if (allData.get(i).getSesid() == allData.get(i + 2).getSesid()) {
                    while(i < allData.size()-2 && allData.get(i).getSesid()==allData.get(i + 2).getSesid()){
                        dist = dist + kalk.calcDist(allData.get(i).getData(), allData.get(i + 1).getData(), allData.get(i + 2).getData(), allData.get(i + 3).getData());
                        i = i + 2;

                    }
                    y.set(6, y.get(6) + dist/1000);
                    if(dist/1000>ecarCar.getRange()){colour.set(6,Color.RED);}   //Balken-Farbe einstellen
                    else if(y.get(6)>ecarCar.getRange()){if(colour.get(6) != Color.RED){colour.set(6,Color.YELLOW);}}
                }
            }
            dist = 0;
            calnow.add(Calendar.DATE, -1);
            if (calnow.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && calnow.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && calnow.get(Calendar.DATE) == cal.get(Calendar.DATE)) {
                if (allData.get(i).getSesid() == allData.get(i + 2).getSesid()) {
                    while(i < allData.size()-2 && allData.get(i).getSesid()==allData.get(i + 2).getSesid()){
                        dist = dist+kalk.calcDist(allData.get(i).getData(), allData.get(i + 1).getData(), allData.get(i + 2).getData(), allData.get(i + 3).getData());
                        i = i + 2;
                    }
                    y.set(5, y.get(5) + dist/1000);
                    if(dist/1000>ecarCar.getRange()){colour.set(5,Color.RED);}   //Balken-Farbe einstellen
                    else if(y.get(5)>ecarCar.getRange()){if(colour.get(5) != Color.RED){colour.set(5,Color.YELLOW);}}
                }
            }
            dist = 0;
            calnow.add(Calendar.DATE, -1);
            if (calnow.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && calnow.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && calnow.get(Calendar.DATE) == cal.get(Calendar.DATE)) {
                if (allData.get(i).getSesid() == allData.get(i + 2).getSesid()) {
                    while(i < allData.size()-2 && allData.get(i).getSesid()==allData.get(i + 2).getSesid()){
                        dist = dist+kalk.calcDist(allData.get(i).getData(), allData.get(i + 1).getData(), allData.get(i + 2).getData(), allData.get(i + 3).getData());
                        i = i + 2;
                    }
                    y.set(4, y.get(4) + dist/1000);
                    if(dist/1000>ecarCar.getRange()){colour.set(4,Color.RED);}   //Balken-Farbe einstellen
                    else if(y.get(4)>ecarCar.getRange()){if(colour.get(4) != Color.RED){colour.set(4,Color.YELLOW);}}
                }
            }
            dist = 0;
            calnow.add(Calendar.DATE, -1);
            if (calnow.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && calnow.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && calnow.get(Calendar.DATE) == cal.get(Calendar.DATE)) {
                if (allData.get(i).getSesid() == allData.get(i + 2).getSesid()) {
                    while(i < allData.size()-2 && allData.get(i).getSesid()==allData.get(i + 2).getSesid()){
                        dist = dist+kalk.calcDist(allData.get(i).getData(), allData.get(i + 1).getData(), allData.get(i + 2).getData(), allData.get(i + 3).getData());
                        i = i + 2;
                    }
                    y.set(3, y.get(3) + dist/1000);
                    if(dist/1000>ecarCar.getRange()){colour.set(3,Color.RED);}   //Balken-Farbe einstellen
                    else if(y.get(3)>ecarCar.getRange()){if(colour.get(3) != Color.RED){colour.set(3,Color.YELLOW);}}
                }
            }
            dist = 0;
            calnow.add(Calendar.DATE, -1);
            if (calnow.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && calnow.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && calnow.get(Calendar.DATE) == cal.get(Calendar.DATE)) {
                if (allData.get(i).getSesid() == allData.get(i + 2).getSesid()) {
                    while(i < allData.size()-2 && allData.get(i).getSesid()==allData.get(i + 2).getSesid()){
                        dist = dist+kalk.calcDist(allData.get(i).getData(), allData.get(i + 1).getData(), allData.get(i + 2).getData(), allData.get(i + 3).getData());
                        i = i + 2;
                    }
                    y.set(2, y.get(2) + dist/1000);
                    if(dist/1000>ecarCar.getRange()){colour.set(2,Color.RED);}   //Balken-Farbe einstellen
                    else if(y.get(2)>ecarCar.getRange()){if(colour.get(2) != Color.RED){colour.set(2,Color.YELLOW);}}
                }//Log.d("dist",dist/1000+"");
            }
            dist = 0;
            calnow.add(Calendar.DATE, -1);
            if (calnow.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && calnow.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && calnow.get(Calendar.DATE) == cal.get(Calendar.DATE)) {
                if (allData.get(i).getSesid() == allData.get(i + 2).getSesid()) {
                    while(i < allData.size()-2 && allData.get(i).getSesid()==allData.get(i + 2).getSesid()){
                        dist = dist+kalk.calcDist(allData.get(i).getData(), allData.get(i + 1).getData(), allData.get(i + 2).getData(), allData.get(i + 3).getData());
                        i = i + 2;
                    }
                    y.set(1, y.get(1) + dist/1000);
                    if(dist/1000>ecarCar.getRange()){colour.set(1,Color.RED);}   //Balken-Farbe einstellen
                    else if(y.get(1)>ecarCar.getRange()){if(colour.get(1) != Color.RED){colour.set(1,Color.YELLOW);}}
                }
            }
            dist = 0;
            calnow.add(Calendar.DATE, -1);
            if (calnow.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && calnow.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && calnow.get(Calendar.DATE) == cal.get(Calendar.DATE)) {
                if (allData.get(i).getSesid() == allData.get(i + 2).getSesid()) {
                    while(i < allData.size()-2 && allData.get(i).getSesid()==allData.get(i + 2).getSesid()){
                        dist = dist+kalk.calcDist(allData.get(i).getData(), allData.get(i + 1).getData(), allData.get(i + 2).getData(), allData.get(i + 3).getData());
                        i = i + 2;
                    }
                    y.set(0, y.get(0) + dist/1000);
                    if(dist>ecarCar.getRange()){colour.set(0,Color.RED);}   //Balken-Farbe einstellen
                    else if(y.get(0)>ecarCar.getRange()){if(colour.get(0) != Color.RED){colour.set(0,Color.YELLOW);}}
                }
            }
            calnow.add(Calendar.DATE, 6);
        }


        //Log.d("Calendar",calnow.get(Calendar.DATE)+"");
        tabWoche.chartBeispiel(y, ecarCar, colour);
    }


    //Berechnet und erstellt die Auswertung zu den Kosten der gefahrenen Strecken der letzten 7-Tage
    private void chartKosten(){
        ArrayList<Double> y = new ArrayList<Double>();
        y.add(0.0);
        y.add(0.0);
        y.add(0.0);
        y.add(0.0);
        y.add(0.0);
        y.add(0.0);
        y.add(0.0);
        double dist = 0;
        List<EcarData> allData;
        AuswertungElektro auswE = new AuswertungElektro();
        if(ecarCar.getFid()==3) {
            auswE.setdStrompreis(0.2916);
            auswE.setdStrecke(0);
        }else if(ecarCar.getFid()==2) {
            auswE.setdStrompreis(1.3);
            auswE.setdStrecke(0);
        }else{
            auswE.setdStrompreis(1.1);
            auswE.setdStrecke(0);
        }


        dataSource.open();
        allData = dataSource.getAllEcarData();  //lat->long->lat->long....
        dataSource.close();
        Calendar calnow = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < allData.size() - 2; i = i + 2) {
            cal.setTimeInMillis((long) allData.get(i).getTime() * 1000);
            //calendar.add(Calendar.DAY_OF_YEAR, -90);

            if (calnow.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && calnow.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && calnow.get(Calendar.DATE) == cal.get(Calendar.DATE)) {
                if (allData.get(i).getSesid() == allData.get(i + 2).getSesid()) {
                    dist = kalk.calcDist(allData.get(i).getData(), allData.get(i + 1).getData(), allData.get(i + 2).getData(), allData.get(i + 3).getData());
                    auswE.setdStrecke(dist);
                    y.set(6, y.get(6) + auswE.getKosten(ecarCar.getConsumption()/1000));
                }
            }
            calnow.add(Calendar.DATE, -1);
            if (calnow.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && calnow.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && calnow.get(Calendar.DATE) == cal.get(Calendar.DATE)) {
                if (allData.get(i).getSesid() == allData.get(i + 2).getSesid()) {
                    dist = kalk.calcDist(allData.get(i).getData(), allData.get(i + 1).getData(), allData.get(i + 2).getData(), allData.get(i + 3).getData());
                    auswE.setdStrecke(dist);
                    y.set(5, y.get(5) + auswE.getKosten(ecarCar.getConsumption()/1000));
                }
            }
            calnow.add(Calendar.DATE, -1);
            if (calnow.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && calnow.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && calnow.get(Calendar.DATE) == cal.get(Calendar.DATE)) {
                if (allData.get(i).getSesid() == allData.get(i + 2).getSesid()) {
                    dist = kalk.calcDist(allData.get(i).getData(), allData.get(i + 1).getData(), allData.get(i + 2).getData(), allData.get(i + 3).getData());
                    auswE.setdStrecke(dist);
                    y.set(4, y.get(4) + auswE.getKosten(ecarCar.getConsumption()/1000));
                }
            }
            calnow.add(Calendar.DATE, -1);
            if (calnow.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && calnow.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && calnow.get(Calendar.DATE) == cal.get(Calendar.DATE)) {
                if (allData.get(i).getSesid() == allData.get(i + 2).getSesid()) {
                    dist = kalk.calcDist(allData.get(i).getData(), allData.get(i + 1).getData(), allData.get(i + 2).getData(), allData.get(i + 3).getData());
                    auswE.setdStrecke(dist);
                    y.set(3, y.get(3) + auswE.getKosten(ecarCar.getConsumption()/1000));
                }
            }
            calnow.add(Calendar.DATE, -1);
            if (calnow.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && calnow.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && calnow.get(Calendar.DATE) == cal.get(Calendar.DATE)) {
                if (allData.get(i).getSesid() == allData.get(i + 2).getSesid()) {
                    dist = kalk.calcDist(allData.get(i).getData(), allData.get(i + 1).getData(), allData.get(i + 2).getData(), allData.get(i + 3).getData());
                    auswE.setdStrecke(dist);
                    y.set(2, y.get(2) + auswE.getKosten(ecarCar.getConsumption()/1000));
                }//Log.d("dist",dist/1000+"");
            }
            calnow.add(Calendar.DATE, -1);
            if (calnow.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && calnow.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && calnow.get(Calendar.DATE) == cal.get(Calendar.DATE)) {
                if (allData.get(i).getSesid() == allData.get(i + 2).getSesid()) {
                    dist = kalk.calcDist(allData.get(i).getData(), allData.get(i + 1).getData(), allData.get(i + 2).getData(), allData.get(i + 3).getData());
                    auswE.setdStrecke(dist);
                    y.set(1, y.get(1) + auswE.getKosten(ecarCar.getConsumption()/1000));
                }
            }
            calnow.add(Calendar.DATE, -1);
            if (calnow.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && calnow.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && calnow.get(Calendar.DATE) == cal.get(Calendar.DATE)) {
                if (allData.get(i).getSesid() == allData.get(i + 2).getSesid()) {
                    dist = kalk.calcDist(allData.get(i).getData(), allData.get(i + 1).getData(), allData.get(i + 2).getData(), allData.get(i + 3).getData());
                    auswE.setdStrecke(dist);
                    y.set(0, y.get(0) + auswE.getKosten(ecarCar.getConsumption()/1000));
                }
            }
            calnow.add(Calendar.DATE, 6);
        }


        //Log.d("Calendar",calnow.get(Calendar.DATE)+"");
        tabKosten.chartBeispiel(y, ecarCar);
    }





}