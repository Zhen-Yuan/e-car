package com.example.denis.ecar.auswertungTab;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.denis.ecar.R;
import com.example.denis.ecar.datenbank.EcarData;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class Tab_Map extends Fragment implements OnMapReadyCallback {
    // Initparameter
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static View view;

    // Parameter
    public static final String LOG_TAG = "Tab_Map";
    private String mParam1;
    private String mParam2;
    MapView mMapView;
    private GoogleMap googleMap;
    private List<EcarData> ecarLatList;
    private List<EcarData> ecarLongList;
    public double ddist = 0;
    private float color2;
    private int color;
    private String strSpeed;
    GoogleApiClient mGoogleApiClient;

    public Tab_Map() {

    }

    /**
     * Mit dieser "Factory"-Methode werden neue Instanzen erstellt!!!!!!
     * (Mit den Übergebenen Parametern)
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return Neue Instanz Tab_Map.
     */
    public static Tab_Map newInstance(String param1, String param2) {
        Tab_Map fragment = new Tab_Map();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup                  container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragmenttab_map, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(this);

        return rootView;
    }

    public void setStrecke(List<EcarData> ecarLatList,List<EcarData> ecarLongList){
        this.ecarLatList = ecarLatList;
        this.ecarLongList = ecarLongList;
    }


    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        //initDB();
        if(ecarLatList!=null){
            if(ecarLatList.size() > 0) {
                initMarker();

                float zoomLevel = 14; //This goes up to 21
                LatLng center = new LatLng(ecarLatList.get(ecarLatList.size()/2).getData(),ecarLongList.get(ecarLongList.size()/2).getData());
                this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, zoomLevel));
            }
        }}

    //Platziert Marker der entsprechenden Strecke auf der Map
    public void initMarker(){
        LatLng latLng;
        LatLng latLng2;
        this.googleMap.clear();
        latLng = new LatLng(ecarLatList.get(0).getData(), ecarLongList.get(0).getData());
        this.googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("test")
                .icon(BitmapDescriptorFactory
                        .defaultMarker(color2)));

        ddist = 0;

        for (int i = 1; i<ecarLatList.size(); i++){
            latLng2 = new LatLng(ecarLatList.get(i).getData(), ecarLongList.get(i).getData());


            //Berechnung der Strecke
            Log.d(LOG_TAG, "Distanz: " + calcDist(ecarLatList.get(i-1).getData(),ecarLongList.get(i-1).getData(),ecarLatList.get(i).getData(),ecarLongList.get(i).getData()));
            ddist = ddist + calcDist(ecarLatList.get(i-1).getData(),ecarLongList.get(i-1).getData(),ecarLatList.get(i).getData(),ecarLongList.get(i).getData());

            this.googleMap.addMarker(new MarkerOptions()
                    .position(latLng2)
                    .title("test")
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(color2)));
            Polyline line = this.googleMap.addPolyline(new PolylineOptions()
                    .add(latLng, latLng2)
                    .width(6)
                    .color(color));
            latLng = latLng2;
        }
        setStrSpeed(calcVelocity(ecarLatList.get(0).getData(), ecarLongList.get(0).getData(), ecarLatList.get(0).getTime(), ecarLatList.get(ecarLatList.size()-1).getData(), ecarLongList.get(ecarLatList.size()-1).getData(), ecarLatList.get(ecarLatList.size()-1).getTime()));
        float zoomLevel = 14;
        LatLng center = new LatLng(ecarLatList.get(ecarLatList.size()/2).getData(),ecarLongList.get(ecarLongList.size()/2).getData());
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, zoomLevel));
        //tv_disp.setText(ausgabe());
    }

    //Farbe für den Marker
    public void setColor(boolean col){
        if (col == true){
            color = Color.GREEN;
            color2 = BitmapDescriptorFactory.HUE_GREEN;
        }else{
            color = Color.RED;
            color2 = BitmapDescriptorFactory.HUE_RED;
        }
    }


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

    //Berechnung der Geschwindigkeit zwischen zwei Punkten
    private double calcVelocity(double lat1, double lon1, int time1, double lat2, double lon2, int time2){
        double dist = calcDist(lat1, lon1, lat2, lon2);
        double time_s = time1 - time2;
        time_s = Math.abs(time_s);
        double speed_mps = dist / time_s;
        double speed_kph = (speed_mps * 3600.0) / 1000.0;
        return speed_kph;
    }

    public void setStrSpeed(double strSpeed) {
        this.strSpeed = String.format("%.2f", strSpeed);
    }


}
