package com.example.denis.ecar.liveAuswertung;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.denis.ecar.MapsActivity;
import com.example.denis.ecar.R;
import com.example.denis.ecar.auswertung.AuswertungCO2;
import com.example.denis.ecar.auswertung.AuswertungElektro;
import com.example.denis.ecar.datenbank.EcarData;
import com.example.denis.ecar.datenbank.EcarDataSource;
import com.example.denis.ecar.datenbank.EcarSession;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.DetectedActivityResult;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Denis on 09.11.2017.
 */

public class LiveAuswertung extends Activity
{

    TextView tvGeschwindigkeit;
    ListView lvAusgabe;
    FloatingActionButton fabStartStop;
    String[] values;
    private List<EcarSession> ecarSessionList;
    private List<EcarData> ecarLatList, ecarLongList;
    private double dStrecke;
    public String strActivity;
    public String strWeather;
    private Context con;
    public String strHeadphones;
    public String strLocation;
    private Location location;
    private boolean bAufnahme;
    private EcarSession ecarsession;
    private EcarDataSource dataSource = new EcarDataSource(this);
    public Activity activity; // Für die Initalisierung der Ggl-AwarenessAPI benötigt -> siehe initAwareness
    GoogleApiClient mGoogleApiClient; //Wird für die Verwendung der AwarenessAPI benötigt.
    private ArrayList<Location> locationList = new ArrayList<>(); //Liste zum Speichern von Locations.
    private int intervall;
    private AuswertungCO2 auswertungCO2;
    private AuswertungElektro auswertungElektro;
    SharedPreferences sharedPreferences;

    public LiveAuswertung()
    {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_auswertung);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // Landscape erzwingen
        sharedPreferences = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
        init();
    }

    @Override
    public void setContentView(View view)
    {
        super.setContentView(view);
    }

    private void init()
    {
        con = this;
        auswertungCO2 = new AuswertungCO2();
        auswertungElektro = new AuswertungElektro();
        tvGeschwindigkeit = (TextView) findViewById(R.id.tvGeschwindigkeit);
        tvGeschwindigkeit.setText("0"+" km/h");
        fabStartStop = (FloatingActionButton) findViewById(R.id.fabStartStop);
        lvAusgabe = (ListView) findViewById(R.id.lvDaten);
        // Anzeigearray
        values = new String[]{ //TODO: ZEIT!!!
                "Geschwindigkeit(⌀): ",
                "Batterie: ",
                "Strecke: ",
                "CO2 Einsparung: ",
                "Wetter: ",
        };
        initListView(values);
        //Nicht plausible Startwerte
        setBatterie(-1);
        setCO2Einsparung(-1);
        setDurchschnittGeschwindigkeit(-1);
        setWetter("-1");
        setStrecke(-1);
        //initAwareness();
        initFab();
        intervall = sharedPreferences.getInt("intervall", 30); //Aufnahme Intervall einstellen über sharedpreferences
        bAufnahme = false;
        Log.d("Aufnahmeintervall ", intervall+"");
    }

    private void initListView(String[] values) {

        // Adapterdefinition
        // Erster  Parameter - Context
        // Zweiter Parameter - Layout(reihen)
        // Dritter Parameter - lvID (Wo wird geschrieben?)
        // Vierter Parameter - DatenArray

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);


        // Zuweisung des Adapters
        lvAusgabe.setAdapter(adapter);
    }

    private void startAwareness() {
        activity();
        location();
        weather();
        Toast toast = Toast.makeText(getApplicationContext(),"StartAwareness geladen", Toast.LENGTH_SHORT);
        toast.show();
    }
    private void initAwareness()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(LiveAuswertung.this) //Erstellung eines GoogleApiClients, um die AwarenessAPI verwenden zu können.
                .addApi(Awareness.API) // Gewünschte Api hinzufügen.
                .build(); //Erstellung des Objekts.
        mGoogleApiClient.connect();
        Toast toast = Toast.makeText(getApplicationContext(),"Awareness geladen", Toast.LENGTH_SHORT);
        toast.show();
        startAwareness();
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
                    setStrWeather("Wetter wird ermittelt...");
                    setWetter("Wetter wird ermittelt...");
                    Toast toast = Toast.makeText(getApplicationContext(),"Wetter lädt nicht", Toast.LENGTH_SHORT);
                    toast.show();
                    initListView(values);
                    //AUSGABE
                    return;
                }
                Weather weather = weatherResult.getWeather();
                setStrWeather(weather.toString());
                setWetter(weather.toString()); // Änderung von values
                Toast toast = Toast.makeText(getApplicationContext(), weather.toString(), Toast.LENGTH_SHORT);
                toast.show();
                initListView(values); //Anzeige ListView! String[] values ändern und Methode aufrufen, um text zu aktualisieren.
            }
        });
    }//Methode, welche durch die AwarenessAPI das aktuelle Wetter ermittelt.
    private void activity() //Methode, welche durch die AwarenessAPI die aktuelle Aktivität ermittelt.
    {
        Awareness.SnapshotApi.getDetectedActivity(mGoogleApiClient).setResultCallback(new ResultCallback<DetectedActivityResult>() {
            @Override
            public void onResult(@NonNull DetectedActivityResult detectedActivityResult) {
                if (!detectedActivityResult.getStatus().isSuccess()) {
                    setStrActivity("Aktuelle Aktivität wird ermittelt...");//Ausgabe, falls noch keine anderen Werte ausgegeben wurden.
                    Toast toast = Toast.makeText(getApplicationContext(),"Aktuelle Aktivität wird ermittelt...", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                ActivityRecognitionResult ar = detectedActivityResult.getActivityRecognitionResult();
                DetectedActivity probableActivity = ar.getMostProbableActivity();
                setStrActivity(probableActivity.toString());//Ausgabe, falls noch keine anderen Werte ausgegeben wurden.
                Toast toast = Toast.makeText(getApplicationContext(),probableActivity.toString(), Toast.LENGTH_SHORT);
                toast.show();

            }
        });
    }
    private void handler(final int i) // Methode, welche alle n Sekunden einen bestimmten Quelltext ausführt. (1000 = 1s)
    {
        final Handler ha = new Handler();
        ha.postDelayed(new Runnable() {

            //Hier werden die Berechnungen zur Strecke und Geschwindigkeit aufgerufen
            @Override
            public void run() {
                //TODO: abspeichern der Daten in die Datenbank
                if (bAufnahme) {
                    ha.postDelayed(this, i); //1000 = 1s
                    location();
                    tvGeschwindigkeit.setText(calcDist(locationList.get(0).getLatitude(), locationList.get(0).getLongitude(), locationList.get(locationList.size() - 1).getLatitude(), locationList.get(locationList.size() - 1).getLongitude()) + " m");

                    if (locationList.size() > 1) {
                        tvGeschwindigkeit.setText(Math.round(calcVelocity(locationList.get(locationList.size() - 1), locationList.get(locationList.size() - 2))) + " km/h");
                        //setStrSpeed(locationList.get(locationList.size() - 1).getTime()+"-"+locationList.get(locationList.size() - 2).getTime());
                    }

                    dataSource.createEcarData(locationList.get(locationList.size()-1).getLatitude(),ecarsession.getSesid(),1);
                    dataSource.createEcarData(locationList.get(locationList.size()-1).getLongitude(),ecarsession.getSesid(),2);
                    Log.d("DB insert: ", locationList.get(locationList.size()-1).getLatitude() + " , " + ecarsession.getSesid() + " , " + 1);
                }else
                {
                    ha.removeMessages(0);
                }
            }
        }, i);
    }
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
    //Berechnung der Geschwindigkeit
    private double calcVelocity(Location p1, Location p2){
        double dist = calcDist(p1.getLatitude(), p1.getLongitude(), p2.getLatitude(), p2.getLongitude());
        double time_s = intervall;//(p2.getTime() - p1.getTime()) / 1000.0;
        double speed_mps = dist / time_s;
        double speed_kph = (speed_mps * 3600.0) / 1000.0;
        return speed_kph;
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
                    setStrLocation("Suche nach GPS-Signal...");
                    Toast toast = Toast.makeText(getApplicationContext(),"Suche nach GPS-Signal...", Toast.LENGTH_SHORT);
                    toast.show();
                    //AUSGABE
                    return;
                }

                setLocation(locationResult.getLocation()); //Aktuelle Location wird zwischengespeichert.
                setStrLocation("Latitude: " + locationResult.getLocation().getLatitude() + "//Longitude:" + locationResult.getLocation().getLongitude());
                locationList.add(locationResult.getLocation());// Für spätere Verwendung werden die Locations in einer Liste temporär gespeichert.
                setLocation(locationResult.getLocation());
                Toast toast = Toast.makeText(getApplicationContext(),locationResult.getLocation().toString(), Toast.LENGTH_SHORT);
                double dStrecke;
                dStrecke = gesamteStrecke();
                setStrecke(dStrecke); // Anzeige (Strecke)
                setBatterie(auswertungElektro.getBatterieProzent(dStrecke,25,410)); // Prozentanzeige Batterie
                setCO2Einsparung(auswertungCO2.getCO2CO2AusstossElektro(25));
                toast.show();
            }
        });
    }//Methode, welche durch die AwarenessAPI die aktuelle Location ermittelt.

    private void initFab() {
        fabStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bAufnahme == false) {
                    final EditText input = new EditText(con);
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                    AlertDialog.Builder builder = new AlertDialog.Builder(LiveAuswertung.this);
                    builder.setMessage("Eine neue Strecke starten?")
                            .setTitle("Neue Strecke")
                            .setView(input)
                            .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User bricht ab
                                    bAufnahme = false;
                                    dataSource.close();
                                }
                            })
                            .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    bAufnahme = true;
                                    fabStartStop.setImageResource(android.R.drawable.ic_media_pause); // Ändert FabIcon TODO: STOP-Button, nicht PAUSE.
                                    initAwareness();
                                    //getNewSID();
                                    dataSource.open();
                                    if(input != null)//Leere Streckennamen sind nicht erlaubt
                                    {
                                        ecarsession = dataSource.createEcarSession(1, input.toString());
                                    }
                                    handler(intervall*1000);
                                }
                            });
                    // Erstellt AlertDialogobjekt und zeigt es an
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else
                {
                    dataSource.close(); // Für Testzwecke, damit die DB nicht unnötig genutzt wird. (Falls ein Fehler in der Logik vorhanden ist)
                    fabStartStop.setImageResource(android.R.drawable.ic_media_play); // Ändert FabIcon
                }
            }
        });
    }//Listener

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

    public double gesamteStrecke()
    {
        double dStrecke = 0;
        if (locationList.size() >1)
            for(int i= 1; i < locationList.size();i++)
            {
                dStrecke += calcDist(locationList.get(i-1).getLatitude(),locationList.get(i-1).getLongitude(),locationList.get(i).getLatitude(),locationList.get(i).getLongitude());
            }
            return dStrecke;
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

    // Neue SessionID
    //Neue Session ID generieren
    private int getNewSID(){
        List<EcarSession> ecarSessionList;
        try {
            ecarSessionList = dataSource.getAllEcarSession();
            Log.d("getNewSID: ", "try");
            return ecarSessionList.get(ecarSessionList.size() - 1).getSesid() + 1;
        }catch(Exception e){
            Log.d("getNewSID: ", "catch");
            return 1;
        }
    }
}
