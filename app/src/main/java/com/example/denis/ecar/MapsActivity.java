package com.example.denis.ecar;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.denis.ecar.datenbank.EcarDataSource;
import com.example.denis.ecar.datenbank.EcarSession;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.DetectedActivityResult;
import com.google.android.gms.awareness.snapshot.HeadphoneStateResult;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by denis on 28.07.2017.
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String strAusgabe;
    private String strActivity;
    private String strWeather;
    private String strHeadphones;
    private String strLocation;
    private String strDist = "0"; // TODO: Warum wird die Variable hier initalisiert?
    private String strSpeed = "0";
    private LatLng latLng;
    private TextView txt_strecke, txt_geschw; //-> Infofenster.
    private Location location;
    private ImageButton imgbttn_focus; // Ähnlich, wie bei GoogleMaps Button zum fokussieren
    private Button bttn_loc;
    private boolean record = false;
    private EcarDataSource dataSource = new EcarDataSource(this);
    private int SID;
    private int intervall;
    SharedPreferences sp;
    private Context con = this;
    private EcarSession ecs;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser fUser;
    DecimalFormat f = new DecimalFormat("#0");
    DecimalFormat f2 = new DecimalFormat("#0.00");

    GoogleApiClient mGoogleApiClient; //Wird für die Verwendung der AwarenessAPI benötigt.
    private ArrayList<Location> locationList = new ArrayList<>(); //Liste zum Speichern von Locations.
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        sp = getSharedPreferences("interv", Context.MODE_PRIVATE);

        //Todo UserID
        fUser = mAuth.getCurrentUser();
        int uid = fUser.getUid().hashCode();
        Log.d("UserID", fUser.getUid());


        init();//Aufruf der Initalisierungsmethode
        startAwareness();//Start der Awarenessmethoden.
        imgbttn_focus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                focus();
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
    }
    private void init()//Initalisierungsmethode
    {
        imgbttn_focus = (ImageButton) findViewById(R.id.imgbttn_focus);
        bttn_loc = (Button) findViewById(R.id.bttn_loc);
        txt_strecke = (TextView) findViewById(R.id.txt_strecke);
        txt_geschw = (TextView) findViewById(R.id.txt_geschw);
        onclick();
        Toast toast = Toast.makeText(getApplicationContext(), "Zum Fokussieren der akuellen Position den Button unten rechts betätigen!", Toast.LENGTH_LONG);
        toast.show();
        mGoogleApiClient = new GoogleApiClient.Builder(MapsActivity.this) //Erstellung eines GoogleApiClients, um die AwarenessAPI verwenden zu können.
                .addApi(Awareness.API) // Gewünschte Api hinzufügen.
                .build(); //Erstellung des Objekts.
        mGoogleApiClient.connect();

        intervall = sp.getInt("interval", 30); //Aufnahme Intervall einstellen über sharedpreferences
        Log.d("Aufnahmeintervall ", intervall+"");
    }
    //Awarenessmethoden
    private void startAwareness() {
        activity();
        location();
        weather();
        headphones();
    }//Für Demonstationszwecke werden alle Awarenessmethoden in startAwareness aufgerufen.
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
                    ausgabe();
                    return;
                }
                Weather weather = weatherResult.getWeather();
                    setStrWeather(weather.toString());
                    ausgabe();
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
                    ausgabe();
                    return;
                }
                ActivityRecognitionResult ar = detectedActivityResult.getActivityRecognitionResult();
                DetectedActivity probableActivity = ar.getMostProbableActivity();
                setStrActivity(probableActivity.toString());//Ausgabe, falls noch keine anderen Werte ausgegeben wurden.
                ausgabe();
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
                    ausgabe();
                    return;
                }

                setLocation(locationResult.getLocation()); //Aktuelle Location wird zwischengespeichert.
                setStrLocation("Latitude: " + locationResult.getLocation().getLatitude() + "//Longitude:" + locationResult.getLocation().getLongitude());
                ausgabe();
                locationList.add(locationResult.getLocation());// Für spätere Verwendung werden die Locations in einer Liste temporär gespeichert.
                setLocation(locationResult.getLocation());
            }
        });
    }//Methode, welche durch die AwarenessAPI die aktuelle Location ermittelt.


    private void headphones() //Methode, welche durch die AwarenessAPI den aktuellen Status der Kopfhörer ermittelt.
    {
        Awareness.SnapshotApi.getHeadphoneState(mGoogleApiClient).setResultCallback(new ResultCallback<HeadphoneStateResult>() {
            @Override
            public void onResult(@NonNull HeadphoneStateResult headphoneStateResult) {
                if (!headphoneStateResult.getStatus().isSuccess()) {
                    setStrHeadphones("Status kann nicht abgefragt werden.");
                    ausgabe();
                    return;
                }
                HeadphoneState headphoneState = headphoneStateResult.getHeadphoneState();
                if (headphoneState.getState() == HeadphoneState.PLUGGED_IN)
                {
                    setStrHeadphones("Eingesteckt");
                    ausgabe();
                }else
                {
                    setStrHeadphones("Nicht eingesteckt");
                    ausgabe();
                }
            }
        });
    }
    //Methoden
    private String ausgabe()//Methode, welche einen String zum ausgeben erzeugt
    {
        txt_strecke.setText(getStrDist() + " km");
        txt_geschw.setText(getStrSpeed() + " kmh");


        return "Location: "+getStrLocation()+"\nActivity: "+getStrActivity()+"\nKopfhörer: "+getStrHeadphones()+"\nWetter: "+getStrWeather()+ "\nStrecke: " + getStrDist()+ "\nGeschw.: " + getStrSpeed();
    }
    private  void onclick()
    {


        bttn_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bttn_loc.getText() == "start"){

                    final String[] m_Text = {"Strecke_" + SID};
                    AlertDialog.Builder builder = new AlertDialog.Builder(con);
                    builder.setTitle("Streckenname: ");

                    final EditText input = new EditText(con);
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                    builder.setView(input);

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            m_Text[0] = input.getText().toString();
                            //SID = getNewSID();
                            bttn_loc.setText("stop");
                            bttn_loc.invalidate();
                            record = true;
                            dataSource.open();
                            ecs = dataSource.createEcarSession(1, m_Text[0]);
                            handler(intervall*1000);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
                else
                {
                    bttn_loc.setText("start");
                    record = false;
                    dataSource.close();
                }

            }
        }
        );

    }


    private void handler(final int i) // Methode, welche alle n Sekunden einen bestimmten Quelltext ausführt. (1000 = 1s)
    {
        final Handler ha = new Handler();
        ha.postDelayed(new Runnable() {

            //Hier werden die Berechnungen zur Strecke und Geschwindigkeit aufgerufen
            @Override
            public void run() {
                //TODO: abspeichern der Daten in die Datenbank
                if (record) {
                    ha.postDelayed(this, i); //1000 = 1s
                    location();


                    if (locationList.size() > 1) {
                        setStrDist(f2.format(calcDist(locationList.get(0).getLatitude(), locationList.get(0).getLongitude(), locationList.get(locationList.size() - 1).getLatitude(), locationList.get(locationList.size() - 1).getLongitude())/1000) + "");
                        setStrSpeed(f.format(calcVelocity(locationList.get(locationList.size() - 1), locationList.get(locationList.size() - 2))) + "");
                        //setStrSpeed(locationList.get(locationList.size() - 1).getTime()+"-"+locationList.get(locationList.size() - 2).getTime());

                        dataSource.createEcarData(locationList.get(locationList.size()-1).getLatitude(),ecs.getSesid(),1);
                        dataSource.createEcarData(locationList.get(locationList.size()-1).getLongitude(),ecs.getSesid(),2);
                        Log.d("DB insert: ", locationList.get(locationList.size()-1).getLatitude() + " , " + ecs.getSesid() + " , " + 1);

                        focus();
                    }


                }
            }
        }, i);
    }


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

    //Methode um die aufgenommenen Werte in die DB zu schreiben
    private void writeDB(){
        List<EcarSession> ecarSessionList;
        ecarSessionList = dataSource.getAllEcarSession();
        for (int i = 0; i< ecarSessionList.size();i++){
            Log.d("SesName: ", ecarSessionList.get(i).getName());
            Log.d("SesID: ", ecarSessionList.get(i).getSesid()+"");
        }
    }


    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    } //Für die zukünftige Verwendung in Maps

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }//Siehe deg2rad

    private void focus() // Methode welche
    {
        try {
            if (location != null) {//Nur ausführen, falls ein Locationobjekt vorhanden ist.
                setLatLng(new LatLng(getLocation().getLatitude(), getLocation().getLongitude()));//Maps verwendet LatLng
            }
        }catch (Exception e)
        {
            //Fallls die Konvertierung des Locationobjekts fehl schlägt, soll der Fehler als Toast ausgegeben werden.
            Toast toast = Toast.makeText(getApplicationContext(), (CharSequence) e, Toast.LENGTH_LONG);
            toast.show();
        }
        if (latLng != null) {//Nur ausführen, falls ein LatLng vorhanden ist.
            Circle circle = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(getLatLng().latitude,getLatLng().longitude))
                    .radius(1)
                    .strokeColor(Color.RED)
                    .fillColor(Color.BLUE));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(getLatLng()));
            mMap.setMinZoomPreference(17.0f);
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

    //Berechnung der Geschwindigkeit
    private double calcVelocity(Location p1, Location p2){
        double dist = calcDist(p1.getLatitude(), p1.getLongitude(), p2.getLatitude(), p2.getLongitude());
        double time_s = intervall;//(p2.getTime() - p1.getTime()) / 1000.0;
        double speed_mps = dist / time_s;
        double speed_kph = (speed_mps * 3600.0) / 1000.0;
        return speed_kph;
    }


    //Getter/Setter TODO: Anmerkung -> Auf fehlende Datenkapselung hinweisen!

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        //i++;
        //location.setLongitude(location.getLongitude()+(i*0.000001));
        this.location = location;
    }

    public ArrayList<Location> getLocationList() {
        return locationList;
    }

    public void setLocationList(ArrayList<Location> locationList) {
        this.locationList = locationList;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getStrAusgabe() {
        return strAusgabe;
    }

    public void setStrAusgabe(String strAusgabe) {
        this.strAusgabe = strAusgabe;
    }

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

    public String getStrDist() {
        return strDist;
    }

    public void setStrDist(String strDist) {
        this.strDist = strDist;
    }

    public String getStrSpeed() {
        return strSpeed;
    }

    public void setStrSpeed(String strSpeed) {
        this.strSpeed = strSpeed;
    }

}
