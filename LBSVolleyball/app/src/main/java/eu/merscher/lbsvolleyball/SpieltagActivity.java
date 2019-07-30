package eu.merscher.lbsvolleyball;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SpieltagActivity extends AppCompatActivity implements View.OnClickListener {

    private SpielerDataSource spielerDataSource;
    private BuchungDataSource buchungDataSource;

    Button buttonAddSpieltag;
    EditText editTextPlatzkosten;
    ListView spielerListView;
    TextView betragJeSpieler;
    ArrayAdapter<Spieler> spielerArrayAdapter;

    private static DecimalFormat df = new DecimalFormat("0.00");

    public static final String LOG_TAG = SpieltagActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spieltag);


        findViewsById();
        bottomNavBarInitialisieren();

        editTextPlatzkosten.setGravity(Gravity.CENTER);

        spielerDataSource = new SpielerDataSource(this);
        buchungDataSource = new BuchungDataSource(this);

        spielerListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        showAllListEntries();

        setBetragJeSpieler();
        buttonAddSpieltag.setOnClickListener(this);
    }

    private void findViewsById() {


        buttonAddSpieltag = (Button) findViewById(R.id.button_add_spieltag);
        editTextPlatzkosten = (EditText) findViewById(R.id.editText_platzkosten);
        spielerListView = (ListView) findViewById(R.id.listview_spielerauswahl);
        betragJeSpieler = (TextView) findViewById(R.id.textview_betrag_je_spieler);
    }

    private void bottomNavBarInitialisieren(){

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
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
                    case R.id.action_kontoverwaltung_bottom:
                        Intent intent3 = new Intent(SpieltagActivity.this, KontenVerwaltungActivity.class);
                        SpieltagActivity.this.startActivity(intent3);
                        break;
                }
                return true;
            }
        });
    }
    public void onClick(View v) {

        float platzkosten = 0;
        double bu_btr;
        ArrayList<Spieler> selectedSpielerFuerBuchung = new ArrayList<Spieler>();
        Calendar kalender = Calendar.getInstance();
        SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy");

        if (editTextPlatzkosten.getText().toString().isEmpty());
        else platzkosten = Float.parseFloat(editTextPlatzkosten.getText().toString().replace(',','.'));

        InputMethodManager inputMethodManager;
        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        SparseBooleanArray selectedSpieler = spielerListView.getCheckedItemPositions();
        int anzahlSelected = 0;
        for (int i = 0; i < selectedSpieler.size(); i++) {
            boolean isChecked = selectedSpieler.valueAt(i);
            if (isChecked) {
                int postitionInListView = selectedSpieler.keyAt(i);
                Spieler spieler = (Spieler) spielerListView.getItemAtPosition(postitionInListView);
                selectedSpielerFuerBuchung.add(spieler);
                anzahlSelected++;
            }
        }
        if (anzahlSelected > 0) {

            bu_btr = platzkosten / anzahlSelected;
            Toast toast = Toast.makeText(this, "Der Spieltag wurde gebucht.", Toast.LENGTH_SHORT);
            toast.show();
            editTextPlatzkosten.setText("");
            betragJeSpieler.setText("0,00");

            //Datenbank-Einträge erzeugen
            for (Spieler s : selectedSpielerFuerBuchung) {
                spielerDataSource.updateTeilnahmenSpieler(s, s.getTeilnahmen() + 1);
                if (!buchungDataSource.getAlleBuchungBetragZuSpieler(s).isEmpty()) {
                    double kto_saldo_alt = buchungDataSource.getNeusteBuchungZuSpieler(s).getKto_saldo_neu();
                    double kto_saldo_neu = kto_saldo_alt - bu_btr;

                    buchungDataSource.createBuchung(s.getU_id(), -bu_btr, kto_saldo_alt, kto_saldo_neu, datumsformat.format(kalender.getTime()));
                } else {
                    double kto_saldo_neu = -bu_btr;
                    buchungDataSource.createBuchung(s.getU_id(), -bu_btr, 0, kto_saldo_neu, datumsformat.format(kalender.getTime()));
                }

            }
        } else {
            Toast toast = Toast.makeText(this, "Es wurde kein Spieler ausgewählt.", Toast.LENGTH_SHORT);
            toast.show();
        }

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

    private void showAllListEntries() {
        List<Spieler> spielerList = spielerDataSource.getAllSpieler();

        ArrayAdapter<Spieler> spielerArrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_multiple_choice,
                spielerList);

        ListView spielerListView = (ListView) findViewById(R.id.listview_spielerauswahl);
        spielerListView.setAdapter(spielerArrayAdapter);
    }

    private void setBetragJeSpieler() {

        final ListView spielerListView = (ListView) findViewById(R.id.listview_spielerauswahl);
        spielerListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        spielerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (!editTextPlatzkosten.getText().toString().isEmpty()) {
                    double platzkosten = Double.valueOf(editTextPlatzkosten.getText().toString().replace(',', '.'));
                    int anzahl = spielerListView.getCheckedItemCount();

                    if (anzahl > 0) {

                        double betragDouble = platzkosten / anzahl;
                        betragJeSpieler.setText(df.format(betragDouble).replace('.', ','));
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(LOG_TAG, "Die Datenquelle wird geöffnet.");
        spielerDataSource.open();
        showAllListEntries();

        Log.d(LOG_TAG, "Folgende Einträge sind in der Datenbank vorhanden:");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(LOG_TAG, "Die Datenquelle wird geschlossen.");
        spielerDataSource.close();
    }
}
