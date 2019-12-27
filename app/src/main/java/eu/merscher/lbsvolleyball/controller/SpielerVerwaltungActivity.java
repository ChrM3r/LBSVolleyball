package eu.merscher.lbsvolleyball.controller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.database.DataSource;
import eu.merscher.lbsvolleyball.model.Spieler;
import eu.merscher.lbsvolleyball.utilities.Utilities;


public class SpielerVerwaltungActivity extends AppCompatActivity {

    private ArrayList<Bitmap> spielerFotos = new ArrayList<>();
    private ArrayList<String> spielerNamen = new ArrayList<>();
    private ArrayList<String> spielerGeburtstage = new ArrayList<>();
    private ArrayList<Spieler> spielerList = new ArrayList<>();

    public void setSpielerFotos(Bitmap b) {
        spielerFotos.add(b);
    }

    public void setSpielerNamen(String s) {
        spielerNamen.add(s);
    }

    public void setSpielerGeburtstage(String s) {
        spielerGeburtstage.add(s);
    }

    public void setSpielerList(ArrayList<Spieler> list) {
        spielerList = list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spielerverwaltung);

        //Add-Spieler-Button
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(view -> {

            Intent intent = new Intent(SpielerVerwaltungActivity.this, AddSpielerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            SpielerVerwaltungActivity.this.startActivity(intent);
        });

        //Toolbar
        Toolbar toolbar = findViewById(R.id.activity_spielerverwaltung_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.spielerverwaltung));

        //Spielerlistview
        ListView spielerListView = findViewById(R.id.listview_spieler);
        spielerListView.setOnItemClickListener((parent, view, position, id) -> new GetSpielerAndStartSpielerseiteAsyncTask(SpielerVerwaltungActivity.this, position).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {

        super.onResume();
        getSpielerUndSetAdapter();

        //Berechtigungen
        Utilities.berechtigungenPruefen(this);

    }

    private void getSpielerUndSetAdapter() {
        DataSource dataSource = DataSource.getInstance();
        dataSource.open();

        spielerList.clear();
        spielerNamen.clear();
        spielerFotos.clear();
        spielerGeburtstage.clear();

        setSpielerList(dataSource.getAllSpielerAlphabetischName());

        for (Spieler s : spielerList) {

            Bitmap spielerBildOriginal;

            if (s.getFoto().equals("avatar_m"))
                spielerBildOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.avatar_m);
            else {
                spielerBildOriginal = BitmapFactory.decodeFile(s.getFoto().replace(".png", "_klein.png"));
            }

            if (spielerBildOriginal == null)
                spielerBildOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.avatar_m);


            setSpielerFotos(spielerBildOriginal);
            setSpielerNamen(s.getVname() + " " + s.getName());
            setSpielerGeburtstage(s.getBdate());

        }

        ListView spielerListView = findViewById(R.id.listview_spieler);
        spielerListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
        SpielerVerwaltungAdapter adapter = new SpielerVerwaltungAdapter(spielerList, spielerNamen, spielerFotos, spielerGeburtstage, getApplicationContext());
        spielerListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    static class GetSpielerAndStartSpielerseiteAsyncTask extends AsyncTask<Void, Void, ArrayList<Spieler>> {


        private final WeakReference<SpielerVerwaltungActivity> activityReference;
        private final int position;
        private ArrayList<Spieler> spielerList = new ArrayList<>();

        GetSpielerAndStartSpielerseiteAsyncTask(SpielerVerwaltungActivity context, int position) {
            activityReference = new WeakReference<>(context);
            this.position = position;

        }

        @Override
        protected ArrayList<Spieler> doInBackground(Void... args) {

            DataSource dataSource = DataSource.getInstance();
            dataSource.open();

            spielerList = dataSource.getAllSpielerAlphabetischName();
            return spielerList;
        }

        @Override
        public void onPostExecute(ArrayList<Spieler> result) {

            SpielerVerwaltungActivity activity = activityReference.get();

            if (activity == null || activity.isFinishing()) return;

            spielerList = result;

            Spieler spieler = spielerList.get(position);

            Intent data = new Intent(activity, SpielerseiteActivity.class);
            data.putExtra("spieler", spieler);
            activity.startActivity(data);

        }
    }

}

