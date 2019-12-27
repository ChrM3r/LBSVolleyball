package eu.merscher.lbsvolleyball.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.marcohc.robotocalendar.RobotoCalendarView;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.database.DataSource;
import eu.merscher.lbsvolleyball.model.Spieler;
import eu.merscher.lbsvolleyball.model.Training;
import eu.merscher.lbsvolleyball.model.Trainingsort;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class TrainingUebersichtActivity extends AppCompatActivity implements RobotoCalendarView.RobotoCalendarListener {

    private static boolean shouldExecuteOnResume;
    private static Resources resources;
    private TrainingUebersichtAdapter trainingUebersichtAdapter;
    private RobotoCalendarView calendarView;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private DataSource dataSource;
    private long trainings_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_trainingsuebersicht);

        resources = getResources();
        shouldExecuteOnResume = false;

        dataSource = DataSource.getInstance();
        dataSource.open();

        Toolbar toolbar = findViewById(R.id.toolbar_activity_trainingsuebersicht);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.trainingsuebersicht));

        recyclerView = findViewById(R.id.activity_trainingsuebersicht_recyclerView);
        layoutManager = new LinearLayoutManager(this);


        calendarView = findViewById(R.id.calendarView);
        calendarView.setRobotoCalendarListener(this);
        calendarView.setShortWeekDays(true);
        //calendarView.showDateTitle(true);
        updateCalendarView();
    }


    private void updateCalendarView() {

        //TODO: Vielleicht nur die letzten drei Monate abfragen, wg. Performance.

        //CalenderView Trainingstage markieren

        ArrayList<Training> trainingList = dataSource.getAllTraining();
        ArrayList<String[]> trainigsDtmList = new ArrayList<>();

        if (trainingList.get(0).getDb_id() != -999) {
            for (Training t : trainingList) {

                String[] trainings_dtm = t.getTraining_dtm().split("\\.");
                trainigsDtmList.add(trainings_dtm);
            }

            for (String[] dtm : trainigsDtmList) {

                int jahr = Integer.parseInt(dtm[2]);
                int monat = Integer.parseInt(dtm[1]);
                int tag = Integer.parseInt(dtm[0]);

                Calendar calendar = Calendar.getInstance();

                calendar.set(jahr, monat - 1, tag);

                Date date = calendar.getTime();

                if (calendarView.getDate().getMonth() == date.getMonth() && calendarView.getDate().getYear() == date.getYear()) {

                    calendarView.markCircleImage1(date);
                }
            }
        } else
            System.out.println("Kein Training angelegt.");

        //Spielergeburttage

        ArrayList<Spieler> spielerList = dataSource.getAllSpieler();
        ArrayList<String[]> geburttagsList = new ArrayList<>();

        for (Spieler s : spielerList) {

            String[] geburtstag_dtm = s.getBdate().split("\\.");
            geburttagsList.add(geburtstag_dtm);
        }

        for (String[] dtm : geburttagsList) {

            int jahr = Integer.parseInt(dtm[2]);
            int monat = Integer.parseInt(dtm[1]);
            int tag = Integer.parseInt(dtm[0]);

            Calendar calendar = Calendar.getInstance();

            int jahr_aktuell = calendar.get(Calendar.YEAR);

            //Geburttage der Vergngehiet bis 20 Jahre in die Zukunft
            int zunknft = 20;

            for (int i = jahr; i <= jahr_aktuell + zunknft; i++) {

                calendar.set(i, monat - 1, tag);

                Date date = calendar.getTime();

                if (calendarView.getDate().getMonth() == date.getMonth() && calendarView.getDate().getYear() == date.getYear()) {

                    calendarView.markCircleImage2(date);
                }
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onDayClick(Date date) {


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int jahr = calendar.get(Calendar.YEAR);
        //Plus 1 weil Calendar.Month {0 - 11}
        int monat = calendar.get(Calendar.MONTH) + 1;
        int tag = calendar.get(Calendar.DAY_OF_MONTH);

        ArrayList<Long> trainingIDList = dataSource.getTrainingsIDzuDatum(jahr, monat, tag);
        String[] trainingAuswahlDialog = new String[trainingIDList.size()];

        //Wenn mehrere Trainings am Tag

        if (trainingIDList.size() > 1) {

            for (int i = 0; i < trainingIDList.size(); i++) {

                long id = trainingIDList.get(i);
                String ort = dataSource.getTrainingsortZuTrainingsId(id).getOrt();
                int anzahl_spieler = dataSource.getSpielerZuTrainingsId(id).size();
                String anzeige = "ID: " + id + "; Ort: " + ort + "; Anzahl der Spieler: " + anzahl_spieler;
                trainingAuswahlDialog[i] = anzeige;
            }


            AlertDialog.Builder builder = new AlertDialog.Builder(TrainingUebersichtActivity.this);
            builder.setTitle("Mehrere Trainings am " + tag + "." + monat + "." + jahr + " gefunden! Bitte auswählen.");

            builder.setItems(trainingAuswahlDialog, (dialog, which) -> {
                trainings_id = trainingIDList.get(which);

                if (trainings_id != -999) {
                    double platzkosten = dataSource.getPlatzkostenZuTrainingsId(trainings_id);
                    Trainingsort trainingsort = dataSource.getTrainingsortZuTrainingsId(trainings_id);

                    trainingUebersichtAdapter = new TrainingUebersichtAdapter(getApplicationContext(), trainings_id, platzkosten, trainingsort);
                    recyclerView.setAdapter(trainingUebersichtAdapter);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setHasFixedSize(true);
                    layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

                } else
                    System.out.println("Keine ordnetliche TrainingsID");
            });

            builder.show();

            //Wenn nur ein Training am Tag
        } else {

            if (!trainingIDList.isEmpty()) {
                trainings_id = trainingIDList.get(0);

                if (trainings_id != -999) {
                    double platzkosten = dataSource.getPlatzkostenZuTrainingsId(trainings_id);
                    Trainingsort trainingsort = dataSource.getTrainingsortZuTrainingsId(trainings_id);

                    recyclerView.setAdapter(new TrainingUebersichtAdapter(getApplicationContext(), trainings_id, platzkosten, trainingsort));
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setHasFixedSize(true);
                    layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

                } else
                    System.out.println("Keine ordnetliche TrainingsID");

            } else {
                if (trainingUebersichtAdapter != null)
                    trainingUebersichtAdapter.clear();
            }
        }
    }


    @Override
    public void onDayLongClick(Date date) {
        Toast.makeText(this, "onDayLongClick: " + date, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRightButtonClick() {
        updateCalendarView();

    }

    @Override
    public void onLeftButtonClick() {
        updateCalendarView();

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
        if (shouldExecuteOnResume) {

            updateCalendarView();

        } else
            shouldExecuteOnResume = true;

    }


    public class TrainingUebersichtAdapter extends RecyclerView.Adapter<TrainingUebersichtAdapter.ViewHolder> {


        private final DecimalFormat df = new DecimalFormat("0.00");
        private final long trainings_id;
        private final double platzkosten;
        private final LayoutInflater inflate;
        private Context context;
        private Trainingsort trainingsort;
        private TrainingUebersichtAdapter.ViewHolder holder;
        private ArrayList<Bitmap> spielerFotos = new ArrayList<>();
        private ArrayList<String> spielerNamen = new ArrayList<>();
        private ArrayList<Spieler> spielerList = new ArrayList<>();


        TrainingUebersichtAdapter(Context context, long trainings_id, double platzkosten, Trainingsort trainingsort) {
            this.inflate = LayoutInflater.from(context);
            this.context = context;
            this.trainings_id = trainings_id;
            this.platzkosten = platzkosten;
            this.trainingsort = trainingsort;
        }

        @NotNull
        @Override
        public TrainingUebersichtAdapter.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {

            View view = inflate.inflate(R.layout.fragment_trainingsuebersicht, parent, false);
            return new TrainingUebersichtAdapter.ViewHolder(view);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(TrainingUebersichtAdapter.ViewHolder holder, int position) {

            this.holder = holder;

            // Trainingsteilnehmer

            getSpielerUndSetAdapter();

            // Trainingsort

            holder.trainingsort.setText(trainingsort.getName());

            //Platzkosten

            holder.platzkosten.setText(df.format(platzkosten).replace('.', ','));
            //Map

            if (holder.mapView != null) {
                holder.mapView.onCreate(null);
                holder.mapView.getMapAsync(googleMap -> {

                    MapsInitializer.initialize(context);
                    holder.map = googleMap;
                    setMapLocation(trainingsort.getName(), trainingsort.getLatitude(), trainingsort.getLongitude());
                    holder.map.getUiSettings().setMapToolbarEnabled(false);
                    holder.map.setMapStyle(MapStyleOptions.loadRawResourceStyle(Objects.requireNonNull(context), R.raw.style_json));

                });
            }

            holder.teilnehmerListView.setOnTouchListener((v, event) -> {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            });


            //Spielerseite aus Übersicht starteb
            holder.teilnehmerListView.setOnItemClickListener((parent, view, position1, id) -> new GetSpielerAndStartSpielerseiteAsyncTask(TrainingUebersichtActivity.this, position1, trainings_id).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));
        }

        void clear() {
            int size = spielerList.size();
            spielerList.clear();
            notifyItemRangeRemoved(0, size);
        }

        private void setMapLocation(String name, double lat, double lng) {
            if (holder.map == null) return;

            NamedLocation data = new NamedLocation(name, new LatLng(lat, lng));

            holder.map.addMarker(new MarkerOptions().position(data.location).title(data.name));
            holder.map.animateCamera(CameraUpdateFactory.newLatLngZoom(data.location, 15.0f));
            holder.map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }

        private void getSpielerUndSetAdapter() {

            DataSource dataSource = DataSource.getInstance();
            dataSource.open();

            spielerList.clear();
            spielerNamen.clear();
            spielerFotos.clear();

            spielerList = dataSource.getSpielerZuTrainingsId(trainings_id);

            for (Spieler s : spielerList) {

                Bitmap spielerBildOriginal;

                if (s.getFoto().equals("avatar_m"))
                    spielerBildOriginal = BitmapFactory.decodeResource(resources, R.drawable.avatar_m);
                else {
                    spielerBildOriginal = BitmapFactory.decodeFile(s.getFoto().replace(".png", "_klein.png"));
                }

                if (spielerBildOriginal == null)
                    spielerBildOriginal = BitmapFactory.decodeResource(resources, R.drawable.avatar_m);


                spielerFotos.add(spielerBildOriginal);
                spielerNamen.add(s.getVname() + " " + s.getName());
            }

            holder.teilnehmerListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
            TeilnehmerUebersichtAdapter adapter = new TeilnehmerUebersichtAdapter(spielerList, spielerNamen, spielerFotos, getApplicationContext());
            holder.teilnehmerListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return 1;
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            final TextView trainingsort;
            final TextView platzkosten;
            final ListView teilnehmerListView;

            MapView mapView;
            GoogleMap map;

            ViewHolder(View view) {
                super(view);
                trainingsort = view.findViewById(R.id.fragment_trainingsuebersicht_textView_trainingsort);
                platzkosten = view.findViewById(R.id.fragment_trainingsuebersicht_textView_platzkosten);
                mapView = view.findViewById(R.id.fragment_trainingsuebersicht_mapView);
                teilnehmerListView = view.findViewById(R.id.fragment_trainingsuebersicht_listView);
            }

        }

        public class TeilnehmerUebersichtAdapter extends BaseAdapter implements ListAdapter {

            private final Context context;
            private ArrayList<Spieler> spielerList;
            private ArrayList<String> spielerNamen;
            private ArrayList<Bitmap> spielerFotos;


            TeilnehmerUebersichtAdapter(ArrayList<Spieler> spielerList, ArrayList<String> spielerNamen, ArrayList<Bitmap> spielerFotos, Context context) {
                this.spielerList = spielerList;
                this.spielerNamen = spielerNamen;
                this.spielerFotos = spielerFotos;
                this.context = context;
            }

            @Override
            public int getCount() {
                return spielerNamen.size();
            }

            @Override
            public Object getItem(int pos) {
                return spielerNamen.get(pos);
            }

            @Override
            public long getItemId(int pos) {
                return 0;
            }

            @SuppressLint("InflateParams")
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View view = convertView;
                if (view == null) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.fragment_trainingsuebersicht_list_view, null);
                }

                TextView listSpielerName = view.findViewById(R.id.fragment_trainingsuebersicht_list_view_name);
                listSpielerName.setText(spielerNamen.get(position));

                ImageView spielerBild = view.findViewById(R.id.fragment_trainingsuebersicht_list_view_spielerBild);

                if (spielerList.get(position).getFoto().equals("avatar_m"))
                    spielerBild.setImageResource(R.drawable.avatar_m);
                else
                    spielerBild.setImageBitmap(spielerFotos.get(position));


                return view;

            }


        }

    }

    private static class NamedLocation {

        public final String name;
        final LatLng location;

        NamedLocation(String name, LatLng location) {
            this.name = name;
            this.location = location;
        }
    }

    static class GetSpielerAndStartSpielerseiteAsyncTask extends AsyncTask<Void, Void, ArrayList<Spieler>> {


        final WeakReference<TrainingUebersichtActivity> activityReference;
        private final int position;
        private ArrayList<Spieler> spielerList = new ArrayList<>();
        long id;

        GetSpielerAndStartSpielerseiteAsyncTask(TrainingUebersichtActivity context, int position, long id) {
            activityReference = new WeakReference<>(context);
            this.position = position;
            this.id = id;

        }

        @Override
        protected ArrayList<Spieler> doInBackground(Void... args) {

            DataSource dataSource = DataSource.getInstance();
            dataSource.open();

            spielerList = dataSource.getSpielerZuTrainingsId(id);
            return spielerList;
        }

        @Override
        public void onPostExecute(ArrayList<Spieler> result) {

            TrainingUebersichtActivity activity = activityReference.get();

            if (activity == null || activity.isFinishing()) return;

            spielerList = result;

            Spieler spieler = spielerList.get(position);

            Intent data = new Intent(activity, SpielerseiteActivity.class);
            data.putExtra("spieler", spieler);
            activity.startActivity(data);

        }
    }

}

