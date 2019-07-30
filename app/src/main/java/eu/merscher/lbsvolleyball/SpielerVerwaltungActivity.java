package eu.merscher.lbsvolleyball;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;

public class SpielerVerwaltungActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 1;
    private SpielerDataSource spielerDataSource;
    public static final String LOG_TAG = SpielerVerwaltungActivity.class.getSimpleName();


    private String userFotoAlsString;
    private ImageView spielerBild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spielerverwaltung);
        spielerDataSource = new SpielerDataSource(this);
        showAllListEntries();
        spielerAddButton();

        Toolbar toolbar = findViewById(R.id.activity_spielerverwaltung_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Spielerverwaltung");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initializeContextualActionBar();
        bottomNavBarInitialisieren();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void spielerAddButton() {

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SpielerVerwaltungActivity.this, AddSpielerActivity.class);
                SpielerVerwaltungActivity.this.startActivity(intent);
            }
        });
        showAllListEntries();
    }


    private void bottomNavBarInitialisieren() {

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
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
                }
                return true;
            }
        });
    }

    private void initializeContextualActionBar() {

        final ListView spielerListView = findViewById(R.id.listview_spieler);
        spielerListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        spielerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                spielerDataSource.open();
                ArrayList<Spieler> spielerList = spielerDataSource.getAllSpielerAlphabetischName();
                spielerDataSource.close();

                Spieler spieler = spielerList.get(position);

                Intent data = new Intent(SpielerVerwaltungActivity.this, SpielerseiteActivity.class);
                data.putExtra("spieler", new Gson().toJson(spieler));
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

                spielerDataSource.open();

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

                            }
                        }

                        mode.finish();
                        break;

                    default:
                        returnValue = false;
                        break;
                }
                spielerDataSource.close();
                return returnValue;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                selCount = 0;
            }
        });
    }


    private void showAllListEntries() {

        final ListView spielerListView = findViewById(R.id.listview_spieler);

        spielerDataSource.open();
        ArrayList<Spieler> spielerList = spielerDataSource.getAllSpielerAlphabetischName();
        spielerDataSource.close();
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
}

