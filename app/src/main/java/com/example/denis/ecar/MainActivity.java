package com.example.denis.ecar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.denis.ecar.StreckenView.ViewStrecken;
import com.example.denis.ecar.auswertungTab.AuswertungMain;
import com.example.denis.ecar.datenbank.EcarCar;
import com.example.denis.ecar.datenbank.EcarDataSource;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity
        implements ValueEventListener, NavigationView.OnNavigationItemSelectedListener {

    Button bttn_shop,bttn_socialmedia,bttn_info,bttn_maps;
    DataCollector dataCollector;
    public static ViewPager vPager;
    public InfoFragment ifragment;
    public static SelectedIdVariable selectedCarId;
    public SwipeAdapter swad;

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
        dataSource.open();
        if(dataSource.checkCar("Model S 75","Tesla")==null){
            Bitmap bitmaptesla = BitmapFactory.decodeResource(getResources(), R.drawable.tesla3);
            dataSource.createEcarCar(
                    "Model S 75",
                    "Tesla",
                    "Universaler Mobile Connector mit rotem 11 kW-Industriestrom-Adapter (400V, 16A) und 3 kW \"Schuko\"-Steckdosenadapter (230V, 13A)\nZugang zum wachsenden Tesla Supercharger-Netzwerk \n\nInnenansicht: \n17-Zoll-Touchscreen \nBordkarten und Navigation mit Gratis-Updates für 7 Jahre \nSchlüsselloser Zugang \nWiFi- und Mobilfunk-Konnektivität \nFernbedienung über Mobile-App für Smartphones \nTürgriffe mit automatischem Einzug \n\" +Elektrische Fensterheber mit Tastendruck-Automatik \nHD-Rückfahrkamera \nBluetooth-Freisprechsystem \nSprachgesteuerte Funktionen \nAM-, FM-, DAB+ und Internet-Radio \nSpiegel mit Abblendautomatik \nLED-Ambienteleuchten im Innenraum \nBeleuchtete Türgriffe \nElektrisch einklappbare, beheizbare Seitenspiegel mit Positionsspeicher \nZwei USB-Anschlüsse für Mediengeräte und Nebenverbraucher \n12 V-Netzbuchse \nBeheizbare Vordersitze mit 12 elektrischen Verstellfunktionen, Memoryfunktion und Fahrerprofilspeicher \nFrontstauraum (statt sperrigem Motor!), Gepäckraum hinten und 60/40 umklappbare Rücksitze - 894 Liter Stauraum",
                    3,
                    185,
                    430,
                    500,
                    bitmaptesla,
                    3);
            Bitmap bmnewcar = BitmapFactory.decodeResource(getResources(),R.drawable.newhidden);
            dataSource.createEcarCar(
                    "New Car",
                    "Me",
                    "Test zum anzeigen selbst angelegter autos",
                    0,
                    150,
                    400,
                    300,
                    bmnewcar,
                    3);
        }
        dataSource.close();
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
        List<EcarCar> eclist = dataSource.getCarType(3);
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
        } else if(id == R.id.nav_ActivityAuswertung) {
            evalMaps();
        }else if(id == R.id.nav_ActivityLiveAuswertung)
        {
            oeffneLiveAuswertung();
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
        Picasso.with(this).load(u.getImageUrl()).into(ivProfileImage);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
    }
}