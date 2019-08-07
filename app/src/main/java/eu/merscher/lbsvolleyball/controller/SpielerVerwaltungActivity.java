package eu.merscher.lbsvolleyball.controller;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.database.SpielerDataSource;
import eu.merscher.lbsvolleyball.model.Spieler;
import eu.merscher.lbsvolleyball.utilities.BitmapScaler;


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

        new GetSpielerAndSetAdapterAsyncTask(this, spielerListView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


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

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Spielerverwaltung");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

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
            SpielerVerwaltungAdapter adapter = getAdapter();
            spielerListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            new GetSpielerAndSetAdapterAsyncTask(this, spielerListView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else
            shouldExecuteOnResume = true;

    }

    static class GetSpielerAndSetAdapterAsyncTask extends AsyncTask<Void, Void, Void> {


        public final WeakReference<SpielerVerwaltungActivity> activityReference;
        private static ListView spielerListview;

        GetSpielerAndSetAdapterAsyncTask(SpielerVerwaltungActivity context, ListView list) {
            activityReference = new WeakReference<>(context);
            spielerListview = list;
        }

        @Override
        protected Void doInBackground(Void... args) {


            SpielerDataSource spielerDataSource = SpielerDataSource.getInstance();
            spielerDataSource.open();

            spielerList.clear();
            spielerNamen.clear();
            spielerFotos.clear();
            spielerGeburtstage.clear();


            SpielerVerwaltungActivity.setSpielerList(spielerDataSource.getAllSpielerAlphabetischName());


            for (Spieler s : spielerList) {

                Bitmap spielerBildOriginal;
                Bitmap spielerBildScaled;
                Uri uri;

                if (s.getFoto().equals("avatar_m"))
                    spielerBildOriginal = BitmapFactory.decodeResource(resources, R.drawable.avatar_m);
                else if (s.getFoto().equals("avatar_f"))
                    spielerBildOriginal = BitmapFactory.decodeResource(resources, R.drawable.avatar_f);
                else {
                    spielerBildOriginal = BitmapFactory.decodeFile(s.getFoto());
                }

                if (spielerBildOriginal != null)
                    spielerBildScaled = BitmapScaler.scaleToFitWidth(spielerBildOriginal, 100);
                else {
                    spielerBildOriginal = BitmapFactory.decodeResource(resources, R.drawable.avatar_m);
                    spielerBildScaled = BitmapScaler.scaleToFitWidth(spielerBildOriginal, 100);
                }

                SpielerVerwaltungActivity.setSpielerFotos(spielerBildScaled);
                SpielerVerwaltungActivity.setSpielerNamen(s.getVname() + " " + s.getName());
                SpielerVerwaltungActivity.setSpielerGeburtstage(s.getBdate());
            }

            return null;
        }

        @Override
        public void onPostExecute(Void v) {

            SpielerVerwaltungActivity activity = activityReference.get();

            if (activity == null || activity.isFinishing())
                return;


            ListView spielerListView = activity.findViewById(R.id.listview_spieler);
            SpielerVerwaltungAdapter adapter = new SpielerVerwaltungAdapter(spielerList, spielerNamen, spielerFotos, spielerGeburtstage, activity.getApplicationContext());
            spielerListView.setAdapter(adapter);
            SpielerVerwaltungActivity.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            String userFotoAlsString = null;


        }
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

            SpielerDataSource spielerDataSource = SpielerDataSource.getInstance();
            spielerDataSource.open();

            spielerList = spielerDataSource.getAllSpielerAlphabetischName();
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

