package com.example.denis.ecar;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.denis.ecar.datenbank.EcarData;
import com.example.denis.ecar.datenbank.EcarDataSource;
import com.example.denis.ecar.datenbank.EcarSession;
import com.example.denis.ecar.fragment_Uebersicht.Chart_Verbrauch;
import com.example.denis.ecar.fragment_Uebersicht.Chart_Woche;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MapsEval extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public static final String LOG_TAG = "lel";
    private EcarDataSource dataSource;
    private Spinner ddmenu;
    private TextView tv_disp;
    private EcarData cardatalat;
    private EcarData cardatalong;
    private List<EcarSession> ecarSessionList;
    private List<EcarData> ecarLatList;
    private List<EcarData> ecarLongList;
    private int color;
    private float color2;
    private double ddist = 0;
    private String strSpeed;
    private Button bttn_ausw;
    SupportMapFragment mapFragment;
    private Chart_Verbrauch auswertVerbrauch;
    private Chart_Woche auswertWoche;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapseval);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //auswertungFragment();
        auswertVerbrauch = (Chart_Verbrauch) getSupportFragmentManager().findFragmentById(R.id.auswertung);
        auswertVerbrauch.getView().setVisibility(View.GONE);
        auswertWoche = (Chart_Woche) getSupportFragmentManager().findFragmentById(R.id.auswertungWoche);
        auswertWoche.getView().setVisibility(View.GONE);

        ddmenu = (Spinner) findViewById(R.id.ddmenuspinner);
        ddmenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                // An item was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)
                ecarLatList.clear();
                ecarLongList.clear();
                dataSource.open();
                ecarLatList = dataSource.getSpecificEcarData((pos+1),1);
                ecarLongList = dataSource.getSpecificEcarData((pos+1),2);
                //ecarTimeList = dataSource.getSpecificEcarData((pos+1),2);
                dataSource.close();
                mMap.clear();
                if(ecarLatList.size()>0) {
                    setColor(true);
                    initMarker();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        tv_disp = (TextView) findViewById(R.id.textView2);
        tv_disp.setText("Bitte Strecke auswählen...");
        bttn_ausw = (Button) findViewById(R.id.bttn_ausw);
        bttn_ausw.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             if (mapFragment.getView().getVisibility() == View.VISIBLE) {
                                                 chartVerbrauch();
                                                 auswertVerbrauch.getView().setVisibility(View.VISIBLE);
                                                 mapFragment.getView().setVisibility(View.GONE);
                                             } else if (auswertVerbrauch.getView().getVisibility() == View.VISIBLE) {
                                                 chartWoche();
                                                 auswertVerbrauch.getView().setVisibility(View.GONE);
                                                 auswertWoche.getView().setVisibility(View.VISIBLE);
                                             } else if (auswertWoche.getView().getVisibility() == View.VISIBLE) {
                                                 auswertWoche.getView().setVisibility(View.GONE);
                                                 mapFragment.getView().setVisibility(View.VISIBLE);
                                             }
                                         }
                                     }
        );
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        initDB();
        if(ecarLatList.size() > 0) {
            initMarker();

            float zoomLevel = 14; //This goes up to 21
            LatLng center = new LatLng(ecarLatList.get(ecarLatList.size()/2).getData(),ecarLongList.get(ecarLongList.size()/2).getData());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, zoomLevel));
        }
    }

    public void initMarker(){
        LatLng latLng = new LatLng(0,0);
        LatLng latLng2 = new LatLng(0,0);
        latLng = new LatLng(ecarLatList.get(0).getData(), ecarLongList.get(0).getData());
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(ecarSessionList.get(0).getName()+" "+0)
                .icon(BitmapDescriptorFactory
                        .defaultMarker(color2)));

        ddist = 0;

        for (int i = 1; i<ecarLatList.size(); i++){
            latLng2 = new LatLng(ecarLatList.get(i).getData(), ecarLongList.get(i).getData());


            //Berechnung der Strecke
            Log.d(LOG_TAG, "Distanz: " + calcDist(ecarLatList.get(i-1).getData(),ecarLongList.get(i-1).getData(),ecarLatList.get(i).getData(),ecarLongList.get(i).getData()));
            ddist = ddist + calcDist(ecarLatList.get(i-1).getData(),ecarLongList.get(i-1).getData(),ecarLatList.get(i).getData(),ecarLongList.get(i).getData());

            mMap.addMarker(new MarkerOptions()
                    .position(latLng2)
                    .title(ecarSessionList.get(0).getName()+" "+i)
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(color2)));
            Polyline line = mMap.addPolyline(new PolylineOptions()
                    .add(latLng, latLng2)
                    .width(6)
                    .color(color));
            latLng = latLng2;
        }
        setStrSpeed(calcVelocity(ecarLatList.get(0).getData(), ecarLongList.get(0).getData(), ecarLatList.get(0).getTime(), ecarLatList.get(ecarLatList.size()-1).getData(), ecarLongList.get(ecarLatList.size()-1).getData(), ecarLatList.get(ecarLatList.size()-1).getTime())+ "");
        float zoomLevel = 14;
        LatLng center = new LatLng(ecarLatList.get(ecarLatList.size()/2).getData(),ecarLongList.get(ecarLongList.size()/2).getData());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, zoomLevel));
        tv_disp.setText(ausgabe());
    }

    public void initDB() {
        Log.d(LOG_TAG, "Das Datenquellen-Objekt wird angelegt.");
        dataSource = new EcarDataSource(this);
        dataSource.open();
        //EcarSession carses = dataSource.createEcarSession(1, "Testlauf");
        //EcarSession carses2 = dataSource.createEcarSession(1, "Listentest");
        //EcarSession carses3 = dataSource.createEcarSession(1, "Listentest2");
        //cardatalat = dataSource.createEcarData(51.488556,3,1);
        //cardatalong = dataSource.createEcarData(7.209091,3,2);
        //cardatalat = dataSource.createEcarData(51.476838,3,1);
        //cardatalong = dataSource.createEcarData(7.230914,3,2);
        //cardatalat = dataSource.createEcarData(51.487481,2,1);
        //cardatalong = dataSource.createEcarData(7.210878,2,2);
        ecarSessionList = dataSource.getAllEcarSession();
        ecarLatList = dataSource.getSpecificEcarData(1, 1);
        ecarLongList = dataSource.getSpecificEcarData(1, 2);

        dataSource.close();

        String[] items = new String[ecarSessionList.size()];
        for (int i = 0; i< ecarSessionList.size();i++){
            items[i] = ecarSessionList.get(i).getName();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        ddmenu.setAdapter(adapter);
    }

    public void setColor(boolean col){
        if (col == true){
            color = Color.GREEN;
            color2 = BitmapDescriptorFactory.HUE_GREEN;
        }else{
            color = Color.RED;
            color2 = BitmapDescriptorFactory.HUE_RED;
        }
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    } //Für die zukünftige Verwendung in Maps

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }//Siehe deg2rad

    //Berechnung der Entfernung zwischen zwei Punkten
    private double calcDist(double lat1, double lon1, double lat2, double lon2) {
        double dist = 0.0;
        double earth = 6371000;
        double lat = Math.toRadians(lat1 - lat2);
        double lng = Math.toRadians(lon1 - lon2);
        double a = Math.sin(lat/2) * Math.sin(lat/2) +
                Math.cos(Math.toRadians(lat2))
                        *Math.cos(Math.toRadians(lat1))
                        *Math.sin(lng/2)
                        *Math.sin(lng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        dist = earth * c;
        return (dist);
    }


    private double calcVelocity(double lat1, double lon1, int time1, double lat2, double lon2, int time2){
        double dist = calcDist(lat1, lon1, lat2, lon2);
        double time_s = time1 - time2;
        time_s = Math.abs(time_s);
        double speed_mps = dist / time_s;
        double speed_kph = (speed_mps * 3600.0) / 1000.0;
        return speed_kph;
    }

    private String ausgabe()//Methode, welche einen String zum ausgeben erzeugt
    {
        return "Strecke: " + getStrDist()+ "\nGeschw.: " + getStrSpeed();
    }


    //Methode um den Verbrauchs Chart zu errechnen
    //Gefahrene Zeit in Sekunden und Batteriestatus in %
    private void chartVerbrauch(){
        ArrayList y = new ArrayList();

        double dist = 0;
        double time = 0;
        double bat = 100;
        double reich = 415000;
        for (int i = 1; i<ecarLatList.size(); i++){
            dist = dist + calcDist(ecarLatList.get(i-1).getData(),ecarLongList.get(i-1).getData(),ecarLatList.get(i).getData(),ecarLongList.get(i).getData());
            time = time + ecarLatList.get(i).getTime()-ecarLatList.get(i-1).getTime();
            bat = 100 - (dist/reich*100);
            y.add(new BarEntry((float)time,(float)bat));
        }
        auswertVerbrauch.chartBeispiel(y);
    }

    private void chartWoche(){
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
                    dist = calcDist(allData.get(i).getData(), allData.get(i + 1).getData(), allData.get(i + 2).getData(), allData.get(i + 3).getData());
                    y.set(6, y.get(6) + dist);
                }
            }
            calnow.add(Calendar.DATE, -1);
            if (calnow.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && calnow.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && calnow.get(Calendar.DATE) == cal.get(Calendar.DATE)) {
                if (allData.get(i).getSesid() == allData.get(i + 2).getSesid()) {
                    dist = calcDist(allData.get(i).getData(), allData.get(i + 1).getData(), allData.get(i + 2).getData(), allData.get(i + 3).getData());
                    y.set(5, y.get(5) + dist);
                }
            }
            calnow.add(Calendar.DATE, -1);
            if (calnow.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && calnow.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && calnow.get(Calendar.DATE) == cal.get(Calendar.DATE)) {
                if (allData.get(i).getSesid() == allData.get(i + 2).getSesid()) {
                    dist = calcDist(allData.get(i).getData(), allData.get(i + 1).getData(), allData.get(i + 2).getData(), allData.get(i + 3).getData());
                    y.set(4, y.get(4) + dist);
                }
            }
            calnow.add(Calendar.DATE, -1);
            if (calnow.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && calnow.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && calnow.get(Calendar.DATE) == cal.get(Calendar.DATE)) {
                if (allData.get(i).getSesid() == allData.get(i + 2).getSesid()) {
                    dist = calcDist(allData.get(i).getData(), allData.get(i + 1).getData(), allData.get(i + 2).getData(), allData.get(i + 3).getData());
                    y.set(3, y.get(3) + dist);
                }
            }
            calnow.add(Calendar.DATE, -1);
            if (calnow.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && calnow.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && calnow.get(Calendar.DATE) == cal.get(Calendar.DATE)) {
                if (allData.get(i).getSesid() == allData.get(i + 2).getSesid()) {
                    dist = calcDist(allData.get(i).getData(), allData.get(i + 1).getData(), allData.get(i + 2).getData(), allData.get(i + 3).getData());
                    y.set(2, y.get(2) + dist);
                }//Log.d("dist",dist/1000+"");
            }
            calnow.add(Calendar.DATE, -1);
            if (calnow.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && calnow.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && calnow.get(Calendar.DATE) == cal.get(Calendar.DATE)) {
                if (allData.get(i).getSesid() == allData.get(i + 2).getSesid()) {
                    dist = calcDist(allData.get(i).getData(), allData.get(i + 1).getData(), allData.get(i + 2).getData(), allData.get(i + 3).getData());
                    y.set(1, y.get(1) + dist);
                }
            }
            calnow.add(Calendar.DATE, -1);
            if (calnow.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && calnow.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && calnow.get(Calendar.DATE) == cal.get(Calendar.DATE)) {
                if (allData.get(i).getSesid() == allData.get(i + 2).getSesid()) {
                    dist = calcDist(allData.get(i).getData(), allData.get(i + 1).getData(), allData.get(i + 2).getData(), allData.get(i + 3).getData());
                    y.set(0, y.get(0) + dist);
                }
            }
            calnow.add(Calendar.DATE, 6);
        }


        //Log.d("Calendar",calnow.get(Calendar.DATE)+"");
        auswertWoche.chartBeispiel(y);
    }

    public String getStrDist() {
        return ddist+"";
    }


    public String getStrSpeed() {
        return strSpeed;
    }

    public void setStrSpeed(String strSpeed) {
        this.strSpeed = strSpeed;
    }
}
