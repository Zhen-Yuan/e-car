package com.example.denis.ecar;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Button bttn_shop,bttn_socialmedia,bttn_info,bttn_maps;
    TextView tv_output;
    DataGenerator dataGenerator;
    DataCollector dataCollector;
    ImageView iv_ecar;
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
        initButtonActivity();
        //tv_output.setText(dataGenerator.fillStr(99));
    }
    //Methoden
    private void init()
    {//Initalisierung
        bttn_info = (Button) findViewById(R.id.bttn_info);
        bttn_shop = (Button) findViewById(R.id.bttn_shop);
        bttn_socialmedia = (Button) findViewById(R.id.bttn_socialmedia);
        bttn_maps = (Button) findViewById(R.id.bttn_Maps);
        //tv_output = (TextView) findViewById(R.id.tv_Output);
        dataGenerator = new DataGenerator();
        iv_ecar = (ImageView) findViewById(R.id.iv_ECAR);
        dataCollector = new DataCollector();
        initImageView();
    }
    private void setActivityBackgroundcolor(int color)
    {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(color); //TODO: Bgcolor ändern.
    }
    private void initImageView()//In dieser Methode werden die Bilder gewechselt
    {
        //TODO: ImageView
        iv_ecar.setImageResource(R.drawable.tesla3);
        iv_ecar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                int x = (int) event.getX();
                if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    if(event.getX()>350) //Wenn Berührung auf der x-Achse rechts, oder Links -> Wechsel das Bild dementsprechend TODO: Swipes
                    {
                        iv_ecar.setImageResource(R.drawable.tesla4);
                    }else if(event.getX()<200)
                    {
                        iv_ecar.setImageResource(R.drawable.teslawhite);
                    }else
                    {
                        iv_ecar.setImageResource(R.drawable.tesla3);
                    }
                }
                return dataCollector.setLOG(event);
            }
        });
    }
    private void initButtonActivity()
    {
        bttn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoFragment();
            }
        });
        bttn_maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oeffneMaps();
            }
        });
        bttn_socialmedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: BTTN-SOCIALMEDIA
            }
        });
        bttn_shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shopFragment();
            }
        });
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
            evalMaps();
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

        } else if (id == R.id.nav_about) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
}
