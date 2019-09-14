package eu.merscher.lbsvolleyball.controller;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.database.DataSource;
import eu.merscher.lbsvolleyball.model.Spieler;


public class SpielerVerwaltungActivity extends AppCompatActivity {

    private static boolean shouldExecuteOnResume;

    public static final String LOG_TAG = SpielerVerwaltungActivity.class.getSimpleName();
    private static ArrayList<Bitmap> spielerFotos = new ArrayList<>();
    private static ArrayList<String> spielerNamen = new ArrayList<>();
    private static ArrayList<String> spielerGeburtstage = new ArrayList<>();
    private static ArrayList<Spieler> spielerList = new ArrayList<>();
    private static Resources resources;
    private static ListView spielerListView;


    public static void setSpielerFotos(Bitmap b) {
        spielerFotos.add(b);
    }

    public static void setSpielerNamen(String s) {
        spielerNamen.add(s);
    }

    public static void setSpielerGeburtstage(String s) {
        spielerGeburtstage.add(s);
    }

    public static void setSpielerList(ArrayList<Spieler> list) {
        spielerList = list;
    }

    public static SpielerVerwaltungAdapter getAdapter() {
        return (SpielerVerwaltungAdapter) spielerListView.getAdapter();
    }

    public static void setAdapter(SpielerVerwaltungAdapter adapter) {
        spielerListView.setAdapter(adapter);
    }


    public static ArrayList<Spieler> getSpielerList() {
        return spielerList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spielerverwaltung);

        resources = getResources();
        shouldExecuteOnResume = false;

        spielerListView = findViewById(R.id.listview_spieler);

        //new GetSpielerAndSetAdapterAsyncTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        getSpielerUndSetAdapter();


        FloatingActionButton fab = findViewById(R.id.floatingActionButton);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SpielerVerwaltungActivity.this, AddSpielerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                SpielerVerwaltungActivity.this.startActivity(intent);
            }
        });

        Toolbar toolbar = findViewById(R.id.activity_spielerverwaltung_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.spielerverwaltung));

        initializeContextualActionBar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void getSpielerUndSetAdapter() {
        DataSource dataSource = DataSource.getInstance();
        dataSource.open();

        spielerList.clear();
        spielerNamen.clear();
        spielerFotos.clear();
        spielerGeburtstage.clear();


        SpielerVerwaltungActivity.setSpielerList(dataSource.getAllSpielerAlphabetischName());

        System.out.println(spielerList + "in AsyncTask");

        for (Spieler s : spielerList) {

            Bitmap spielerBildOriginal;

            if (s.getFoto().equals("avatar_m"))
                spielerBildOriginal = BitmapFactory.decodeResource(resources, R.drawable.avatar_m);
            else {
                spielerBildOriginal = BitmapFactory.decodeFile(s.getFoto().replace(".png", "_klein.png"));
            }

            if (spielerBildOriginal == null)
                spielerBildOriginal = BitmapFactory.decodeResource(resources, R.drawable.avatar_m);


            SpielerVerwaltungActivity.setSpielerFotos(spielerBildOriginal);
            SpielerVerwaltungActivity.setSpielerNamen(s.getVname() + " " + s.getName());
            SpielerVerwaltungActivity.setSpielerGeburtstage(s.getBdate());

        }

        ListView spielerListView = findViewById(R.id.listview_spieler);
        spielerListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
        SpielerVerwaltungAdapter adapter = new SpielerVerwaltungAdapter(spielerList, spielerNamen, spielerFotos, spielerGeburtstage, getApplicationContext());
        spielerListView.setAdapter(adapter);
        SpielerVerwaltungActivity.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void initializeContextualActionBar() {

        final ListView spielerListView = findViewById(R.id.listview_spieler);
        spielerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                new GetSpielerAndStartSpielerseiteAsyncTask(SpielerVerwaltungActivity.this, position).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (shouldExecuteOnResume) {
            getSpielerUndSetAdapter();
        } else
            shouldExecuteOnResume = true;

    }


    static class GetSpielerAndStartSpielerseiteAsyncTask extends AsyncTask<Void, Void, ArrayList<Spieler>> {


        public final WeakReference<SpielerVerwaltungActivity> activityReference;
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

