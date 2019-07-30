package eu.merscher.lbsvolleyball;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class SpieltagActivity extends AppCompatActivity implements SpieltagActivitySpielerauswahlFragment.OnSpielerClickListenerInFragment {

    private SpielerDataSource spielerDataSource;
    private BuchungDataSource buchungDataSource;

    private static ArrayList<Spieler> spielerList;
    public static ArrayList<Spieler> selectedSpieler = new ArrayList<>();
    private Button buttonAddSpieltag;
    private NumericEditText editTextPlatzkosten;
    private TextView betragJeSpieler;
    private Fragment fragment;
    private FragmentManager fm;
    private static DecimalFormat df = new DecimalFormat("0.00");

    //Statische Methoden
    public static void addSelectedSpieler(Spieler spieler) {
        selectedSpieler.add(spieler);
    }

    public static boolean spielerIstSelected(Spieler spieler) {
        if (selectedSpieler.isEmpty())
            return false;
        else
            return selectedSpieler.contains(spieler);
    }

    public static void uncheckSelectedSpieler(Spieler spieler) {
        selectedSpieler.remove(spieler);
    }

    public static void clearSelectedSpieler() {
        selectedSpieler.clear();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spieltag);

        findViewsById();
        bottomNavBarInitialisieren();

        editTextPlatzkosten.setGravity(Gravity.END);

        spielerDataSource = new SpielerDataSource(this);
        buchungDataSource = new BuchungDataSource(this);

        spielerDataSource.open();
        spielerList = spielerDataSource.getAllSpielerAlphabetischName();
        spielerDataSource.close();


        //Toolbar
        Toolbar toolbar = findViewById(R.id.activity_spieltag_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Spieltag");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Spielerauswahl
        fm = getSupportFragmentManager();
        fragment = new SpieltagActivitySpielerauswahlFragment(spielerList, this);

        fm.beginTransaction().add(R.id.activity_spieltag_spielerauswahl_fragmentContainer, fragment).commit();

        buttonAddSpieltag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSpieltagButtonClick(v);
            }
        });


        //Textformat Platzkosten
        editTextPlatzkosten.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                setBetragJeSpieler();
                formatPlatzkosten();
            }
        });


    }

    private void findViewsById() {

        buttonAddSpieltag = findViewById(R.id.activity_spieltag_button_add_spieltag);
        betragJeSpieler = findViewById(R.id.activity_spieltag_textview_betrag_je_spieler);
        editTextPlatzkosten = findViewById(R.id.activity_spieltag_editText_platzkosten);

    }

    private void bottomNavBarInitialisieren() {

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_spieltag_bottom:
                        Intent intent1 = new Intent(SpieltagActivity.this, SpieltagActivity.class);
                        SpieltagActivity.this.startActivity(intent1);
                        break;
                    case R.id.action_spielerverwaltung_bottom:
                        Intent intent2 = new Intent(SpieltagActivity.this, SpielerVerwaltungActivity.class);
                        SpieltagActivity.this.startActivity(intent2);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onSpielerClickInFragment(Spieler spieler) {

        setBetragJeSpieler();
        editTextPlatzkosten.clearFocus();
        buttonAddSpieltag.requestFocus();
    }

    public void onSpieltagButtonClick(View v) {


        float platzkosten = 0;
        double bu_btr;
        Calendar kalender = Calendar.getInstance();
        SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy");

        if (!editTextPlatzkosten.getText().toString().isEmpty())
            platzkosten = Float.parseFloat(editTextPlatzkosten.getText().toString().replace(',', '.'));

        InputMethodManager inputMethodManager;
        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        if (selectedSpieler.size() > 0) {

            bu_btr = platzkosten / selectedSpieler.size();
            Toast toast = Toast.makeText(this, "Der Spieltag wurde gebucht.", Toast.LENGTH_SHORT);
            toast.show();
            editTextPlatzkosten.setText("");
            betragJeSpieler.setText("0,00");

            //Datenbank-Einträge erzeugen
            for (Spieler s : selectedSpieler) {

                buchungDataSource.open();
                spielerDataSource.open();

                if (s.getHat_buchung_mm().equals("X")) {

                    double kto_saldo_alt = buchungDataSource.getNeusteBuchungZuSpieler(s).getKto_saldo_neu();
                    double kto_saldo_neu = kto_saldo_alt - bu_btr;

                    buchungDataSource.createBuchung(s.getU_id(), -bu_btr, kto_saldo_alt, kto_saldo_neu, datumsformat.format(kalender.getTime()));
                    spielerDataSource.updateTeilnahmenSpieler(s);

                } else {

                    double kto_saldo_neu = -bu_btr;
                    buchungDataSource.createBuchung(s.getU_id(), -bu_btr, 0, kto_saldo_neu, datumsformat.format(kalender.getTime()));
                    spielerDataSource.updateHatBuchungenMM(spielerDataSource.updateTeilnahmenSpieler(s));
                }

                buchungDataSource.close();
                spielerDataSource.close();
            }

            selectedSpieler.clear();
            fm.beginTransaction().replace(R.id.activity_spieltag_spielerauswahl_fragmentContainer, new SpieltagActivitySpielerauswahlFragment(spielerList, this)).commit();
        } else {

            Toast toast = Toast.makeText(this, "Es wurde kein Spieler ausgewählt.", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    public void setBetragJeSpieler() {

        if (!editTextPlatzkosten.getText().toString().isEmpty()) {
            double platzkosten = Double.valueOf(editTextPlatzkosten.getText().toString().replace(',', '.'));
            int anzahl = selectedSpieler.size();

            if (anzahl > 0) {

                double betragDouble = platzkosten / anzahl;
                betragJeSpieler.setText(df.format(betragDouble).replace('.', ','));
            }
        }
    }

    private void formatPlatzkosten() {
        String s = null;

        if (!editTextPlatzkosten.getText().toString().isEmpty())
            s = editTextPlatzkosten.getText().toString();
        if (s != null) {
            if (s.contains(",")) {
                String[] split = s.split(",");

                if (split[1].length() == 1)
                    s += "0";
                else if (split[1].length() == 0)
                    s += "00";
            } else {
                s += ",00";
            }
            editTextPlatzkosten.setText(s);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
