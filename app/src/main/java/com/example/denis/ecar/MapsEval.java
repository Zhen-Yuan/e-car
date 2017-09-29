package com.example.denis.ecar;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapseval);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


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