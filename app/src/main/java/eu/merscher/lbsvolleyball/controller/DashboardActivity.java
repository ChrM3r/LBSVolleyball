package eu.merscher.lbsvolleyball.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.navigation.NavigationView;

import java.text.DecimalFormat;
import java.util.Objects;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.database.DataSource;
import eu.merscher.lbsvolleyball.utilities.Utilities;

public class DashboardActivity extends AppCompatActivity {


    private TextView spieleranzahl_textview;
    private LinearLayout teamkonto_layout;
    private TextView teamkonto_textview;
    private CardView spieltag_cardview;
    private CardView spielerverwaltung_cardview;
    private CardView trainingsuebersicht_cardview;
    private CardView trainingsort_cardview;
    private CardView auswertungen_cardView;
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
                        Intent intent = new Intent(DashboardActivity.this, TrainingVerwaltungActivity.class);
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


    @Override
    protected void onResume() {
        super.onResume();

        //Dashboard-Header Daten abrufen
        datenAktualisieren();

        //Berechtigungen
        Utilities.berechtigungenPruefen(this);

        //Dashboard-Header

        teamkonto_layout.setOnClickListener(v -> {
            Intent intent = new Intent(this, TeamkontoActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivityIfNeeded(intent, 0);
        });

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
            Intent intent = new Intent(this, TrainingVerwaltungActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivityIfNeeded(intent, 0);
        });

        trainingsort_cardview.setOnClickListener(v -> {
            Intent intent = new Intent(this, TrainingsortVerwaltungActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivityIfNeeded(intent, 0);
        });

        auswertungen_cardView.setOnClickListener(v -> {
            Intent intent = new Intent(this, AuswertungenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivityIfNeeded(intent, 0);
        });

        einstellungen_cardview.setOnClickListener(v -> {
            Intent intent = new Intent(this, EinstellungenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivityIfNeeded(intent, 0);
        });
    }

    private void findViewsById() {

        spieleranzahl_textview = findViewById(R.id.dashboard_textview_spieleranzahl);
        teamkonto_layout = findViewById(R.id.dashboard_linLayout_teamkonto);
        teamkonto_textview = findViewById(R.id.dashboard_textview_teamkonto);
        spieltag_cardview = findViewById(R.id.dashboard_cardview_spieltag);
        spielerverwaltung_cardview = findViewById(R.id.dashboard_cardview_spielerverwaltung);
        trainingsuebersicht_cardview = findViewById(R.id.dashboard_cardview_trainingsuebersicht);
        trainingsort_cardview = findViewById(R.id.dashboard_cardview_trainingsortverwaltung);
        auswertungen_cardView = findViewById(R.id.dashboard_cardview_auswertungen);
        einstellungen_cardview = findViewById(R.id.dashboard_cardview_einstellungen);


    }

    private void datenAktualisieren() {

        DataSource.initializeInstance(this);
        DataSource dataSource = DataSource.getInstance();
        dataSource.open();

        int spieleranzahl = dataSource.getAllSpieler().size();

        if (dataSource.getNeusteBuchungZuTeamkonto() != null) {

            double kto_saldo_neu = dataSource.getNeusteBuchungZuTeamkonto().getKto_saldo_neu();
            String ktoSaldoString = df.format(kto_saldo_neu).replace(".", ",");

            if ((ktoSaldoString.equals("-0,00") || ktoSaldoString.equals("0,00")) && (kto_saldo_neu != 0)) {
                teamkonto_textview.setText(getResources().getString(R.string.rund_0));
            } else
                teamkonto_textview.setText(ktoSaldoString);

        } else
            teamkonto_textview.setText(getResources().getString(R.string.betrag_0));


        spieleranzahl_textview.setText(df_einzel.format(spieleranzahl));

    }

}
