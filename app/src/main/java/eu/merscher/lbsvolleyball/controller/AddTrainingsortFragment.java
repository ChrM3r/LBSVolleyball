package eu.merscher.lbsvolleyball.controller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.database.DataSource;
import eu.merscher.lbsvolleyball.model.Trainingsort;
import eu.merscher.lbsvolleyball.utilities.Utilities;


public class AddTrainingsortFragment extends Fragment {

    private AddTrainingsortFragmentAdapter adapter;

    public AddTrainingsortFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add_edit_spieler, container, false);
        //activity = this.getActivity();

        adapter = new AddTrainingsortFragmentAdapter(this);
        RecyclerView recyclerView = rootView.findViewById(R.id.fragment_add_edit_spieler_recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return rootView;

    }

    public class AddTrainingsortFragmentAdapter extends RecyclerView.Adapter<AddTrainingsortFragmentAdapter.ViewHolder> {


        private final LayoutInflater inflate;
        private final AddTrainingsortFragment context;
        ViewHolder holder;


        AddTrainingsortFragmentAdapter(AddTrainingsortFragment context) {
            this.inflate = LayoutInflater.from(context.getContext());
            this.context = context;
        }

        @NotNull
        @Override
        public AddTrainingsortFragmentAdapter.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {

            View view = inflate.inflate(R.layout.fragment_add_trainingsort_item, parent, false);
            holder = new AddTrainingsortFragmentAdapter.ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NotNull AddTrainingsortFragmentAdapter.ViewHolder holder, int position) {
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            final EditText editTextName;
            final EditText editTextStrasse;
            final EditText editTextPlz;
            final EditText editTextOrt;
            final Button buttonTrainingsortAnlegen;
            MapView mapView;
            GoogleMap map;


            ViewHolder(final View view) {
                super(view);
                editTextName = view.findViewById(R.id.editText_name_trainingsort_add);
                editTextStrasse = view.findViewById(R.id.editText_strasse_add);
                editTextPlz = view.findViewById(R.id.editText_plz_add);
                editTextOrt = view.findViewById(R.id.editText_ort_add);
                buttonTrainingsortAnlegen = view.findViewById(R.id.fragement_add_trainingsort_button);

                //Trainigsort anlegen
                buttonTrainingsortAnlegen.setOnClickListener(v -> {
                    String name = editTextName.getText().toString();
                    String strasse = editTextStrasse.getText().toString();
                    String plz = editTextPlz.getText().toString();
                    String ort = editTextOrt.getText().toString();


                    if (TextUtils.isEmpty(name)) {
                        editTextName.setError(context.getString(R.string.editText_errorMessage_empty));
                        return;
                    }
                    if (TextUtils.isEmpty(strasse)) {
                        editTextStrasse.setError(context.getString(R.string.editText_errorMessage_empty));
                        return;
                    }

                    if (TextUtils.isEmpty(plz)) {
                        editTextPlz.setError(context.getString(R.string.editText_errorMessage_empty));
                        return;
                    }

                    if (TextUtils.isEmpty(ort)) {
                        editTextOrt.setError(context.getString(R.string.editText_errorMessage_empty));
                        return;
                    }
                    DataSource dataSource = DataSource.getInstance();
                    dataSource.open();

                    Bitmap trainingsortBild;
                    Trainingsort neuerTrainingsort;

                    String adresse = strasse + ", " + plz + " " + ort;

                    LatLng latLngTrainingsort = Utilities.getLocationFromAddress(getContext(), adresse);

                    if (Objects.requireNonNull(latLngTrainingsort).equals(new LatLng(-999, -999))) {

                        trainingsortBild = BitmapFactory.decodeResource(getResources(), R.drawable.avatar_map);
                        String pfad = Utilities.bildTrainingsortSpeichern(getContext(), trainingsortBild);
                        neuerTrainingsort = dataSource.createTrainingsort(name, strasse, plz, ort, pfad, latLngTrainingsort.latitude, latLngTrainingsort.longitude, 0);
                        AddTrainingsortActivity.setTrainingsortFotoAlsString(Utilities.bildNachTrainingsortBenennen(getContext(), neuerTrainingsort));
                        dataSource.updateFotoTrainingsort(neuerTrainingsort, AddTrainingsortActivity.getTrainingsortFotoAlsString());


                        Log.d("Bild ermitteln", "LatLng falsch bzw. auf -999");

                        Toast toast = Toast.makeText(getContext(), "Der Trainingsort wurde angelegt.", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 0);
                        toast.show();

                        Intent intent = new Intent(getContext(), TrainingsortVerwaltungActivity.class);
                        Objects.requireNonNull(getActivity()).finish();
                        startActivity(intent);

                    } else {
                        try {
                            final AtomicReference<Bitmap> reference = new AtomicReference<>();
                            int mMapWidth = 400;
                            int mMapHeight = 400;

                            GoogleMapOptions options = new GoogleMapOptions()
                                    .compassEnabled(false)
                                    .mapToolbarEnabled(false)
                                    .camera(CameraPosition.fromLatLngZoom(latLngTrainingsort, 15))
                                    .liteMode(true);

                            mapView = new MapView(getContext(), options);
                            mapView.onCreate(null);

                            mapView.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(GoogleMap googleMap) {

                                    mapView.measure(View.MeasureSpec.makeMeasureSpec(mMapWidth, View.MeasureSpec.EXACTLY),
                                            View.MeasureSpec.makeMeasureSpec(mMapHeight, View.MeasureSpec.EXACTLY));
                                    mapView.layout(0, 0, mMapWidth, mMapHeight);

                                    googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(Objects.requireNonNull(getContext()), R.raw.style_json));

                                    Log.d("Bild ermitteln", "Map geladen");


                                    googleMap.setOnMapLoadedCallback(() -> {
                                        // Make a snapshot when map's done loading
                                        googleMap.snapshot(newValue -> {
                                            String pfad = Utilities.bildTrainingsortSpeichern(getContext(), newValue);
                                            AddTrainingsortActivity.setTrainingsortFotoAlsString(pfad);

                                            Log.d("Bild ermitteln", "Map Screenshot aufgenommen");
                                            Log.d("Bild ermitteln", "Screen Pfad: " + pfad);


                                            if (AddTrainingsortActivity.getTrainingsortFotoAlsString() != null) {

                                                Trainingsort neuerTrainingsort = dataSource.createTrainingsort(name, strasse, plz, ort, AddTrainingsortActivity.getTrainingsortFotoAlsString(), latLngTrainingsort.latitude, latLngTrainingsort.longitude, 0);
                                                AddTrainingsortActivity.setTrainingsortFotoAlsString(Utilities.bildNachTrainingsortBenennen(getContext(), neuerTrainingsort));
                                                dataSource.updateFotoTrainingsort(neuerTrainingsort, AddTrainingsortActivity.getTrainingsortFotoAlsString());

                                            } else {
                                                Trainingsort neuerTrainingsort = dataSource.createTrainingsort(name, strasse, plz, ort, "avatar_map", latLngTrainingsort.latitude, latLngTrainingsort.longitude, 0);
                                                AddTrainingsortActivity.setTrainingsortFotoAlsString(Utilities.bildNachTrainingsortBenennen(getContext(), neuerTrainingsort));
                                                dataSource.updateFotoTrainingsort(neuerTrainingsort, AddTrainingsortActivity.getTrainingsortFotoAlsString());


                                            }

                                            AddTrainingsortActivity.setTrainingsortFotoAlsString(null);


                                            Toast toast = Toast.makeText(getContext(), "Der Trainingsort wurde angelegt.", Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.BOTTOM, 0, 0);
                                            toast.show();

                                            Intent intent = new Intent(getContext(), TrainingsortVerwaltungActivity.class);
                                            Objects.requireNonNull(getActivity()).finish();
                                            startActivity(intent);
                                        });

                                    });

                                }
                            });


                            //trainingsortBild = Utilities.getMapBildAusURL(Objects.requireNonNull(latLngTrainingsort).latitude, Objects.requireNonNull(latLngTrainingsort).longitude, 500, 500);


                        } catch (Exception e) {
                            e.printStackTrace();
                            AddTrainingsortActivity.setTrainingsortFotoAlsString(null);
                        }

                    }
                });
            }

        }


    }

}
