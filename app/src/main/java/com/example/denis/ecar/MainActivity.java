package com.example.denis.ecar;

import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.denis.ecar.datenbank.EcarData;
import com.example.denis.ecar.datenbank.EcarDataSource;
import com.example.denis.ecar.datenbank.EcarSession;
import com.example.denis.ecar.fragment_Uebersicht.UebersichtFragment;
import com.example.denis.ecar.fragmente_Auto.DataGenerator;
import com.example.denis.ecar.fragmente_Auto.InfoFragment;
import com.example.denis.ecar.fuellmethoden.DataCollector;
import com.example.denis.ecar.liveAuswertung.LiveAuswertung;
import com.example.denis.ecar.login.LoginActivity;
import com.example.denis.ecar.sharedPref.Settings;
import com.example.denis.ecar.sharedPref.User;
import com.example.denis.ecar.swipes.SwipeAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener /*, ValueEventListener, DatabaseReference.CompletionListener */ {
    Button bttn_shop,bttn_socialmedia,bttn_info,bttn_maps;
    DataGenerator dataGenerator;
    DataCollector dataCollector;
    private FirebaseAuth firebaseAuth;
    private User user;
    private ImageView ivProfileImage;
    private TextView etName;

    int images[] = {R.drawable.tesla3,R.drawable.tesla4};
    //TODO: Bilder besorgen, Modellen zuordnen, aus der DB erhalten.
    private ArrayList<Integer> arr_images = new ArrayList<>();
    private static ViewPager vPager;
    private static int currentPage = 0;
    int k=0;

    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    // Einfache Ausgabe über LogTag zum testen ermöglichen
    private EcarDataSource dataSource; // Datenbank

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        init();
        initImageView();
    }
    //Methoden
    private void init()
    {//Initalisierung
        dataGenerator = new DataGenerator();
        dataCollector = new DataCollector();
        uebersichtFragment();
        initImageView();

        firebaseAuth = FirebaseAuth.getInstance();
        user = new User();
        user.setId(firebaseAuth.getCurrentUser().getUid());
        //user.contextDataDB(this);
        etName = (TextView) findViewById(R.id.tvUsername);
    }
    private void setActivityBackgroundcolor(int color)
    {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(color); //TODO: Bgcolor ändern.
    }
    private void initImageView()//In dieser Methode werden die Bilder gewechselt
    {
        for(int i=0;i<images.length;i++)
            arr_images.add(images[i]);

        vPager = (ViewPager) findViewById(R.id.pager);
        vPager.setAdapter(new SwipeAdapter(MainActivity.this,arr_images));
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
        } else if (id == R.id.nav_dbtest) {

            Log.d(LOG_TAG, "Das Datenquellen-Objekt wird angelegt.");
            dataSource = new EcarDataSource(this);
            //Durch .open() wird ebenfals zum Test ein User, sowie die Einträge für das Accelerometer X,Y,Z angelegt.
            dataSource.open();
            //createEcarSession legt die Session in der Datenbank an und gibt ein Objekt 'EcarSession' mit den angelegten werten zurück.
            // Die 1 steht für die UserID, "Testlauf" für den Namen der Session.
            EcarSession carses = dataSource.createEcarSession(1, "Testlauf");
            Toast toast = Toast.makeText(getApplicationContext(), "Session: "+carses.getName()+" angelegt!", Toast.LENGTH_LONG);
            toast.show();
            //createEcarData ist wie createSession aufgebaut.
            //22 ist ein Testwert für einen Dateneintrag. carses.getSesid() gibt die Session vor.
            // Als letzte stelle wird die Id der Values angegeben. Diese wurden in .open() angelegt. (z.B Latitude)
            EcarData cardataLat = dataSource.createEcarData(22,carses.getSesid(),1);
            EcarData cardataLong = dataSource.createEcarData(140,carses.getSesid(),2);
            toast = Toast.makeText(getApplicationContext(), "Werte: Lat "+cardataLat.getData()+" Long "+cardataLong.getData()+" angelegt", Toast.LENGTH_LONG);
            toast.show();
            // getSpecificEcarData gibt alle Daten einer Session oder einer Value in einer Liste von Objekten wieder.
            // Die Übergabewerte stehen hier für (Session, Value). Bei der Eingabe von 0 werden alle ausgegeben.
            // (dataSource.getSpecificEcarData(0, 3); = Daten von jeder Session, jedoch nur Accelerometer Z)
            // (dataSource.getSpecificEcarData(3, 1); = Daten der Session mit der Id = 3 , jedoch nur Accelerometer X)
            // getAllEcarData würde alle Objekte geben.
            List<EcarSession> ecarSessionList = dataSource.getAllEcarSession();
            List<EcarData> ecarDataList = dataSource.getAllEcarData();
            // getAllEcarSession hat den selben Effekt nur für Sessions.
            Log.d(LOG_TAG, cardataLat.toString() +" "+ cardataLong.toString());
            // löschen der Werte
            dataSource.deleteEcarData(cardataLat);
            dataSource.deleteEcarData(cardataLong);
            dataSource.deleteEcarSession(carses);
            toast = Toast.makeText(getApplicationContext(), "Alle Einträge gelöscht!", Toast.LENGTH_LONG);
            toast.show();
            dataSource.close();

        } else if (id == R.id.nav_settings) {
            openSettings();
        } else if(id == R.id.nav_logout) {
            signOut();
        } else if(id == R.id.nav_ActivityAuswertung)
        {
            evalMaps();
        }else if(id == R.id.nav_ActivityLiveAuswertung)
        {
            oeffneLiveAuswertung();
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        InfoFragment fragment = new InfoFragment();//Erstellung des Infofragments
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.rl_Anzeige, fragment).commit();//Wechsel des Fragments in der Mainklasse(rl-> Relativelayout)
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

    private void openSettings(){
        Intent intSet = new Intent(MainActivity.this,Settings.class);
        startActivity(intSet);
    }

/*
    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        if (databaseError != null) {
            Toast.makeText(this, "Fehler: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Aktualisierung wurde erfolgreich durchgeführt.", Toast.LENGTH_SHORT ).show();
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        User u = dataSnapshot.getValue(User.class);
        etName.setText(u.getName());
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }   */


    /**
     * Logged den aktuellen User aus und wechselt dann zum Login-Screen
     */
    private void signOut() {
        firebaseAuth.signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }
}
