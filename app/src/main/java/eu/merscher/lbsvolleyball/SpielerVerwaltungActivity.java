package eu.merscher.lbsvolleyball;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class SpielerVerwaltungActivity extends AppCompatActivity {

    public static final String LOG_TAG = SpielerVerwaltungActivity.class.getSimpleName();


    private static String userFotoAlsString;
    private static ArrayList<Bitmap> spielerFotos = new ArrayList<>();
    private static ArrayList<String> spielerNamen = new ArrayList<>();
    private static ArrayList<String> spielerGeburtstage = new ArrayList<>();
    private static Resources resources;

    public static void setSpielerFotos(Bitmap b) {
        spielerFotos.add(b);
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

    public static void setSpielerNamen(String s) {
        spielerNamen.add(s);
    }

    public static void setSpielerGeburtstage(String s) {
        spielerGeburtstage.add(s);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spielerverwaltung);
        resources = getResources();
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

    private void initializeContextualActionBar() {

        final ListView spielerListView = findViewById(R.id.listview_spieler);
        spielerListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        spielerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                new GetSpielerAndStartSpielerseiteAsyncTask(SpielerVerwaltungActivity.this, position).execute();
            }
        });
    }

    private void showAllListEntries() {

        new GetSpielerAndSetAdapterAsyncTask(this).execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //spielerDataSource.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //spielerDataSource.close();
    }


    static class GetSpielerAndSetAdapterAsyncTask extends AsyncTask<Void, Void, ArrayList<Spieler>> {


        public WeakReference<SpielerVerwaltungActivity> activityReference;
        private ArrayList<Spieler> spielerList = new ArrayList<>();

        GetSpielerAndSetAdapterAsyncTask(SpielerVerwaltungActivity context) {
            activityReference = new WeakReference<>(context);

        }

        @Override
        protected ArrayList<Spieler> doInBackground(Void... args) {

            SpielerDataSource spielerDataSource = SpielerDataSource.getInstance();
            spielerDataSource.open();

            spielerList = spielerDataSource.getAllSpielerAlphabetischName();
            spielerNamen.clear();
            spielerFotos.clear();
            spielerGeburtstage.clear();

            for (Spieler s : spielerList) {

                Bitmap spielerBildOriginal;
                Bitmap spielerBildScaled;

                if (s.getFoto().equals("avatar_m"))
                    spielerBildOriginal = BitmapFactory.decodeResource(resources, R.drawable.avatar_m);
                else if (s.getFoto().equals("avatar_f"))
                    spielerBildOriginal = BitmapFactory.decodeResource(resources, R.drawable.avatar_f);
                else
                    spielerBildOriginal = BitmapFactory.decodeFile(s.getFoto());

                spielerBildScaled = BitmapScaler.scaleToFitWidth(spielerBildOriginal, 100);

                SpielerVerwaltungActivity.setSpielerFotos(spielerBildScaled);
                SpielerVerwaltungActivity.setSpielerNamen(s.getVname() + " " + s.getName());
                SpielerVerwaltungActivity.setSpielerGeburtstage(s.getBdate());

            }

            return spielerList;
        }

        @Override
        public void onPostExecute(ArrayList<Spieler> result) {

            SpielerVerwaltungActivity activity = activityReference.get();

            if (activity == null || activity.isFinishing()) return;

            final ListView spielerListView = activity.findViewById(R.id.listview_spieler);
            SpielerVerwaltungAdapter adapter = new SpielerVerwaltungAdapter(result, spielerNamen, spielerFotos, spielerGeburtstage, activity.getApplicationContext());
            spielerListView.setAdapter(adapter);
            userFotoAlsString = null;


        }
    }

    static class GetSpielerAndStartSpielerseiteAsyncTask extends AsyncTask<Void, Void, ArrayList<Spieler>> {


        public WeakReference<SpielerVerwaltungActivity> activityReference;
        private int position;
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
            data.putExtra("spieler", new Gson().toJson(spieler));
            activity.startActivity(data);

        }
    }

}

