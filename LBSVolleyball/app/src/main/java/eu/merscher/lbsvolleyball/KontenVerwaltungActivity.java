package eu.merscher.lbsvolleyball;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class KontenVerwaltungActivity extends AppCompatActivity{

    private SpielerDataSource spielerDataSource;
    private BuchungDataSource buchungDataSource;

    private SQLiteDatabase database;
    private BuchungDbHelper buchungDbHelper;

    ListView spielerListView;
    Button buttonAddBuchung;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kontenverwaltung);

        findViewsById();
        //generate list
        spielerDataSource = new SpielerDataSource(this);
        ArrayList<Spieler> spielerList = spielerDataSource.getAllSpieler();

        ArrayList<String> spielerNamen = new ArrayList<>();
        for (Spieler s : spielerList) {

            spielerNamen.add(s.getVname() + " " + s.getName());
        }


        //instantiate custom adapter
        KontoAdapter adapter = new KontoAdapter(spielerList, spielerNamen, this);

        //handle listview and assign adapter
        spielerListView.setAdapter(adapter);
        //buttonAddBuchung.setOnClickListener(this);

        bottomNavBarInitialisieren();
    }

    private void bottomNavBarInitialisieren(){

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_spieltag_bottom:
                        Intent intent1 = new Intent(KontenVerwaltungActivity.this, SpieltagActivity.class);
                        KontenVerwaltungActivity.this.startActivity(intent1);
                        break;
                    case R.id.action_spielerverwaltung_bottom:
                        Intent intent2 = new Intent(KontenVerwaltungActivity.this, SpielerVerwaltungActivity.class);
                        KontenVerwaltungActivity.this.startActivity(intent2);
                        break;
                    case R.id.action_kontoverwaltung_bottom:
                        Intent intent3 = new Intent(KontenVerwaltungActivity.this, KontenVerwaltungActivity.class);
                        KontenVerwaltungActivity.this.startActivity(intent3);
                        break;
                }
                return true;
            }
        });
    }


    private void findViewsById() {

        buttonAddBuchung = (Button) findViewById(R.id.add_btn);
        spielerListView = (ListView) findViewById(R.id.listview_kontoverwaltung);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main,menu);
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


    public void onClick(View v) {

        finish();
    }
//    protected AlertDialog createAddBuchungDialog(final Spieler buchungSpieler) {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        LayoutInflater inflater = getLayoutInflater();
//
//
//        View dialogsView = inflater.inflate(R.layout.dialog_add_buchung, null);
//
//        final EditText editTextAddBuchung = (EditText) dialogsView.findViewById(R.id.editText_bdate);
//
//        AlertDialog.Builder builder1 = builder.setView(dialogsView)
//                .setTitle(R.string.dialog_title)
//                .setPositiveButton(R.string.dialog_button_positive, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int id) {
//
//                        double bu_btr = Double.valueOf(editTextAddBuchung.getText().toString());
//                        double kto_saldo_alt;
//                        double kto_saldo_neu;
//
//                        if ((TextUtils.isEmpty(Double.toString(bu_btr)))) {
//                            return;
//                        }
//
//                        String[] columns = {BuchungDbHelper.COLUMN_KTOSLDALT};
//
//                        Cursor cursor = database.query(BuchungDbHelper.TABLE_BUCHUNG_DATA,columns, BuchungDbHelper.COLUMN_UID + "=" + buchungSpieler.getU_id(),
//                                null, null, null, null);
//
//                        if (cursor.getString(cursor.getColumnIndex(BuchungDbHelper.COLUMN_KTOSLDALT)).isEmpty())
//                            kto_saldo_alt = 0;
//                        else
//                            kto_saldo_alt = cursor.getDouble(cursor.getColumnIndex(BuchungDbHelper.COLUMN_KTOSLDALT));
//
//                        kto_saldo_neu = kto_saldo_alt + bu_btr;
//
//                        buchungDataSource.createBuchung(buchungSpieler.getU_id(), bu_btr, kto_saldo_alt, kto_saldo_neu);
//
//                        dialog.dismiss();
//                    }
//                })
//                .setNegativeButton(R.string.dialog_button_negative, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
//
//        return builder.create();
//    }

    @Override
    public void finish() {

    }

    @Override
    protected void onResume(){
        super.onResume();

        spielerDataSource.open();

    }
    @Override
    protected void onPause(){
        super.onPause();

        spielerDataSource.close();
    }
}
