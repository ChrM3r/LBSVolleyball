package eu.merscher.lbsvolleyball.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class AuswertungenActivity extends AppCompatActivity {

    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auswertungen);

        context = this;

        //Toolbar
        Toolbar toolbar = findViewById(R.id.activity_auswertungen_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.auswertungen));

        DataSource dataSource = DataSource.getInstance();
        dataSource.open();


        AuswertungenFragmentAdapter adapter = new AuswertungenFragmentAdapter(this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView_auswertungen);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }


    //Zurück-Button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public class AuswertungenFragmentAdapter extends RecyclerView.Adapter<AuswertungenFragmentAdapter.ViewHolder> {


        private final LayoutInflater inflate;
        private DataSource dataSource;
        //Beste Spieler und Trainingsorte Anzahl
        int anzahl = 3;


        AuswertungenFragmentAdapter(Context context) {
            this.inflate = LayoutInflater.from(context);
        }


        @NotNull
        @Override
        public AuswertungenFragmentAdapter.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {

            View view = inflate.inflate(R.layout.fragment_auswertungen, parent, false);
            dataSource = DataSource.getInstance();

            return new ViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NotNull AuswertungenFragmentAdapter.ViewHolder holder, int position) {

            dataSource.open();

            //Beste Spieler und Orte

            holder.getSpielerUndOrteUndSetAdapter();

            //Anzahl Spieler, Training, Trainingsorte
            int anzahl_spieler = dataSource.getAllSpieler().size();
            holder.anzahl_spieler.setText(Integer.toString(anzahl_spieler));

            ArrayList<Long> trainingsIDList = dataSource.getAllTrainingsID();
            int anzahl_training = trainingsIDList.size();
            holder.anzahl_training.setText(Integer.toString(anzahl_training));

            int anzahl_trainingsorte = dataSource.getAllTrainingsort().size();
            holder.anzahl_trainingorte.setText(Integer.toString(anzahl_trainingsorte));


            //Durchschnittliche Anzahl Spieler
            DecimalFormat df = new DecimalFormat("0.0#");
            int summe_spieler = 0;

            for (Long l : trainingsIDList) {
                summe_spieler = summe_spieler + dataSource.getSpielerZuTrainingsId(l).size();
            }
            double durchschnitt = 0;
            if (!(summe_spieler <= 0 || anzahl_training <= 0))

                durchschnitt = (double) summe_spieler / anzahl_training;

            holder.durchschnittliche_anzahl.setText(df.format(durchschnitt).replace(".", ","));

            //OnClick-Listener für Spieler und Trainingsorte
            holder.spieler_meiste_teilnahmen.setOnItemClickListener((adapterView, view, position1, l) -> new GetSpielerAndStartSpielerseiteAsyncTask(context, position1, anzahl).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));
            holder.trainingsorte_meiste_besuche.setOnItemClickListener((adapterView, view, position12, l) -> new GetTrainingsortAndStartTrainingsortAsyncTask(context, position12, anzahl).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));

        }

        @Override
        public int getItemCount() {
            return 1;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            final ListView trainingsorte_meiste_besuche;
            final ListView spieler_meiste_teilnahmen;
            final TextView durchschnittliche_anzahl;
            final TextView anzahl_spieler;
            final TextView anzahl_training;
            final TextView anzahl_trainingorte;


            ViewHolder(final View view) {
                super(view);
                trainingsorte_meiste_besuche = view.findViewById(R.id.auswertungen_meiste_besuche_listView);
                spieler_meiste_teilnahmen = view.findViewById(R.id.auswertungen_meiste_teilnahmen_listView);
                durchschnittliche_anzahl = view.findViewById(R.id.auswertungen_durchschnitt_spieler);
                anzahl_spieler = view.findViewById(R.id.auswertungen_anzahl_spieler);
                anzahl_training = view.findViewById(R.id.auswertungen_anzahl_training);
                anzahl_trainingorte = view.findViewById(R.id.auswertungen_anzahl_trainingsorte);


            }


            private void getSpielerUndOrteUndSetAdapter() {

                DataSource dataSource = DataSource.getInstance();
                dataSource.open();

                ArrayList<Spieler> spielerList;
                ArrayList<Bitmap> spielerFotos = new ArrayList<>();
                ArrayList<String> spielerNamen = new ArrayList<>();
                ArrayList<Trainingsort> trainingsortList;
                ArrayList<Bitmap> trainingsortFotos = new ArrayList<>();


                spielerList = dataSource.getBesteSpieler(anzahl);
                trainingsortList = dataSource.getBesteTrainingsorte(anzahl);

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

                spieler_meiste_teilnahmen.setChoiceMode(ListView.CHOICE_MODE_NONE);
                BesteSpielerAdapter adapter = new BesteSpielerAdapter(spielerList, spielerNamen, spielerFotos, getApplicationContext());
                spieler_meiste_teilnahmen.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                Utilities.setListViewHeightNachInhalt(spieler_meiste_teilnahmen);

                for (Trainingsort t : trainingsortList) {

                    Bitmap trainingsortBildOriginal;

                    if (t.getFoto().equals("avatar_map"))
                        trainingsortBildOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.avatar_map);
                    else {
                        trainingsortBildOriginal = BitmapFactory.decodeFile(t.getFoto().replace(".png", "_klein.png"));
                    }

                    if (trainingsortBildOriginal == null)
                        trainingsortBildOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.avatar_map);


                    trainingsortFotos.add(trainingsortBildOriginal);
                }

                trainingsorte_meiste_besuche.setChoiceMode(ListView.CHOICE_MODE_NONE);
                BesteTrainingsorteAdapter adapter2 = new BesteTrainingsorteAdapter(trainingsortList, trainingsortFotos, getApplicationContext());
                trainingsorte_meiste_besuche.setAdapter(adapter2);
                adapter2.notifyDataSetChanged();
                Utilities.setListViewHeightNachInhalt(trainingsorte_meiste_besuche);

            }


            public class BesteSpielerAdapter extends BaseAdapter implements ListAdapter {

                private final Context context;
                private ArrayList<Spieler> spielerList;
                private ArrayList<String> spielerNamen;
                private ArrayList<Bitmap> spielerFotos;


                BesteSpielerAdapter(ArrayList<Spieler> spielerList, ArrayList<String> spielerNamen, ArrayList<Bitmap> spielerFotos, Context context) {
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

                @SuppressLint({"InflateParams", "SetTextI18n"})
                @Override
                public View getView(final int position, View convertView, ViewGroup parent) {
                    View view = convertView;
                    if (view == null) {
                        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        view = Objects.requireNonNull(inflater).inflate(R.layout.activity_auswertungen_listview, null);
                    }

                    TextView listtName = view.findViewById(R.id.auswertugnen_Name);
                    listtName.setText(spielerNamen.get(position));

                    ImageView listBild = view.findViewById(R.id.auswertungen_Bild);

                    if (spielerList.get(position).getFoto().equals("avatar_m"))
                        listBild.setImageResource(R.drawable.avatar_m);
                    else
                        listBild.setImageBitmap(spielerFotos.get(position));

                    TextView listBesuche = view.findViewById(R.id.auswertungen_Teilnahmen);
                    listBesuche.setText(Integer.toString(spielerList.get(position).getTeilnahmen()));


                    return view;

                }


            }

            public class BesteTrainingsorteAdapter extends BaseAdapter implements ListAdapter {

                private final Context context;
                private ArrayList<Trainingsort> trainingsortList;
                private ArrayList<Bitmap> trainingsortFotos;


                BesteTrainingsorteAdapter(ArrayList<Trainingsort> trainingsortList, ArrayList<Bitmap> trainingsortFotos, Context context) {
                    this.trainingsortList = trainingsortList;
                    this.trainingsortFotos = trainingsortFotos;
                    this.context = context;
                }

                @Override
                public int getCount() {
                    return trainingsortList.size();
                }

                @Override
                public Object getItem(int pos) {
                    return trainingsortList.get(pos);
                }

                @Override
                public long getItemId(int pos) {
                    return 0;
                }

                @SuppressLint({"InflateParams", "SetTextI18n"})
                @Override
                public View getView(final int position, View convertView, ViewGroup parent) {
                    View view = convertView;
                    if (view == null) {
                        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        view = Objects.requireNonNull(inflater).inflate(R.layout.activity_auswertungen_listview, null);
                    }

                    TextView listtName = view.findViewById(R.id.auswertugnen_Name);
                    listtName.setText(trainingsortList.get(position).getName());

                    ImageView listBild = view.findViewById(R.id.auswertungen_Bild);

                    if (trainingsortList.get(position).getFoto().equals("avatar_map"))
                        listBild.setImageResource(R.drawable.avatar_map);
                    else
                        listBild.setImageBitmap(trainingsortFotos.get(position));

                    TextView listBesuche = view.findViewById(R.id.auswertungen_Teilnahmen);
                    listBesuche.setText(Integer.toString(trainingsortList.get(position).getBesuche()));


                    return view;

                }


            }
        }
    }

    static class GetSpielerAndStartSpielerseiteAsyncTask extends AsyncTask<Void, Void, ArrayList<Spieler>> {


        final WeakReference<Context> activityReference;
        private final int position;
        private ArrayList<Spieler> spielerList = new ArrayList<>();
        int anzahl;

        GetSpielerAndStartSpielerseiteAsyncTask(Context context, int position, int anzahl) {
            activityReference = new WeakReference<>(context);
            this.position = position;
            this.anzahl = anzahl;

        }

        @Override
        protected ArrayList<Spieler> doInBackground(Void... args) {

            DataSource dataSource = DataSource.getInstance();
            dataSource.open();

            spielerList = dataSource.getBesteSpieler(anzahl);
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
            data.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(data);

        }
    }

    static class GetTrainingsortAndStartTrainingsortAsyncTask extends AsyncTask<Void, Void, ArrayList<Trainingsort>> {


        final WeakReference<Context> activityReference;
        private final int position;
        private ArrayList<Trainingsort> trainingsortList = new ArrayList<>();
        int anzahl;

        GetTrainingsortAndStartTrainingsortAsyncTask(Context context, int position, int anzahl) {
            activityReference = new WeakReference<>(context);
            this.position = position;
            this.anzahl = anzahl;

        }

        @Override
        protected ArrayList<Trainingsort> doInBackground(Void... args) {

            DataSource dataSource = DataSource.getInstance();
            dataSource.open();

            trainingsortList = dataSource.getBesteTrainingsorte(anzahl);
            return trainingsortList;
        }

        @Override
        public void onPostExecute(ArrayList<Trainingsort> result) {

            Context activity = activityReference.get();

            if (activity == null) return;

            trainingsortList = result;

            Trainingsort trainingsort = trainingsortList.get(position);

            Intent data = new Intent(activity, TrainingsortActivity.class);
            data.putExtra("trainingsort", trainingsort);
            data.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(data);

        }
    }
}
