package eu.merscher.lbsvolleyball.controller;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.database.DataSource;
import eu.merscher.lbsvolleyball.model.Spieler;
import eu.merscher.lbsvolleyball.model.Trainingsort;
import eu.merscher.lbsvolleyball.utilities.Utilities;


public class TrainingVerwaltungActivity extends AppCompatActivity {

    private ArrayList<Long> trainingIDList = new ArrayList<>();

    public void setTrainingIDList(ArrayList<Long> list) {
        trainingIDList = list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainingsverwaltung);


        //Toolbar
        Toolbar toolbar = findViewById(R.id.activity_trainingverwaltung_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.trainingsverwaltung));

        //Spielerlistview
        ListView trainingListView = findViewById(R.id.listview_training);
        //trainingListView.setOnItemClickListener((parent, view, position, id) -> new GetSpielerAndStartSpielerseiteAsyncTask(TrainingVerwaltungActivity.this, position).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));
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
        getTrainingUndSetAdapter();

        //Berechtigungen
        Utilities.berechtigungenPruefen(this);

    }

    private void getTrainingUndSetAdapter() {
        DataSource dataSource = DataSource.getInstance();
        dataSource.open();


        trainingIDList.clear();

        trainingIDList = dataSource.getAllTrainingsID();
        ArrayList<String> trainingsDatumList = new ArrayList<>();
        ArrayList<String> trainingsortNameList = new ArrayList<>();
        ArrayList<Integer> trainingTeilnehmerAnzahl = new ArrayList<>();
        ArrayList<String> trainingBildList = new ArrayList<>();
        for (Long ID : trainingIDList) {
            trainingsDatumList.add(dataSource.getDatumZuTrainingsId(ID));
            trainingsortNameList.add(dataSource.getTrainingsortZuTrainingsId(ID).getName());
            trainingTeilnehmerAnzahl.add(dataSource.getSpielerZuTrainingsId(ID).size());
            trainingBildList.add(dataSource.getTrainingsortZuTrainingsId(ID).getFoto());

        }

        ListView trainingListView = findViewById(R.id.listview_training);
        trainingListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
        TrainingVerwaltungAdapter adapter = new TrainingVerwaltungAdapter(trainingIDList, trainingsDatumList, trainingsortNameList, trainingTeilnehmerAnzahl, trainingBildList, getApplicationContext());
        trainingListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        trainingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showPopup(view, position);
            }
        });
    }


    public void showPopup(View anchorView, int position) {

        View view = getLayoutInflater().inflate(R.layout.popup_trainingsverwaltung, null);

        Dialog popupWindow = new Dialog(this, android.R.style.Theme_Light_NoTitleBar);

        popupWindow.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        popupWindow.setCancelable(true);
        popupWindow.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));

        popupWindow.show();


        RecyclerView recyclerView = view.findViewById(R.id.popup_trainingsverwaltung_recyclerView);
        ImageView closeButton = view.findViewById(R.id.popup_close);

        closeButton.setOnClickListener(v -> popupWindow.cancel());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);


        DataSource dataSource = DataSource.getInstance();
        dataSource.open();


        long trainings_id = trainingIDList.get(position);

        if (trainings_id != -999) {
            double platzkosten = dataSource.getPlatzkostenZuTrainingsId(trainings_id);
            Trainingsort trainingsort = dataSource.getTrainingsortZuTrainingsId(trainings_id);

            recyclerView.setAdapter(new TrainingUebersichtAdapter(getApplicationContext(), trainings_id, platzkosten, trainingsort));
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        }


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

            if (trainingsort.getFoto() != null)
                holder.mapView.setImageBitmap(BitmapFactory.decodeFile(trainingsort.getFoto()));
            else
                holder.mapView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.avatar_map));

            //Teilnehmer
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
            holder.teilnehmerListView.setOnItemClickListener((parent, view, position1, id) -> new GetSpielerAndStartSpielerseiteAsyncTask(context, position1, trainings_id).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));
        }

        void clear() {
            int size = spielerList.size();
            spielerList.clear();
            notifyItemRangeRemoved(0, size);
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
                    spielerBildOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.avatar_m);
                else {
                    spielerBildOriginal = BitmapFactory.decodeFile(s.getFoto().replace(".png", "_klein.png"));
                }

                if (spielerBildOriginal == null)
                    spielerBildOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.avatar_m);


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
            final ImageView mapView;


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


        final WeakReference<Context> activityReference;
        private final int position;
        private ArrayList<Spieler> spielerList = new ArrayList<>();
        long id;

        GetSpielerAndStartSpielerseiteAsyncTask(Context context, int position, long id) {
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

            Context activity = activityReference.get();

            if (activity == null) return;

            spielerList = result;

            Spieler spieler = spielerList.get(position);

            Intent data = new Intent(activity, SpielerseiteActivity.class);
            data.putExtra("spieler", spieler);
            activity.startActivity(data);

        }
    }

}



