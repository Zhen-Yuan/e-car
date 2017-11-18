package com.example.denis.ecar.liveAuswertung;

import android.Manifest;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.denis.ecar.MapsActivity;
import com.example.denis.ecar.R;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.DetectedActivityResult;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

/**
 * Created by Denis on 09.11.2017.
 */

public class LiveAuswertung extends Activity
{

    TextView tvGeschwindigkeit;
    ListView lvAusgabe;
    FloatingActionButton fabStartStop;
    String[] values;
    public String strActivity;
    public String strWeather;
    public String strHeadphones;
    public String strLocation;
    private Location location;
    public Activity activity; // Für die Initalisierung der Ggl-AwarenessAPI benötigt -> siehe initAwareness
    GoogleApiClient mGoogleApiClient; //Wird für die Verwendung der AwarenessAPI benötigt.
    private ArrayList<Location> locationList = new ArrayList<>(); //Liste zum Speichern von Locations.
    public LiveAuswertung()
    {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_auswertung);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // Landscape erzwingen
        init();
        initAwareness();
    }

    @Override
    public void setContentView(View view)
    {
        super.setContentView(view);
    }

    private void init()
    {
        tvGeschwindigkeit = (TextView) findViewById(R.id.tvGeschwindigkeit);
        tvGeschwindigkeit.setText("0"+" km/h");
        fabStartStop = (FloatingActionButton) findViewById(R.id.fabStartStop);
        lvAusgabe = (ListView) findViewById(R.id.lvDaten);
        // Anzeigearray
        values = new String[]{
                "Geschwindigkeit(⌀): ",
                "Batterie: ",
                "Strecke: ",
                "CO2 Einsparung: ",
                "Wetter: ",
        };

        // Adapterdefinition
        // Erster  Parameter - Context
        // Zweiter Parameter - Layout(reihen)
        // Dritter Parameter - lvID (Wo wird geschrieben?)
        // Vierter Parameter - DatenArray

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);


        // Zuweisung des Adapters
        lvAusgabe.setAdapter(adapter);
        //Nicht plausible Startwerte
        setBatterie(-1);
        setCO2Einsparung(-1);
        setDurchschnittGeschwindigkeit(-1);
        setWetter("-1");
        setStrecke(-1);
    }
    private void initAwareness()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(LiveAuswertung.this) //Erstellung eines GoogleApiClients, um die AwarenessAPI verwenden zu können.
                .addApi(Awareness.API) // Gewünschte Api hinzufügen.
                .build(); //Erstellung des Objekts.
        mGoogleApiClient.connect();
        location();
    }

    private void weather() {
        //Persmissioncheck
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Awareness.SnapshotApi.getWeather(mGoogleApiClient).setResultCallback(new ResultCallback<WeatherResult>() {
            @Override
            public void onResult(@NonNull WeatherResult weatherResult)
            {
                if (!weatherResult.getStatus().isSuccess()) {
                    setStrWeather("Wetter: Wetter konnte nicht geladen werden.");
                    //AUSGABE
                    return;
                }
                Weather weather = weatherResult.getWeather();
                setStrWeather(weather.toString());
                //AUSGABE
            }
        });
    }//Methode, welche durch die AwarenessAPI das aktuelle Wetter ermittelt.
    private void activity() //Methode, welche durch die AwarenessAPI die aktuelle Aktivität ermittelt.
    {
        Awareness.SnapshotApi.getDetectedActivity(mGoogleApiClient).setResultCallback(new ResultCallback<DetectedActivityResult>() {
            @Override
            public void onResult(@NonNull DetectedActivityResult detectedActivityResult) {
                if (!detectedActivityResult.getStatus().isSuccess()) {
                    setStrActivity("Konnte noch nicht ermittelt werden.");//Ausgabe, falls noch keine anderen Werte ausgegeben wurden.
                    //AUSGABE
                    return;
                }
                ActivityRecognitionResult ar = detectedActivityResult.getActivityRecognitionResult();
                DetectedActivity probableActivity = ar.getMostProbableActivity();
                setStrActivity(probableActivity.toString());//Ausgabe, falls noch keine anderen Werte ausgegeben wurden.
                //AUSGABE
            }
        });
    }


    private void location() {
        //Automatisch generierter Permissioncheck.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Awareness.SnapshotApi.getLocation(mGoogleApiClient).setResultCallback(new ResultCallback<LocationResult>() {
            @Override
            public void onResult(@NonNull LocationResult locationResult) {
                if (!locationResult.getStatus().isSuccess()) {
                    setStrLocation("Locationstatus nicht verfügbar");
                    //AUSGABE
                    return;
                }

                setLocation(locationResult.getLocation()); //Aktuelle Location wird zwischengespeichert.
                setStrLocation("Latitude: " + locationResult.getLocation().getLatitude() + "//Longitude:" + locationResult.getLocation().getLongitude());
                locationList.add(locationResult.getLocation());// Für spätere Verwendung werden die Locations in einer Liste temporär gespeichert.
                setLocation(locationResult.getLocation());
            }
        });
    }//Methode, welche durch die AwarenessAPI die aktuelle Location ermittelt.

    //GETTER SETTER
    public String getStrActivity() {
        return strActivity;
    }

    public void setStrActivity(String strActivity) {
        this.strActivity = strActivity;
    }

    public String getStrWeather() {
        return strWeather;
    }

    public void setStrWeather(String strWeather) {
        this.strWeather = strWeather;
    }

    public String getStrHeadphones() {
        return strHeadphones;
    }

    public void setStrHeadphones(String strHeadphones) {
        this.strHeadphones = strHeadphones;
    }

    public String getStrLocation() {
        return strLocation;
    }

    public void setStrLocation(String strLocation) {
        this.strLocation = strLocation;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    //Setter für value
    /*
    Beispiel:
    setDurchschnittGeschwindigkeit(dBerechnete Geschwindigkeit)
    Fertig. Anzeige aktualisiert!
     */
    private void setDurchschnittGeschwindigkeit(double dDurchschnittGeschwindigkeit)
    {
        values[0] = "Geschwindigkeit(⌀): " + String.valueOf(dDurchschnittGeschwindigkeit)+"km/h";
    }
    private void setBatterie(double dBatterieProzent)
    {
        values[1] = "Batterie: " + String.valueOf(dBatterieProzent)+"%";
    }
    private void setStrecke(double dStrecke)
    {
        values[2] = "Strecke: " + String.valueOf(dStrecke) +"km";
    }
    private void setCO2Einsparung(double dCO2Einsparung)
    {
        values[3] = "CO2 Einsparung: " + String.valueOf(dCO2Einsparung) + "g";
    }
    private void setWetter(String strWetter)
    {
        values[4] = "Wetter: " + strWetter;
    }
}
