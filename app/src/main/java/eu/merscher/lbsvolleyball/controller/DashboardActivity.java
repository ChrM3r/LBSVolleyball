package eu.merscher.lbsvolleyball.controller;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.navigation.NavigationView;

import java.text.DecimalFormat;
import java.util.Objects;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.database.DataSource;

public class DashboardActivity extends AppCompatActivity {


    private TextView spieleranzahl_textview;
    private TextView teamkonto_textview;
    private CardView spieltag_cardview;
    private CardView spielerverwaltung_cardview;
    private CardView trainingsuebersicht_cardview;
    private CardView trainingsort_cardview;
    private CardView einstellungen_cardview;
    private final DecimalFormat df = new DecimalFormat("0.00");
    private final DecimalFormat df_einzel = new DecimalFormat("0");


    public Context context;
    public Fragment fragment;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //Initialer Einstellungen-Load für spätere Activitya
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        findViewsById();

        //Dashboard-Header Daten abrufen
        datenAktualisieren();

        //Berechtigungen
        berechtigungenPruefen();


        //CardViews im Gittrnetz
        spieltag_cardview.setOnClickListener(v -> {
            Intent intent = new Intent(this, TrainingTunierActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivityIfNeeded(intent, 0);
        });

        spielerverwaltung_cardview.setOnClickListener(v -> {
            Intent intent = new Intent(this, SpielerVerwaltungActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivityIfNeeded(intent, 0);
        });

        trainingsuebersicht_cardview.setOnClickListener(v -> {
            Intent intent = new Intent(this, TrainingUebersichtActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivityIfNeeded(intent, 0);
        });

        trainingsort_cardview.setOnClickListener(v -> {
            Intent intent = new Intent(this, TrainingsortVerwaltungActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivityIfNeeded(intent, 0);
        });

        einstellungen_cardview.setOnClickListener(v -> {
            Intent intent = new Intent(this, EinstellungenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivityIfNeeded(intent, 0);
        });

        //Toolbar
        Toolbar toolbar = findViewById(R.id.dashboard_toolbar);
        drawerLayout = findViewById(R.id.dashboard_drawer_layout);
        navigationView = findViewById(R.id.dashboard_nav_view);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("LBS Volleyball");

        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.spieltag, R.string.spieltag);
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            drawerLayout.addDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();
            navigationView.bringToFront();
            navigationView.setCheckedItem(R.id.nav_dashboard);

            navigationView.setNavigationItemSelectedListener(menuItem -> {
                navigationView.getMenu().findItem(menuItem.getItemId()).setChecked(false);

                switch (menuItem.getItemId()) {

                    case R.id.nav_dashboard: {
                        break;
                    }

                    case R.id.nav_spieltag: {
                        Intent intent = new Intent(this, TrainingTunierActivity.class);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        startActivityIfNeeded(intent, 0);
                        break;
                    }

                    case R.id.nav_spielerverwaltung: {
                        Intent intent = new Intent(DashboardActivity.this, SpielerVerwaltungActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivityIfNeeded(intent, 0);
                        break;
                    }

                    case R.id.nav_einstellungen: {
                        Intent intent = new Intent(DashboardActivity.this, EinstellungenActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivityIfNeeded(intent, 0);
                        break;
                    }

                    case R.id.nav_trainingsortverwatung: {
                        Intent intent = new Intent(DashboardActivity.this, TrainingsortVerwaltungActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivityIfNeeded(intent, 0);
                        break;
                    }

                    case R.id.nav_trainingsverwaltung: {
                        Intent intent = new Intent(DashboardActivity.this, TrainingUebersichtActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivityIfNeeded(intent, 0);
                        break;
                    }
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            });
        }
    }

    private void findViewsById() {

        spieleranzahl_textview = findViewById(R.id.dashboard_textview_spieleranzahl);
        teamkonto_textview = findViewById(R.id.dashboard_textview_teamkonto);
        spieltag_cardview = findViewById(R.id.dashboard_cardview_spieltag);
        spielerverwaltung_cardview = findViewById(R.id.dashboard_cardview_spielerverwaltung);
        trainingsuebersicht_cardview = findViewById(R.id.dashboard_cardview_trainingsuebersicht);
        trainingsort_cardview = findViewById(R.id.dashboard_cardview_trainingsortverwaltung);
        einstellungen_cardview = findViewById(R.id.dashboard_cardview_einstellungen);


    }

    private void datenAktualisieren() {

        DataSource.initializeInstance(this);
        DataSource dataSource = DataSource.getInstance();
        dataSource.open();

        int spieleranzahl = dataSource.getAllSpieler().size();
        double teamkonto;

        if (dataSource.getNeusteBuchungZuTeamkonto() != null)
            teamkonto = dataSource.getNeusteBuchungZuTeamkonto().getKto_saldo_neu();
        else
            teamkonto = 0;

        spieleranzahl_textview.setText(df_einzel.format(spieleranzahl));
        teamkonto_textview.setText(df.format(teamkonto));

    }

    private void berechtigungenPruefen() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        0);
            }

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            0);
                }
            }
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_NETWORK_STATE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_NETWORK_STATE)) {

                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                            0);
                }
            }
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {

                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            0);
                }
            }
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.INTERNET)
                    != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.INTERNET)) {

                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.INTERNET},
                            0);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        datenAktualisieren();
    }
}
