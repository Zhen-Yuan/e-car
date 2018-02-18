package com.example.denis.ecar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.denis.ecar.StreckenView.ViewStrecken;
import com.example.denis.ecar.auswertungTab.AuswertungMain;
import com.example.denis.ecar.datenbank.EcarCar;
import com.example.denis.ecar.datenbank.EcarDataSource;
import com.example.denis.ecar.datenbank.EcarSession;
import com.example.denis.ecar.fragment_Uebersicht.UebersichtFragment;
import com.example.denis.ecar.fragmente_Auto.CreateCarFragment;
import com.example.denis.ecar.fragmente_Auto.InfoFragment;
import com.example.denis.ecar.fragmente_Auto.SelectedIdVariable;
import com.example.denis.ecar.fuellmethoden.DataCollector;
import com.example.denis.ecar.liveAuswertung.LiveAuswertung;
import com.example.denis.ecar.login.LoginActivity;
import com.example.denis.ecar.sharedPref.Settings;
import com.example.denis.ecar.sharedPref.User;
import com.example.denis.ecar.swipes.SwipeAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity
        implements ValueEventListener, NavigationView.OnNavigationItemSelectedListener {

    Button bttn_shop,bttn_socialmedia,bttn_info,bttn_maps;
    DataCollector dataCollector;
    public static ViewPager vPager;
    private static List<EcarCar> eclist;
    public InfoFragment ifragment;
    public static SelectedIdVariable selectedCarId;
    public static boolean bswitch = false;
    public SwipeAdapter swad;
    public static final double Rworld = 6372.8;
    public static int newTime;


    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    // Einfache Ausgabe über LogTag zum testen ermöglichen
    private EcarDataSource dataSource; // Datenbank

    private FirebaseAuth firebaseAuth;
    private DatabaseReference firebaseDB;
    private CircleImageView ivProfileImage;
    private TextView username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        selectedCarId = new SelectedIdVariable();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // nav_header
        username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvUsername);
        ivProfileImage = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.ivProfileImage);

        init();
        initImageView();
    }
    //Methoden
    private void init()
    {//Initalisierung
        dataSource = new EcarDataSource(this);
        InitTestValues();

        /*int width = eclist.get(0).getCarpic().getWidth();
        int height = eclist.get(0).getCarpic().getHeight();
        images = new int[width * height];
        eclist.get(0).getCarpic().getPixels(images, 0, width, 0, 0, width, height);*/

        dataCollector = new DataCollector();
        uebersichtFragment();
        initImageView();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDB = FirebaseDatabase.getInstance().getReference().child("users")
                .child(firebaseAuth.getCurrentUser().getUid());
        firebaseDB.addListenerForSingleValueEvent(this);
    }
    private void InitTestValues(){
        if(bswitch == false) {
            dataSource.open();
            if (dataSource.checkCar("Model S 75", "Tesla") == null) {
                Bitmap bitmapvar = BitmapFactory.decodeResource(getResources(), R.drawable.tesla_s75);
                dataSource.createEcarCar(
                        "Model S 75",
                        "Tesla",
                        "Universaler Mobile Connector mit rotem 11 kW-Industriestrom-Adapter (400V, 16A) und 3 kW \"Schuko\"-Steckdosenadapter (230V, 13A)\nZugang zum wachsenden Tesla Supercharger-Netzwerk \n\nInnenansicht: \n17-Zoll-Touchscreen \nBordkarten und Navigation mit Gratis-Updates für 7 Jahre \nSchlüsselloser Zugang \nWiFi- und Mobilfunk-Konnektivität \nFernbedienung über Mobile-App für Smartphones \nTürgriffe mit automatischem Einzug \n\" +Elektrische Fensterheber mit Tastendruck-Automatik \nHD-Rückfahrkamera \nBluetooth-Freisprechsystem \nSprachgesteuerte Funktionen \nAM-, FM-, DAB+ und Internet-Radio \nSpiegel mit Abblendautomatik \nLED-Ambienteleuchten im Innenraum \nBeleuchtete Türgriffe \nElektrisch einklappbare, beheizbare Seitenspiegel mit Positionsspeicher \nZwei USB-Anschlüsse für Mediengeräte und Nebenverbraucher \n12 V-Netzbuchse \nBeheizbare Vordersitze mit 12 elektrischen Verstellfunktionen, Memoryfunktion und Fahrerprofilspeicher \nFrontstauraum (statt sperrigem Motor!), Gepäckraum hinten und 60/40 umklappbare Rücksitze - 894 Liter Stauraum",
                        0,
                        23,
                        420,
                        100,
                        bitmapvar,
                        3);
                bitmapvar = BitmapFactory.decodeResource(getResources(), R.drawable.renault_zoe);
                dataSource.createEcarCar(
                        "ZOE",
                        "Renault",
                        "Der französische Autohersteller Renault verkauft den Renault Zoe seit 2013, 2017 gab es ein wichtiges Update. Der fünftürige Kleinwagen kommt mit seinem 65 kW Motor auf eine Höchstgeschwindigkeit von 135 km/h. Wie die meisten Elektroautos hat der Renault Zoe mit seiner „Z.E. 40 Batterie“ genannten 41-kWh-Batterie (Vormodell: 22 kWh) eine anständige Reichweite: Etwa 400 km schafft das Elektroauto auf dem Papier (NEFZ), Renault gibt die realistische Reichweite mit 300 km an. Ein kompaktes Stadtauto, das auch Landpartien schafft.",
                        0,
                        14,
                        350,
                        41,
                        bitmapvar,
                        3);
                bitmapvar = BitmapFactory.decodeResource(getResources(), R.drawable.bmw_i3);
                dataSource.createEcarCar(
                        "i3",
                        "BMW",
                        "Der Kleinwagen BMW i3 wurde von vornherein als reines Elektroauto konzipiert. Kein deutscher Wettbewerber ging dabei so konsequent und durchdacht vor wie BMW. So wiegt der BMW i3 mit 1.195 Kilogramm rund 400 kg weniger als etwa ein Nissan Leaf oder ein Volkswagen e-Golf. Dazu ist er mit 125 kW (170 PS) sauschnell und schafft je nach Akku eine Reichweite 190 bis 300 Kilometer. Ein ideales Stadtauto.",
                        0,
                        13,
                        290,
                        27,
                        bitmapvar,
                        3);
                bitmapvar = BitmapFactory.decodeResource(getResources(), R.drawable.nissan_leaf);
                dataSource.createEcarCar(
                        "Leaf",
                        "Nissan",
                        "Der Nissan Leaf gilt als das Elektroauto mit der besten CO2-Bilanz. Ab einem Preis von 23.365 Euro zuzüglich einer monatlichen Batteriemiete ab 79 Euro kann man ihn kaufen. Dabei sichert man sich die Elektroautoprämie: Nissan legt 1000 Euro extra drauf, dadurch gibt es insgesamt 5000 Euro Elektro-Bonus.",
                        0,
                        15,
                        200,
                        24,
                        bitmapvar,
                        3);

                bitmapvar = BitmapFactory.decodeResource(getResources(), R.drawable.polo);
                dataSource.createEcarCar(
                        "Polo Blue GT",
                        "Volkswagen",
                        "Strecken wurden mit diesem Auto aufgenommen.",
                        0,
                        6.0,
                        600,
                        0,
                        bitmapvar,
                        1);
            }
            dataSource.close();
            bswitch = true;
        }
    }
    public void reloadswipes(){
        dataSource.open();
        List<EcarCar> templist = dataSource.getCarType(3);
        dataSource.close();
        swad.setSwipelist(templist);
    }
    private void setActivityBackgroundcolor(int color)
    {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(color); //TODO: Bgcolor ändern.
    }
    private void initImageView()//In dieser Methode werden die Bilder gewechselt
    {
        dataSource.open();
        eclist = dataSource.getCarType(3);
        dataSource.close();

        vPager = (ViewPager) findViewById(R.id.pager);
        vPager.setAdapter(swad = new SwipeAdapter(MainActivity.this,eclist));
        vPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                selectedCarId.setivar(position);
            }

            public void onPageSelected(int position) {
                // Check if this is the page you want.
            }
        });
        /*                  Automatische Slideshow - NICHT LÖSCHEN! (Funktioniert) Könnte für den Welcomescreen interresant sein!
        //                  Automatische Slideshow - NICHT LÖSCHEN! (Funktioniert)
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == images.length) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 2500, 2500);*/
    /*ivProfileImage = (ImageView)findViewById(R.id.ivProfileImage);
        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateImage();
            }
        }); */
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Noch leer, da wir noch keine Optionen erstellt haben. //TODO: SETTINGS!
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Navigation
        int id = item.getItemId();
        if (id == R.id.nav_activity1) {
            oeffneMain();
        } else if (id == R.id.nav_Activity2) {
            oeffneMaps();
        } else if (id == R.id.nav_Activity3) {
            shopFragment();
        } else if (id == R.id.nav_Activity4) {
            infoFragment();
            dataSource.open();
            List<EcarCar> ectemp = dataSource.getCarType(3);
            swad.setSwipelist(ectemp);
            swad.notifyDataSetChanged();
            dataSource.close();
        } else if (id == R.id.nav_dayses) {
            openDaySessions();
        } else if (id == R.id.nav_newcar) {
            newcarFragment();
        } else if (id == R.id.nav_settings) {
            openSettings();
        } else if(id == R.id.nav_logout) {
            signOut();
        }

        // else if(id == R.id.nav_ActivityAuswertung) {
        //    evalMaps();
        //}
        else if(id == R.id.nav_ActivityAuswertungNeu)
        {
            evalMapsNeu();
        }else if(id == R.id.nav_ActivityLiveAuswertung)
        {
            oeffneLiveAuswertung();
        }else if(id == R.id.nav_testdata){
            int day = 0;
            //Beginn des Tages wird als Epoch in day gespeichert
            try {
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd 00:00:00 zzz");
                Date date = new Date();
                date = dateFormat.parse(dateFormat.format(date));
                long epoch = date.getTime()/1000;
                day = (int)epoch;
            }catch (Exception ex){}
            //Wenn keine Strecken am heutigen Tag vorhanden sind wird day nicht verändert
            dataSource.open();
            Cursor cur = dataSource.getSessionDay(day);;
            while(cur!= null){
                day = day -86400;
                cur = dataSource.getSessionDay(day);
            }
            newTime = day;
            //Geht solange zurück bis keine Strecken am Tag vorhanden sind.
            dataSource.close();
            long epoch= day;
            final String date = new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date (epoch*1000));
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(MainActivity.this);
            }
            builder.setTitle("Daten einfügen?")
                    .setMessage("Sind Sie sich sicher am "+date+" neue Testwerte zu generieren?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            LatLng ll = new LatLng(51.447996164091094,7.270647883415222);
                            newTime = newTime + 3600; //Start der Strecken um 1 Uhr morgens
                            dataSource.open();
                            Random r = new Random();
                            //Zwischen 3 und 6 Strecken werden generiert mit dem Startpunkt ll
                            createSessions((r.nextInt(8-2)+2),ll, ""+date);
                            dataSource.close();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void oeffneTabAuswertung()
    {
        Intent intent = new Intent(MainActivity.this, AuswertungMain.class);
        startActivity(intent);
    }
    private void oeffneLiveAuswertung()
    {
        Intent intentAuswertung = new Intent(MainActivity.this, LiveAuswertung.class);
        startActivity(intentAuswertung);

    }
    private void oeffneMain()
    {
        Intent intentMain = new Intent(MainActivity.this,MainActivity.class);
        startActivity(intentMain);
    }
    private void oeffneMaps()//Öffnet eine neue Activity(Maps)
    {
        Intent intMaps = new Intent(MainActivity.this,MapsActivity.class);
        startActivity(intMaps);
    }
    private void infoFragment()//Wechselt das relativeLayout zum InfoFragment
    {
        setTitle("Info");
        ifragment = new InfoFragment();//Erstellung des Infofragments
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.rl_Anzeige, ifragment).commit();//Wechsel des Fragments in der Mainklasse(rl-> Relativelayout)
    }
    private void uebersichtFragment()
    {
        setTitle("Übersicht");
        UebersichtFragment fragment = new UebersichtFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.rl_Anzeige, fragment).commit();
    }
    private void shopFragment()//Wechselt das relativeLayout zum ShopFragment
    {
        setTitle("Shop");
        ShopFragment fragment = new ShopFragment();//Erstellung des Shopfragments
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.rl_Anzeige, fragment).commit();//Wechsel des Fragments in der Mainklasse(rl-> Relativelayout)
    }
    private void evalMaps(){
        Intent intMapsEval = new Intent(MainActivity.this,MapsEval.class);
        startActivity(intMapsEval);
    }
    private void evalMapsNeu(){
        Intent intMapsEvalNew = new Intent(MainActivity.this,AuswertungMain.class);
        startActivity(intMapsEvalNew);
    }

    private void openSettings(){
        Intent intSet = new Intent(MainActivity.this,Settings.class);
        startActivity(intSet);
    }

    private void openDaySessions(){
        Intent intDay = new Intent(MainActivity.this, ViewStrecken.class);
        startActivity(intDay);
    }

    private void newcarFragment(){
        setTitle("Neues Auto anlegen");
        CreateCarFragment fragment = new CreateCarFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.rl_Anzeige, fragment).commit();//Wechsel des Fragments in der Mainklasse(rl-> Relativelayout)
    }


    /**
     * Logged den aktuellen User aus und wechselt dann zum Login-Screen
     */
    private void signOut() {
        firebaseAuth.signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }


    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        User u = dataSnapshot.getValue(User.class);
        username.setText(u.getName());
        if (u.getImageUrl() != null) {
            Picasso.with(this).load(u.getImageUrl()).into(ivProfileImage);
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
    }

    public static LatLng travel(LatLng start, double initialBearing, double distance) {
        double bR = Math.toRadians(initialBearing);
        double lat1R = Math.toRadians(start.latitude);
        double lon1R = Math.toRadians(start.longitude);
        double dR = distance / Rworld;

        double a = Math.sin(dR) * Math.cos(lat1R);
        double lat2 = Math.asin(Math.sin(lat1R) * Math.cos(dR) + a * Math.cos(bR));
        double lon2 = lon1R
                + Math.atan2(Math.sin(bR) * a, Math.cos(dR) - Math.sin(lat1R) * Math.sin(lat2));
        return new LatLng(Math.toDegrees(lat2), Math.toDegrees(lon2));
    }
    public static int timeDist(int dist, int speed){
        double speedms = speed *1000 /3600;
        double time = dist/speedms;
        return (int)time;
    }
    public LatLng createData(int sid, LatLng llStart, int points){
        //Startpunkt wird angelegt
        dataSource.createAndUpdateData(llStart.latitude,llStart.longitude,newTime,sid);
        Random r = new Random();
        LatLng llNew = llStart;
        int initbearing = r.nextInt(360);
        for (int i = 0; i<points;i++){
            // Die neuen Punkte liegen immer zwischen 500m-1500m
            // Die Geschwindigkeit wechselt zwischen 30km/h-100km/h
            int dist = r.nextInt(1500-500+1)+500;
            int speed = r.nextInt(100-30+1)+30;
            double ddist = (double)dist;
            llNew = travel(llNew,initbearing,(ddist/1000));
            newTime = newTime + timeDist(dist,speed);
            // Die Zeit zwischen den Punkten wird anhand der Geschwindigkeit und Entfernung ermittelt und aufaddiert.
            // Somit können wir eine Strecke über die Timestamps in der DB simulieren.
            // Damit nicht immer wieder eine 180 Grad drehung volzogen passiert, wird die Richtung geprüft
            // Wenn wir über 180 Grad liegen besteht beim nächsten Wert nur die Chance auf einen Wechsel wenn 100 - 180 generiert wird. (Gleich bei < 180)
            // Somit besteht eine 44,44444% chance einen starken Richtungswechsel zu haben.
            if (initbearing > 180) {
                initbearing = r.nextInt(360 - 150) + 150;
            }else{
                initbearing = r.nextInt(210);
            }

            Log.d("random","NewPos: "+llNew.latitude+","+llNew.longitude);
            Log.d("random","DoubleDist: "+ddist);
            Log.d("random","Random initialBearing: "+initbearing);
            Log.d("random","Random speed: "+speed);
            Log.d("random","Random dist: "+dist);
            Log.d("random","NewTime: "+newTime);
            Log.d("random","---------------------------------------");

            dataSource.createAndUpdateData(llNew.latitude,llNew.longitude,newTime,sid);
        }
        return llNew;
    }
    public void createSessions(int anz, LatLng llstartday, String StreckenDatum){
        EcarSession tempses;
        Random r = new Random();
        int rndmponits;
        LatLng llnewStart = llstartday;
        // Es wird zwischen 30 und 110 Punkten gewählt
        for (int i = 1; i<=anz;i++){
            tempses = dataSource.createEcarSession(1,StreckenDatum+" - GenerierteStrecke "+i);
            rndmponits = r.nextInt(110-30+1)+30;
            llnewStart = createData(tempses.getSesid(),llnewStart, rndmponits); // Ende der letzten Strecke als Start der nächsten Strecke
            newTime = newTime + 120; //Zeit zwischen den Strecken 2 min
        }
    }
}