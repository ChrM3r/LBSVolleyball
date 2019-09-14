package eu.merscher.lbsvolleyball.controller;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.database.DataSource;
import eu.merscher.lbsvolleyball.model.Spieler;


public class TrainingUebersichtActivity extends AppCompatActivity {

    public static final String LOG_TAG = TrainingUebersichtActivity.class.getSimpleName();
    /**
     * A list of locations to show in this ListView.
     */
    private static final NamedLocation[] LIST_LOCATIONS = new NamedLocation[]{
            new NamedLocation("BUGA Potsdam", new LatLng(52.4124881, 13.0491085)),
            new NamedLocation("Werder/Havel", new LatLng(52.384314, 12.9088907))
    };
    private static boolean shouldExecuteOnResume;
    private static Resources resources;
    private static ListView spielerListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainingsuebersicht);

        resources = getResources();
        shouldExecuteOnResume = false;

        long trainings_id = getIntent().getLongExtra("trainings_id", -999);
        String trainings_ort = getIntent().getStringExtra("trainings_ort");


        Toolbar toolbar = findViewById(R.id.toolbar_activity_trainingsuebersicht);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.trainingsuebersicht));

        RecyclerView recyclerView = findViewById(R.id.activity_trainingsuebersicht_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setHasFixedSize(true);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        recyclerView.setAdapter(new TrainingUebersichtAdapter(this, trainings_id, trainings_ort));

        recyclerView.setLayoutManager(layoutManager);

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
        } else
            shouldExecuteOnResume = true;

    }

    /**
     * Location represented by a position ({@link com.google.android.gms.maps.model.LatLng} and a
     * name ({@link java.lang.String}).
     */
    private static class NamedLocation {

        public final String name;
        public final LatLng location;

        NamedLocation(String name, LatLng location) {
            this.name = name;
            this.location = location;
        }
    }

    public class TrainingUebersichtAdapter extends RecyclerView.Adapter<TrainingUebersichtAdapter.ViewHolder> implements OnMapReadyCallback {


        private final DecimalFormat df = new DecimalFormat("0.00");
        private final long trainings_id;
        private final String trainings_ort;
        private final LayoutInflater inflate;
        private TrainingUebersichtAdapter.ViewHolder holder;
        private ArrayList<Bitmap> spielerFotos = new ArrayList<>();
        private ArrayList<String> spielerNamen = new ArrayList<>();
        private ArrayList<String> spielerGeburtstage = new ArrayList<>();
        private ArrayList<Spieler> spielerList = new ArrayList<>();


        public TrainingUebersichtAdapter(Context context, long trainings_id, String trainings_ort) {
            this.inflate = LayoutInflater.from(context);
            Context context1 = context;
            this.trainings_id = trainings_id;
            this.trainings_ort = trainings_ort;
        }

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

        public TeilnehmerUebersichtAdapter getAdapter() {
            return (TeilnehmerUebersichtAdapter) spielerListView.getAdapter();
        }

        public void setAdapter(TeilnehmerUebersichtAdapter adapter) {
            holder.teilnehmerListView.setAdapter(adapter);
        }

        @Override
        public TrainingUebersichtAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = inflate.inflate(R.layout.fragment_trainingsuebersicht, parent, false);
            return new TrainingUebersichtAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(TrainingUebersichtAdapter.ViewHolder holder, int position) {

            this.holder = holder;

            // Trainingsteilnehmer


            getSpielerUndSetAdapter();

            // Trainingsort

            holder.trainingsort.setText(trainings_ort);

            //Map

            if (holder.mapView != null) {
                // Initialise the MapView
                holder.mapView.onCreate(null);
                // Set the map ready callback to receive the GoogleMap object
                holder.mapView.getMapAsync(this);
            }

            setMapLocation();
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(getApplicationContext());
            holder.map = googleMap;
            setMapLocation();
            holder.map.getUiSettings().setMapToolbarEnabled(false);

        }


        private void setMapLocation() {
            if (holder.map == null) return;

            NamedLocation data = new NamedLocation("BUGAPotsdam", new LatLng(52.4124881, 13.0491085));

            if (data == null) return;

            // Add a marker for this item and set the camera
            holder.map.moveCamera(CameraUpdateFactory.newLatLngZoom(data.location, 13f));
            holder.map.addMarker(new MarkerOptions().position(data.location));

            // Set the map type back to normal.
            holder.map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }

        private void getSpielerUndSetAdapter() {

            DataSource dataSource = DataSource.getInstance();
            dataSource.open();

            spielerList.clear();
            spielerNamen.clear();
            spielerFotos.clear();
            spielerGeburtstage.clear();


            setSpielerList(dataSource.getSpielerZuTrainingsId(trainings_id));

            for (Spieler s : spielerList) {

                Bitmap spielerBildOriginal;

                if (s.getFoto().equals("avatar_m"))
                    spielerBildOriginal = BitmapFactory.decodeResource(resources, R.drawable.avatar_m);
                else {
                    spielerBildOriginal = BitmapFactory.decodeFile(s.getFoto().replace(".png", "_klein.png"));
                }

                if (spielerBildOriginal == null)
                    spielerBildOriginal = BitmapFactory.decodeResource(resources, R.drawable.avatar_m);


                setSpielerFotos(spielerBildOriginal);
                setSpielerNamen(s.getVname() + " " + s.getName());
                setSpielerGeburtstage(s.getBdate());

            }

            holder.teilnehmerListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
            TeilnehmerUebersichtAdapter adapter = new TeilnehmerUebersichtAdapter(spielerList, spielerNamen, spielerFotos, spielerGeburtstage, getApplicationContext());
            holder.teilnehmerListView.setAdapter(adapter);
            setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return 1;
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            final TextView trainingsort;
            final ListView teilnehmerListView;

            private MapView mapView;
            private GoogleMap map;

            ViewHolder(View view) {
                super(view);
                trainingsort = view.findViewById(R.id.fragment_trainingsuebersicht_textView);
                mapView = view.findViewById(R.id.fragment_trainingsuebersicht_mapView);
                teilnehmerListView = view.findViewById(R.id.fragment_trainingsuebersicht_listView);
            }

        }

        public class TeilnehmerUebersichtAdapter extends BaseAdapter implements ListAdapter {

            private final Context context;
            private ArrayList<Spieler> spielerList = new ArrayList<Spieler>();
            private ArrayList<String> spielerNamen = new ArrayList<String>();
            private ArrayList<String> spielerGeburtstage = new ArrayList<String>();
            private ArrayList<Bitmap> spielerFotos = new ArrayList<Bitmap>();
            private ListView spielerListView;


            public TeilnehmerUebersichtAdapter(ArrayList<Spieler> spielerList, ArrayList<String> spielerNamen, ArrayList<Bitmap> spielerFotos, ArrayList<String> spielerGeburtstage, Context context) {
                this.spielerList = spielerList;
                this.spielerNamen = spielerNamen;
                this.spielerFotos = spielerFotos;
                this.spielerGeburtstage = spielerGeburtstage;
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

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View view = convertView;
                if (view == null) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.fragment_trainingsuebersicht_list_view, null);
                }

                TextView listSpielerName = view.findViewById(R.id.fragment_trainingsuebersicht_list_view_name);
                listSpielerName.setText(spielerNamen.get(position));

                TextView listSpielerGeburtstage = view.findViewById(R.id.fragment_trainingsuebersicht_list_view_bdate);
                listSpielerGeburtstage.setText(spielerGeburtstage.get(position));

                ImageView spielerBild = view.findViewById(R.id.fragment_trainingsuebersicht_list_view_spielerBild);

                if (spielerList.get(position).getFoto().equals("avatar_m"))
                    spielerBild.setImageResource(R.drawable.avatar_m);
                else
                    spielerBild.setImageBitmap(spielerFotos.get(position));

                return view;

            }


        }

    }

}

