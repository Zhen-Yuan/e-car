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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.denis.ecar.datenbank.EcarCar;
import com.example.denis.ecar.datenbank.EcarData;
import com.example.denis.ecar.datenbank.EcarDataSource;
import com.example.denis.ecar.datenbank.EcarSession;
import com.example.denis.ecar.fragment_Uebersicht.UebersichtFragment;
import com.example.denis.ecar.fragmente_Auto.CreateCarFragment;
import com.example.denis.ecar.fragmente_Auto.DataGenerator;
import com.example.denis.ecar.fragmente_Auto.InfoFragment;
import com.example.denis.ecar.fragmente_Auto.SelectedIdVariable;
import com.example.denis.ecar.fuellmethoden.DataCollector;
import com.example.denis.ecar.liveAuswertung.LiveAuswertung;
import com.example.denis.ecar.login.LoginActivity;
import com.example.denis.ecar.swipes.SwipeAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Button bttn_shop,bttn_socialmedia,bttn_info,bttn_maps;
    DataGenerator dataGenerator;
    DataCollector dataCollector;
    private ImageView ivProfileImage;
    public static ViewPager vPager;
    private static int currentPage = 0;
    private Bitmap BmNewcar;
    public InfoFragment ifragment;
    public static SelectedIdVariable selectedCarId;
    public SwipeAdapter swad;
    int k=0;
    private FirebaseAuth firebaseAuth;

    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    // Einfache Ausgabe über LogTag zum testen ermöglichen
    private EcarDataSource dataSource; // Datenbank

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
        init();
    }
    //Methoden
    private void init()
    {//Initalisierung
        BmNewcar = BitmapFactory.decodeResource(getResources(), R.drawable.newhidden);
        dataSource = new EcarDataSource(this);
        InitTestValues();

        /*int width = eclist.get(0).getCarpic().getWidth();
        int height = eclist.get(0).getCarpic().getHeight();
        images = new int[width * height];
        eclist.get(0).getCarpic().getPixels(images, 0, width, 0, 0, width, height);*/

        dataGenerator = new DataGenerator();
        dataCollector = new DataCollector();
        uebersichtFragment();
        initImageView();

        firebaseAuth = FirebaseAuth.getInstance();
        //firebaseDB = UserPref.getFirebaseDB();
        //((TextView)findViewById(R.id.tvUsername)).setText(updateUsername());
        //tvUsername.setText(firebaseAuth.getCurrentUser().getDisplayName());
    }
    private void InitTestValues(){
        dataSource.open();
        if(dataSource.checkCar("Model S 75","Tesla")==null){
            Bitmap bitmaptesla = BitmapFactory.decodeResource(getResources(), R.drawable.tesla3);
            dataSource.createEcarCar("Model S 75","Tesla","Universaler Mobile Connector mit rotem 11 kW-Industriestrom-Adapter (400V, 16A) und 3 kW \"Schuko\"-Steckdosenadapter (230V, 13A)\nZugang zum wachsenden Tesla Supercharger-Netzwerk \n\nInnenansicht: \n17-Zoll-Touchscreen \nBordkarten und Navigation mit Gratis-Updates für 7 Jahre \nSchlüsselloser Zugang \nWiFi- und Mobilfunk-Konnektivität \nFernbedienung über Mobile-App für Smartphones \nTürgriffe mit automatischem Einzug \n\" +Elektrische Fensterheber mit Tastendruck-Automatik \nHD-Rückfahrkamera \nBluetooth-Freisprechsystem \nSprachgesteuerte Funktionen \nAM-, FM-, DAB+ und Internet-Radio \nSpiegel mit Abblendautomatik \nLED-Ambienteleuchten im Innenraum \nBeleuchtete Türgriffe \nElektrisch einklappbare, beheizbare Seitenspiegel mit Positionsspeicher \nZwei USB-Anschlüsse für Mediengeräte und Nebenverbraucher \n12 V-Netzbuchse \nBeheizbare Vordersitze mit 12 elektrischen Verstellfunktionen, Memoryfunktion und Fahrerprofilspeicher \nFrontstauraum (statt sperrigem Motor!), Gepäckraum hinten und 60/40 umklappbare Rücksitze - 894 Liter Stauraum",3,185,0,bitmaptesla,3);
            dataSource.createEcarCar("New Car","Me","Test zum anzeigen selbst angelegter autos",0,150,400,BmNewcar,3);
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

        } else if (id == R.id.nav_newcar) {
            newcarFragment();
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

    private void newcarFragment(){
        setTitle("Neues Auto anlegen");
        CreateCarFragment fragment = new CreateCarFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.rl_Anzeige, fragment).commit();//Wechsel des Fragments in der Mainklasse(rl-> Relativelayout)
    }

    private void updateUsername() {
        //TODO
    }


    private void updateImage() {
        //TODO
    }

    /**
     * Prueft, ob der im Paramenter angegebene Provider mit dem Acc verbunden ist.
     * @param providerId enthaelt den zu pruefenden Provider.
     * @return false, wenn dies nicht der Fall ist und true, wenn es zutrifft
     */
    private boolean isLinked(String providerId) {
        for (UserInfo userInfo: firebaseAuth.getCurrentUser().getProviderData()) {
            if (userInfo.getProviderId().equals(providerId))
                return true;
        }
        return false;
    }


    /**
     * Logged den aktuellen User aus und wechselt dann zum Login-Screen
     */
    private void signOut() {
        firebaseAuth.signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }
}
