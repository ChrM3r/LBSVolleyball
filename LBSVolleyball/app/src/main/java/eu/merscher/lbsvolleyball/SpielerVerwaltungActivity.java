package eu.merscher.lbsvolleyball;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Random;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.widget.AbsListView;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;

import com.google.gson.Gson;


public class SpielerVerwaltungActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 1;
    private SpielerDataSource spielerDataSource;
    public static final String LOG_TAG = SpielerVerwaltungActivity.class.getSimpleName();


    private String userFotoAlsString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spielerverwaltung);
        setTitle("Spielerverwaltung");
        spielerDataSource = new SpielerDataSource(this);
        showAllListEntries();
        spielerAddButton();
        initializeContextualActionBar();
        bottomNavBarInitialisieren();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    private void spielerAddButton() {

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                AlertDialog spielerAnlegen = anlegenSpielerDialog();
                spielerAnlegen.show();
            }
        });
        showAllListEntries();
    }


    private void bottomNavBarInitialisieren() {

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_spieltag_bottom:
                        Intent intent1 = new Intent(SpielerVerwaltungActivity.this, SpieltagActivity.class);
                        SpielerVerwaltungActivity.this.startActivity(intent1);
                        break;
                    case R.id.action_spielerverwaltung_bottom:
                        Intent intent2 = new Intent(SpielerVerwaltungActivity.this, SpielerVerwaltungActivity.class);
                        SpielerVerwaltungActivity.this.startActivity(intent2);
                        break;
                    case R.id.action_kontoverwaltung_bottom:
                        Intent intent3 = new Intent(SpielerVerwaltungActivity.this, KontenVerwaltungActivity.class);
                        SpielerVerwaltungActivity.this.startActivity(intent3);
                        break;
                }
                return true;
            }
        });
    }

    private void initializeContextualActionBar() {

        final ListView spielerListView = (ListView) findViewById(R.id.listview_spieler);
        spielerListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        spielerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ArrayList<Spieler> spielerList = spielerDataSource.getAllSpieler();

                Spieler spieler = (Spieler) spielerList.get(position);

                Intent data = new Intent(SpielerVerwaltungActivity.this, SpielerseiteActivity.class);
                data.putExtra("buchungSpieler", new Gson().toJson(spieler));

                System.out.println(new Gson().toJson(spieler));

                SpielerVerwaltungActivity.this.startActivity(data);
            }
        });

        spielerListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            int selCount = 0;

            // In dieser Callback-Methode zählen wir die ausgewählen Listeneinträge mit
            // und fordern ein Aktualisieren der Contextual Action Bar mit invalidate() an
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if (checked) {
                    selCount++;
                } else {
                    selCount--;
                }
                //spielerListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
                String cabTitle = selCount + " " + getString(R.string.cab_checked_string);
                mode.setTitle(cabTitle);
                mode.invalidate();
            }

            // In dieser Callback-Methode legen wir die CAB-Menüeinträge an
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                getMenuInflater().inflate(R.menu.menu_contextual_action_bar, menu);
                return true;
            }

            // In dieser Callback-Methode reagieren wir auf den invalidate() Aufruf
            // Wir lassen das Edit-Symbol verschwinden, wenn mehr als 1 Eintrag ausgewählt ist
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                MenuItem item = menu.findItem(R.id.cab_change);
                if (selCount == 1) {
                    item.setVisible(true);
                } else {
                    item.setVisible(false);
                }

                return true;
            }

            // In dieser Callback-Methode reagieren wir auf Action Item-Klicks
            // Je nachdem ob das Löschen- oder Ändern-Symbol angeklickt wurde
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                boolean returnValue = true;
                SparseBooleanArray touchedSpielerPositions = spielerListView.getCheckedItemPositions();
                ArrayList<Spieler> spielerList = spielerDataSource.getAllSpieler();

                switch (item.getItemId()) {
                    case R.id.cab_delete:
                        for (int i = 0; i < touchedSpielerPositions.size(); i++) {
                            boolean isChecked = touchedSpielerPositions.valueAt(i);
                            if (isChecked) {
                                int postitionInListView = touchedSpielerPositions.keyAt(i);

                                Spieler spieler = spielerList.get(postitionInListView);
                                spielerDataSource.deleteSpieler(spieler);
                            }
                        }
                        showAllListEntries();
                        mode.finish();
                        break;

                    case R.id.cab_change:
                        Log.d(LOG_TAG, "Eintrag ändern");
                        for (int i = 0; i < touchedSpielerPositions.size(); i++) {
                            boolean isChecked = touchedSpielerPositions.valueAt(i);
                            if (isChecked) {

                                int postitionInListView = touchedSpielerPositions.keyAt(i);
                                Spieler spieler = spielerList.get(postitionInListView);

                                AlertDialog editSpielerDialog = editSpielerDialog(spieler);
                                editSpielerDialog.show();
                            }
                        }

                        mode.finish();
                        break;

                    default:
                        returnValue = false;
                        break;
                }
                return returnValue;
            }

            // In dieser Callback-Methode reagieren wir auf das Schließen der CAB
            // Wir setzen den Zähler auf 0 zurück
            @Override
            public void onDestroyActionMode(ActionMode mode) {
                selCount = 0;
            }
        });
    }

    //AlertDialog zum Ändern eines bereits angelegten Spsielers

    private AlertDialog editSpielerDialog(final Spieler spieler) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View dialogsView = inflater.inflate(R.layout.dialog_add_edit_spieler, null);

        final EditText editTextNewName = (EditText) dialogsView.findViewById(R.id.editText_name);
        editTextNewName.setText(String.valueOf(spieler.getName()));

        final EditText editTextNewVname = (EditText) dialogsView.findViewById(R.id.editText_vname);
        editTextNewVname.setText(String.valueOf(spieler.getVname()));

        final EditText editTextNewBdate = (EditText) dialogsView.findViewById(R.id.editText_bdate);
        editTextNewBdate.setText(spieler.getBdate());

        final Button buttonNewFoto = (Button) dialogsView.findViewById(R.id.button_foto);
        buttonNewFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bildAusGalerieAuswaehlen();
            }
        });


        AlertDialog.Builder builder1 = builder.setView(dialogsView)
                .setTitle(R.string.dialog_title)
                .setPositiveButton(R.string.dialog_button_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String name = editTextNewName.getText().toString();
                        String vname = editTextNewVname.getText().toString();
                        String bdate = editTextNewBdate.getText().toString();


                        if (((TextUtils.isEmpty(name)) || (TextUtils.isEmpty(vname)) || (TextUtils.isEmpty(bdate)) && userFotoAlsString == null)) {
                            Log.d(LOG_TAG, "Es wurden keine Daten geändert. Daher Abbruch der Änderung.");
                            return;
                        }

                        Spieler updatedSpieler;

                        if (userFotoAlsString == null)
                            updatedSpieler = spielerDataSource.updateSpieler(spieler.getU_id(), name, vname, bdate, spieler.getTeilnahmen(), spieler.getFoto(), spieler.getHat_buchung_mm());
                        else
                            updatedSpieler = spielerDataSource.updateSpieler(spieler.getU_id(), name, vname, bdate, spieler.getTeilnahmen(), userFotoAlsString, spieler.getHat_buchung_mm());

                        Log.d(LOG_TAG, "Alter Eintrag - ID: " + spieler.getU_id() + " Inhalt: " + spieler.toString());
                        Log.d(LOG_TAG, "Neuer Eintrag - ID: " + spieler.getU_id() + " Inhalt: " + updatedSpieler.toString());

                        showAllListEntries();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_button_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }

    //AlertDialog zum Anlegen eines Spielers

    private AlertDialog anlegenSpielerDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View dialogsView = inflater.inflate(R.layout.dialog_add_edit_spieler, null);

        final EditText editTextName = (EditText) dialogsView.findViewById(R.id.editText_name);
        final EditText editTextVname = (EditText) dialogsView.findViewById(R.id.editText_vname);
        final EditText editTextBdate = (EditText) dialogsView.findViewById(R.id.editText_bdate);
        final Button buttonNewFoto = (Button) dialogsView.findViewById(R.id.button_foto);
        buttonNewFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bildAusGalerieAuswaehlen();
            }
        });

        AlertDialog.Builder builder1 = builder.setView(dialogsView)
                .setTitle(R.string.dialog_title_add)
                .setPositiveButton(R.string.dialog_button_positive_add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String name = editTextName.getText().toString();
                        String vname = editTextVname.getText().toString();
                        String bdate = editTextBdate.getText().toString();


                        if (TextUtils.isEmpty(name)) {
                            editTextName.setError(getString(R.string.editText_errorMessage));
                            return;
                        }
                        if (TextUtils.isEmpty(vname)) {
                            editTextVname.setError(getString(R.string.editText_errorMessage));
                            return;
                        }

                        if (TextUtils.isEmpty(bdate)) {
                            editTextBdate.setError(getString(R.string.editText_errorMessage));
                            return;
                        }
                        Spieler neuerSpieler;
                        // An dieser Stelle schreiben wir die geänderten Daten in die SQLite Datenbank
                        if (userFotoAlsString != null)
                            neuerSpieler = spielerDataSource.createSpieler(name, vname, bdate, 0, userFotoAlsString, null);
                        else {
                            final int random = new Random().nextInt();
                            if (random % 2 == 0)
                                neuerSpieler = spielerDataSource.createSpieler(name, vname, bdate, 0, "avatar_m", null);
                            else
                                neuerSpieler = spielerDataSource.createSpieler(name, vname, bdate, 0, "avatar_f", null);
                        }

                        showAllListEntries();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_button_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        return builder.create();
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

        final ListView spielerListView = (ListView) findViewById(R.id.listview_spieler);

        ArrayList<Spieler> spielerList = spielerDataSource.getAllSpieler();
        ArrayList<String> spielerNamen = new ArrayList<>();
        ArrayList<String> spielerFotos = new ArrayList<>();
        ArrayList<String> spielerGeburtstage = new ArrayList<>();

        for (Spieler s : spielerList) {

            spielerNamen.add(s.getVname() + " " + s.getName());
            spielerFotos.add(s.getFoto());
            spielerGeburtstage.add(s.getBdate());

            userFotoAlsString = null;
        }

        SpielerVerwaltungAdapter adapter = new SpielerVerwaltungAdapter(spielerList, spielerNamen, spielerFotos, spielerGeburtstage, this);
        spielerListView.setAdapter(adapter);


    }

    //Bild aus Galerie auswählen und auf das Ergebnis reagieren

    private void bildAusGalerieAuswaehlen() {


        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    Uri selectedImage = data.getData();

                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                    userFotoAlsString = cursor.getString(columnIndex);

                    cursor.close();
                    break;

            }

    }
}

