package eu.merscher.lbsvolleyball;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class SpieltagActivity extends AppCompatActivity implements SpieltagActivitySpielerauswahlFragment.OnSpielerClickListenerInFragment {

    //DB-Schreib-Aktionen
    public static final int CREATE_SPIELER = 1;
    public static final int DELETE_SPIELER = 2;
    public static final int UPDATE_SPIELER = 3;
    public static final int UPDATE_SPIELER_TEILNAHMEN = 4;
    public static final int UPDATE_SPIELER_FOTO = 5;
    public static final int UPDATE_SPIELER_HATBUCHUNGMM = 6;
    //DB-Lese-Aktionen
    public static final int GET_ALL_SPIELER = 7;
    public static final int GET_ALL_SPIELER_ALPHA_NAME = 8;
    public static final int GET_ALL_SPIELER_ALPHA_VNAME = 9;
    public static final int GET_ALL_SPIELER_TEILNAHME = 10;
    public static ArrayList<Spieler> selectedSpieler = new ArrayList<>();
    public static Resources resources;
    private static ArrayList<Spieler> spielerList;
    private static DecimalFormat df = new DecimalFormat("0.00");
    private SpielerDataSource spielerDataSource;
    private Button buttonAddSpieltag;
    private NumericEditText editTextPlatzkosten;
    private TextView betragJeSpieler;
    private Fragment fragment;
    private FragmentManager fm;
    private BuchungDataSource buchungDataSource;
    private float platzkosten = 0;
    private double bu_btr;

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

        resources = getResources();

        findViewsById();
        bottomNavBarInitialisieren();

        editTextPlatzkosten.setGravity(Gravity.END);

        SpielerDataSource.initializeInstance(this);
        //spielerDataSource = SpielerDataSource.getInstance();

        BuchungDataSource.initializeInstance(this);
        buchungDataSource = BuchungDataSource.getInstance();

//        spielerDataSource.open();
        buchungDataSource.open();

        new SpielerauswahlBefuellenAsyncTask(this).execute();

        //Toolbar
        Toolbar toolbar = findViewById(R.id.activity_spieltag_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Spieltag");


        //Spieltag-Button
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
                Utils.formatNumericEditText(editTextPlatzkosten);
                setBetragJeSpieler();

            }
        });


    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        setBetragJeSpieler();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    Log.d("focus", "touchevent");
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }

        return super.dispatchTouchEvent(event);
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

        Calendar kalender = Calendar.getInstance();
        SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy");

        if (!editTextPlatzkosten.getText().toString().isEmpty())
            platzkosten = Float.parseFloat(editTextPlatzkosten.getText().toString().replace(',', '.'));

        InputMethodManager inputMethodManager;
        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if (getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        if (selectedSpieler.size() > 0 && platzkosten > 0) {

            bu_btr = platzkosten / selectedSpieler.size();

            //Datenbank-Einträge erzeugen


            for (Spieler s : selectedSpieler) {

                new SpieltagBuchenAsyncTask(this, s).execute();
            }

            new SpielerauswahlBefuellenAsyncTask(this).execute();

            Toast toast = Toast.makeText(this, "Der Spieltag wurde gebucht.", Toast.LENGTH_SHORT);
            toast.show();
            editTextPlatzkosten.setText("");
            betragJeSpieler.setText("0,00");

            selectedSpieler.clear();

        } else if (selectedSpieler.size() <= 0) {

            Toast toast = Toast.makeText(this, "Es wurde kein Spieler ausgewählt.", Toast.LENGTH_SHORT);
            toast.show();

        } else {

            Toast toast = Toast.makeText(this, "Die Platzkosten wurden nicht erfasst", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    public void setBetragJeSpieler() {

        if (!editTextPlatzkosten.getText().toString().isEmpty() && editTextPlatzkosten.getText().toString().charAt(0) != ',') {
            double platzkosten = Double.valueOf(editTextPlatzkosten.getText().toString().replace(',', '.'));
            int anzahl = selectedSpieler.size();

            if (anzahl > 0) {

                double betragDouble = platzkosten / anzahl;
                betragJeSpieler.setText(df.format(betragDouble).replace('.', ','));
            }
        } else
            betragJeSpieler.setText("0,00");
    }

    static class SpielerauswahlBefuellenAsyncTask extends AsyncTask<Void, Void, ArrayList<Spieler>> {


        public WeakReference<SpieltagActivity> activityReference;
        private ArrayList<Spieler> spielerList = new ArrayList<>();

        SpielerauswahlBefuellenAsyncTask(SpieltagActivity context) {
            activityReference = new WeakReference<>(context);

        }

        @Override
        protected ArrayList<Spieler> doInBackground(Void... args) {

            SpielerDataSource spielerDataSource = SpielerDataSource.getInstance();
            spielerDataSource.open();

            spielerList = spielerDataSource.getAllSpielerAbsteigendTeilnahme();
            return spielerList;
        }

        @Override
        public void onPostExecute(ArrayList<Spieler> result) {

            SpieltagActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            System.out.println("TESTASYNC");
            spielerList = result;

            if (activity.fragment == null) {
                //Spielerauswahl
                activity.fm = activity.getSupportFragmentManager();
                activity.fragment = new SpieltagActivitySpielerauswahlFragment(spielerList, activity::onSpielerClickInFragment);

                activity.fm.beginTransaction().add(R.id.activity_spieltag_spielerauswahl_fragmentContainer, activity.fragment).commitAllowingStateLoss();
            } else {
                activity.fm.beginTransaction().replace(R.id.activity_spieltag_spielerauswahl_fragmentContainer, new SpieltagActivitySpielerauswahlFragment(spielerList, activity::onSpielerClickInFragment)).commitAllowingStateLoss();

            }

        }
    }

    static class SpieltagBuchenAsyncTask extends AsyncTask<Void, Void, Void> {


        public WeakReference<SpieltagActivity> activityReference;
        private Spieler s;

        SpieltagBuchenAsyncTask(SpieltagActivity context, Spieler s) {
            activityReference = new WeakReference<>(context);
            this.s = s;
        }


        @Override
        protected Void doInBackground(Void... args) {

            SpieltagActivity activity = activityReference.get();
            Calendar kalender = Calendar.getInstance();
            SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy");


            BuchungDataSource buchungDataSource = BuchungDataSource.getInstance();
            buchungDataSource.open();
            SpielerDataSource spielerDataSource = SpielerDataSource.getInstance();
            spielerDataSource.open();

            if (selectedSpieler.size() > 0 && activity.platzkosten > 0) {

                activity.bu_btr = activity.platzkosten / selectedSpieler.size();

                if (s.getHat_buchung_mm() != null) {

                    double kto_saldo_alt = buchungDataSource.getNeusteBuchungZuSpieler(s).getKto_saldo_neu();
                    double kto_saldo_neu = kto_saldo_alt - activity.bu_btr;

                    buchungDataSource.createBuchung(s.getU_id(), -activity.bu_btr, kto_saldo_alt, kto_saldo_neu, datumsformat.format(kalender.getTime()));
                    spielerDataSource.updateTeilnahmenSpieler(s);

                } else {

                    double kto_saldo_neu = -activity.bu_btr;
                    buchungDataSource.createBuchung(s.getU_id(), -activity.bu_btr, 0, kto_saldo_neu, datumsformat.format(kalender.getTime()));
                    spielerDataSource.updateHatBuchungenMM(spielerDataSource.updateTeilnahmenSpieler(s));
                }
            }
            return null;
        }

        @Override
        public void onPostExecute(Void v) {

            System.out.println("TESTASYNC2");

        }

    }
}
