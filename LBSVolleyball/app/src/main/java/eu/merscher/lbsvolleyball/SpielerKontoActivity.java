package eu.merscher.lbsvolleyball;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SpielerKontoActivity extends AppCompatActivity implements View.OnClickListener {

    Button buttonAddBuchung;
    EditText editTextAddBuchung;
    ListView listviewBuchungDatum;
    ListView listviewBuchungBetrag;
    ListView listviewKtoSaldoNeu;
    TextView spielername;

    private SQLiteDatabase database;
    private BuchungDbHelper buchungDbHelper;

    private SpielerDataSource spielerDataSource;
    private BuchungDataSource buchungDataSource;
    private Spieler buchungsSpieler;

    private static DecimalFormat df = new DecimalFormat("0.00");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spieler_konto);

        findViewsById();

        spielerDataSource = new SpielerDataSource(this);
        buchungDataSource = new BuchungDataSource(this);

        buchungsSpieler = new Gson().fromJson(getIntent().getStringExtra("buchungSpieler"), Spieler.class);
        spielername.setText(buchungsSpieler.toString());

        showAllListEntries();
        bottomNavBarInitialisieren();
        buttonAddBuchung.setOnClickListener(this);
    }

    public void onClick(View v) {

        buchungDataSource.open();
        addBuchung(buchungsSpieler);
        showAllListEntries();
        editTextAddBuchung.setText("");
    }

    private void findViewsById() {

        buttonAddBuchung = (Button) findViewById(R.id.button_add_buchung);
        editTextAddBuchung = (EditText) findViewById(R.id.editText_buchungsbetrag);
        listviewBuchungDatum = (ListView) findViewById(R.id.listView_buchungen);
        spielername = (TextView) findViewById(R.id.textView_spielername);

    }

    private void bottomNavBarInitialisieren(){

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_spieltag_bottom:
                        Intent intent1 = new Intent(SpielerKontoActivity.this, SpieltagActivity.class);
                        SpielerKontoActivity.this.startActivity(intent1);
                        break;
                    case R.id.action_spielerverwaltung_bottom:
                        Intent intent2 = new Intent(SpielerKontoActivity.this, SpielerVerwaltungActivity.class);
                        SpielerKontoActivity.this.startActivity(intent2);
                        break;
                    case R.id.action_kontoverwaltung_bottom:
                        Intent intent3 = new Intent(SpielerKontoActivity.this, KontenVerwaltungActivity.class);
                        SpielerKontoActivity.this.startActivity(intent3);
                        break;
                }
                return true;
            }
        });
    }

    protected void addBuchung(Spieler buchungSpieler) {

        double bu_btr = Double.parseDouble(df.format(Double.parseDouble(editTextAddBuchung.getText().toString().replace(',', '.'))));
        double kto_saldo_alt;
        double kto_saldo_neu;

        Calendar kalender = Calendar.getInstance();
        SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy");

        if ((TextUtils.isEmpty(Double.toString(bu_btr)))) {
            return;
        }

        if (!buchungDataSource.getAlleBuchungBetragZuSpieler(buchungSpieler).isEmpty()) { //Wenn Buchungen für den Spieler vorhanden sind...

            Buchung buchung = buchungDataSource.getNeusteBuchungZuSpieler(buchungSpieler);

            kto_saldo_alt = buchung.getKto_saldo_neu(); //der vorherige Kto_Saldo_neu ist der neue Kto_Saldo_alt
            kto_saldo_neu = kto_saldo_alt + bu_btr;

            buchungDataSource.createBuchung(buchungSpieler.getU_id(), bu_btr, kto_saldo_alt, kto_saldo_neu, datumsformat.format(kalender.getTime()));

        } else { //Wenn keine Buchung vorhaden ist, ist der Startsaldo 0

            kto_saldo_alt = 0; //der vorherige Kto_Saldo_neu ist nicht vorhanden, da keine vorherige Buchung existiert, daher 0
            kto_saldo_neu = kto_saldo_alt + bu_btr;

            buchungDataSource.createBuchung(buchungSpieler.getU_id(), bu_btr, kto_saldo_alt, kto_saldo_neu, datumsformat.format(kalender.getTime()));

        }

        buchungDataSource.close();
    }


    private void showAllListEntries() {

        ArrayList<Buchung> buchungList = buchungDataSource.getAllBuchungZuSpieler(buchungsSpieler);

        //Liste alle Buchungsdatümer

        SpielerKontoListViewAdapter buchungArrayAdapter = new SpielerKontoListViewAdapter(
                this, buchungList);

        ListView buchungDatumListView = (ListView) findViewById(R.id.listView_buchungen);
        buchungDatumListView.setAdapter(buchungArrayAdapter);


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

        buchungDataSource.open();
        spielerDataSource.open();

    }

    @Override
    protected void onPause() {
        super.onPause();

        buchungDataSource.close();
        spielerDataSource.close();
    }

}
