package eu.merscher.lbsvolleyball;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.TextView;


import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class SpielerseiteActivity extends AppCompatActivity implements View.OnClickListener{


    private SpielerDataSource spielerDataSource;
    private BuchungDataSource buchungDataSource;

    private Spieler spieler;
    private ArrayList<Spieler> spielerList = new ArrayList<Spieler>();
    private double kto_saldo_neu;
    private int teilnahmen;
    private ImageView spielerFoto;
    private EditText editTextName;
    private EditText editTextVname;
    private EditText editTextBdate;
    private TextView textViewKtoSaldo;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private androidx.appcompat.widget.Toolbar toolbar;
    private Button buttonKontouebersicht;

    private ArrayList<Buchung> buchungList;


    private static DecimalFormat df = new DecimalFormat("0.00");

    public static final String LOG_TAG = SpieltagActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spielerseite);

        spieler = new Gson().fromJson(getIntent().getStringExtra("buchungSpieler"), Spieler.class);
        setTitle(spieler.getVname() + " " + spieler.getName());

        findViewsById();
        //bottomNavBarInitialisieren();


        spielerDataSource = new SpielerDataSource(this);
        buchungDataSource = new BuchungDataSource(this);

        spielerList.add(spieler);
        buchungList = buchungDataSource.getAllBuchungZuSpieler(spieler);
        if (spieler.getHat_buchung_mm() == null)
            kto_saldo_neu = 0;
        else
            kto_saldo_neu = buchungDataSource.getNeusteBuchungZuSpieler(spieler).getKto_saldo_neu();

        teilnahmen = spieler.getTeilnahmen();
        System.out.println("TEST TEST" + spieler.getTeilnahmen());

        spielerFoto.setImageBitmap(BitmapFactory.decodeFile(spieler.getFoto()));
        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(this, spielerList, buchungList, kto_saldo_neu, teilnahmen, getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        tabLayout = (TabLayout) findViewById(R.id.htab_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void findViewsById() {


        buttonKontouebersicht = (Button) findViewById(R.id.button_add_spieltag);
        viewPager =findViewById(R.id.spielerseite_viewpager);
        spielerFoto = findViewById(R.id.spielerbild_groß);


    }

    private void bottomNavBarInitialisieren(){

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_spieltag_bottom:
                        Intent intent1 = new Intent(SpielerseiteActivity.this, SpieltagActivity.class);
                        SpielerseiteActivity.this.startActivity(intent1);
                        break;
                    case R.id.action_spielerverwaltung_bottom:
                        Intent intent2 = new Intent(SpielerseiteActivity.this, SpielerVerwaltungActivity.class);
                        SpielerseiteActivity.this.startActivity(intent2);
                        break;
                    case R.id.action_kontoverwaltung_bottom:
                        Intent intent3 = new Intent(SpielerseiteActivity.this, KontenVerwaltungActivity.class);
                        SpielerseiteActivity.this.startActivity(intent3);
                        break;
                }
                return true;
            }
        });
    }
    public void onClick(View v) {



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_kontoverwaltung:
                Intent intent1 = new Intent(this, KontenVerwaltungActivity.class);
                this.startActivity(intent1);
                break;
            case R.id.action_spielerverwaltung:
                Intent intent2 = new Intent(this, SpielerVerwaltungActivity.class);
                this.startActivity(intent2);
                break;
            case R.id.action_spieltag:
                Intent intent3 = new Intent(this, SpieltagActivity.class);
                this.startActivity(intent3);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }



    @Override
    protected void onResume() {
        super.onResume();

        Log.d(LOG_TAG, "Die Datenquelle wird geöffnet.");
        spielerDataSource.open();
        Log.d(LOG_TAG, "Folgende Einträge sind in der Datenbank vorhanden:");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(LOG_TAG, "Die Datenquelle wird geschlossen.");
        spielerDataSource.close();
    }



}
