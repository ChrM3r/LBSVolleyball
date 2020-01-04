package eu.merscher.lbsvolleyball.controller;

import android.content.Intent;
import android.content.res.Resources;
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
import eu.merscher.lbsvolleyball.model.Trainingsort;


public class TrainingsortVerwaltungActivity extends AppCompatActivity {

    private static boolean shouldExecuteOnResume;

    private ArrayList<String> trainingsortNamen = new ArrayList<>();
    private ArrayList<Bitmap> trainingsortFotos = new ArrayList<>();
    private ArrayList<Trainingsort> trainingsortList = new ArrayList<>();
    private Resources resources;


    public void setTrainingsortFotos(Bitmap b) {
        trainingsortFotos.add(b);
    }

    public void setTrainingsortNamen(String s) {
        trainingsortNamen.add(s);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainingsortverwaltung);

        resources = getResources();
        shouldExecuteOnResume = false;

        FloatingActionButton fab = findViewById(R.id.floatingActionButton_trainingsort);

        fab.setOnClickListener(view -> {

            Intent intent = new Intent(TrainingsortVerwaltungActivity.this, AddTrainingsortActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            TrainingsortVerwaltungActivity.this.startActivity(intent);
        });

        Toolbar toolbar = findViewById(R.id.activity_trainingsortverwaltung_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.trainingsortverwaltung));

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


    private void getTrainingsorteUndSetAdapter() {
        DataSource dataSource = DataSource.getInstance();
        dataSource.open();

        trainingsortNamen.clear();
        trainingsortFotos.clear();
        trainingsortList.clear();

        trainingsortList = (dataSource.getAllTrainingsort());

        for (Trainingsort t : trainingsortList) {

            Bitmap trainingsortBildOriginal;

            if (t.getFoto().equals("avatar_map"))
                trainingsortBildOriginal = BitmapFactory.decodeResource(resources, R.drawable.avatar_map);
            else {
                trainingsortBildOriginal = BitmapFactory.decodeFile(t.getFoto().replace(".png", "_klein.png"));
            }

            if (trainingsortBildOriginal == null)
                trainingsortBildOriginal = BitmapFactory.decodeResource(resources, R.drawable.avatar_map);


            setTrainingsortFotos(trainingsortBildOriginal);
            setTrainingsortNamen(t.getName());

        }

        ListView trainingsortListView = findViewById(R.id.listview_trainingsort);
        trainingsortListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
        TrainingsortVerwaltungAdapter adapter = new TrainingsortVerwaltungAdapter(trainingsortList, trainingsortNamen, trainingsortFotos, getApplicationContext());
        trainingsortListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void initializeContextualActionBar() {

        final ListView trainingsortListView = findViewById(R.id.listview_trainingsort);
        trainingsortListView.setOnItemClickListener((parent, view, position, id) -> new GetTrainingsortAndStartSTrainingsortseiteAsyncTask(TrainingsortVerwaltungActivity.this, position).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));

    }

    @Override
    public void onResume() {
        super.onResume();
        getTrainingsorteUndSetAdapter();
    }


    static class GetTrainingsortAndStartSTrainingsortseiteAsyncTask extends AsyncTask<Void, Void, ArrayList<Trainingsort>> {


        private final WeakReference<TrainingsortVerwaltungActivity> activityReference;
        private final int position;
        private ArrayList<Trainingsort> trainingsortList = new ArrayList<>();

        GetTrainingsortAndStartSTrainingsortseiteAsyncTask(TrainingsortVerwaltungActivity context, int position) {
            activityReference = new WeakReference<>(context);
            this.position = position;

        }

        @Override
        protected ArrayList<Trainingsort> doInBackground(Void... args) {

            DataSource dataSource = DataSource.getInstance();
            dataSource.open();

            trainingsortList = dataSource.getAllTrainingsort();
            return trainingsortList;
        }

        @Override
        public void onPostExecute(ArrayList<Trainingsort> result) {

            TrainingsortVerwaltungActivity activity = activityReference.get();

            if (activity == null || activity.isFinishing()) return;

            trainingsortList = result;

            Trainingsort trainingsort = trainingsortList.get(position);

            Intent data = new Intent(activity, TrainingsortActivity.class);
            data.putExtra("trainingsort", trainingsort);
            activity.startActivity(data);

        }
    }

}

