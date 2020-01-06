package eu.merscher.lbsvolleyball.controller;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Objects;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.database.DataSource;
import eu.merscher.lbsvolleyball.model.Trainingsort;
import eu.merscher.lbsvolleyball.utilities.Utilities;


public class EditTrainingsortFragment extends Fragment {

    private final Trainingsort trainingsort;
    private EditTrainingsortFragment.OnEditFinish onEditFinish;

    EditTrainingsortFragmentAdapter adapter;


    EditTrainingsortFragment(Trainingsort trainingsort) {
        this.trainingsort = trainingsort;
    }

    public EditTrainingsortFragmentAdapter getAdapter() {
        return adapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add_edit_spieler, container, false);

        onEditFinish = EditTrainingsortActivity.getOnEditFinish();


        adapter = new EditTrainingsortFragmentAdapter(getActivity(), trainingsort);
        RecyclerView recyclerView = rootView.findViewById(R.id.fragment_add_edit_spieler_recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return rootView;

    }

    public void onSpeichernClick() {

        Objects.requireNonNull(getActivity()).finish();
    }

    public void onLoeschenClick() {
        Objects.requireNonNull(getActivity()).finish();
        onEditFinish.onEditFinish();

    }
    public interface OnEditFinish {
        void onEditFinish();
    }


    public class EditTrainingsortFragmentAdapter extends RecyclerView.Adapter<EditTrainingsortFragmentAdapter.ViewHolder> {

        private final LayoutInflater inflate;
        private final Context context;
        private final Trainingsort trainingsort;
        ViewHolder holder;

        EditTrainingsortFragmentAdapter(Context context, Trainingsort trainingsort) {
            this.inflate = LayoutInflater.from(context);
            this.context = context;
            this.trainingsort = trainingsort;
        }


        @NotNull
        @Override
        public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {

            View view = inflate.inflate(R.layout.fragment_edit_trainingsort_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
            this.holder = holder;
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
            final Button button_loeschen;
            MapView mapView;

            ViewHolder(final View view) {
                super(view);
                editTextName = view.findViewById(R.id.editText_name_trainingsort_edit);
                editTextStrasse = view.findViewById(R.id.editText_strasse_edit);
                editTextPlz = view.findViewById(R.id.editText_plz_edit);
                editTextOrt = view.findViewById(R.id.editText_ort_edit);
                button_loeschen = view.findViewById(R.id.fragment_edit_trainingsort_loeschen_button);


                editTextName.setText(trainingsort.getName());
                editTextStrasse.setText(trainingsort.getStrasse());
                editTextPlz.setText(trainingsort.getPlz());
                editTextOrt.setText(trainingsort.getOrt());

                button_loeschen.setOnClickListener(v -> {

                    DataSource dataSource = DataSource.getInstance();
                    dataSource.open();


                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);

                    dialog.setTitle("Achtung")
                            .setMessage(String.format(context.getResources().getString(R.string.dialogMessage_to_wirklichLoeschen), trainingsort.getName()))
                            .setNegativeButton("Abbrechen", (dialog12, which) -> dialog12.cancel())
                            .setPositiveButton("Ja", (dialog1, i) -> {
                                new TrainingsortLoeschenAsyncTask(context, trainingsort).execute();
                                onLoeschenClick();
                                TrainingsortActivity.getOnLoeschenClick().onLoeschenClick();

                            }).show();

                });

            }

            void onTrainingsortSpeichernClick() {

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

                //Wenn nichts geändert
                if ((trainingsort.getName().equals(name) && trainingsort.getOrt().equals(ort) && trainingsort.getPlz().equals(plz) && trainingsort.getStrasse().equals(strasse))) {
                    Intent data = new Intent(context, TrainingsortActivity.class);
                    data.putExtra("trainingsort", trainingsort);
                    context.startActivity(data);
                }

                //Wenn Daten geändert
                if (!trainingsort.getName().equals(name) || !trainingsort.getOrt().equals(ort) || !trainingsort.getPlz().equals(plz) || !trainingsort.getStrasse().equals(strasse)) {

                    DataSource dataSource = DataSource.getInstance();
                    dataSource.open();


                    Bitmap trainingsortBild;
                    Trainingsort updatedTrainingsort;

                    String adresse = strasse + ", " + plz + " " + ort;

                    LatLng latLngTrainingsort = Utilities.getLocationFromAddress(context, adresse);

                    if (Objects.requireNonNull(latLngTrainingsort).equals(new LatLng(-999, -999))) {

                        trainingsortBild = BitmapFactory.decodeResource(context.getResources(), R.drawable.avatar_map);
                        String pfad = Utilities.bildTrainingsortSpeichern(context, trainingsortBild);
                        updatedTrainingsort = dataSource.updateTrainingsort(trainingsort.getTo_id(), name, strasse, plz, ort, pfad, latLngTrainingsort.latitude, latLngTrainingsort.longitude, 0);

                        AddTrainingsortActivity.setTrainingsortFotoAlsString(Utilities.bildNachTrainingsortBenennen(context, updatedTrainingsort));
                        dataSource.updateFotoTrainingsort(updatedTrainingsort, AddTrainingsortActivity.getTrainingsortFotoAlsString());


                        Log.d("Bild ermitteln", "LatLng falsch bzw. auf -999");

                        Toast toast = Toast.makeText(context, "Der Trainingsort wurde angelegt.", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 0);
                        toast.show();

                        Intent intent = new Intent(context, TrainingsortActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("trainingsort", updatedTrainingsort);
                        context.startActivity(intent);
                        Objects.requireNonNull(getActivity()).finish();

                    } else {
                        try {
                            int mMapWidth = 400;
                            int mMapHeight = 400;

                            GoogleMapOptions options = new GoogleMapOptions()
                                    .compassEnabled(false)
                                    .mapToolbarEnabled(false)
                                    .camera(CameraPosition.fromLatLngZoom(new LatLng(trainingsort.getLatitude(), trainingsort.getLongitude()), 15))
                                    .liteMode(true);

                            mapView = new MapView(context, options);
                            mapView.onCreate(null);

                            mapView.getMapAsync(googleMap -> {

                                mapView.measure(View.MeasureSpec.makeMeasureSpec(mMapWidth, View.MeasureSpec.EXACTLY),
                                        View.MeasureSpec.makeMeasureSpec(mMapHeight, View.MeasureSpec.EXACTLY));
                                mapView.layout(0, 0, mMapWidth, mMapHeight);

                                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(Objects.requireNonNull(context), R.raw.style_json));

                                Log.d("Bild ermitteln", "Map geladen");


                                googleMap.setOnMapLoadedCallback(() -> {
                                    // Make a snapshot when map's done loading
                                    googleMap.snapshot(newValue -> {
                                        String pfad = Utilities.bildTrainingsortSpeichern(context, newValue);
                                        EditTrainingsortActivity.setTrainingsortFotoAlsString(pfad);

                                        Log.d("Bild ermitteln", "Map Screenshot aufgenommen");
                                        Log.d("Bild ermitteln", "Screen Pfad: " + pfad);

                                        Trainingsort updatedTrainingsort2;
                                        String trainingsortFotoAlsString = EditTrainingsortActivity.getTrainingsortFotoAlsString();

                                        if (trainingsortFotoAlsString == null) {

                                            updatedTrainingsort2 = dataSource.updateTrainingsort(trainingsort.getTo_id(), name, strasse, plz, ort, trainingsort.getFoto(), trainingsort.getLatitude(), trainingsort.getLongitude(), trainingsort.getBesuche());

                                        } else {
                                            updatedTrainingsort2 = dataSource.updateTrainingsort(trainingsort.getTo_id(), name, strasse, plz, ort, trainingsortFotoAlsString, trainingsort.getLatitude(), trainingsort.getLongitude(), trainingsort.getBesuche());
                                            EditTrainingsortActivity.setTrainingsortFotoAlsString(Utilities.bildNachTrainingsortBenennen(context, updatedTrainingsort2));
                                            updatedTrainingsort2 = dataSource.updateFotoTrainingsort(updatedTrainingsort2, EditTrainingsortActivity.getTrainingsortFotoAlsString());
                                            EditTrainingsortActivity.setTrainingsortFotoAlsString(null);
                                        }

                                        EditTrainingsortActivity.setTrainingsortFotoAlsString(null);
                                        Intent intent = new Intent(context, TrainingsortActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra("trainingsort", updatedTrainingsort2);
                                        context.startActivity(intent);
                                        Objects.requireNonNull(getActivity()).finish();
                                    });
                                });
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            EditTrainingsortActivity.setTrainingsortFotoAlsString(null);
                        }
                    }
                }

            }
        }
    }

    static class TrainingsortLoeschenAsyncTask extends AsyncTask<Void, Void, Void> {

        private final WeakReference<Context> activityReference;

        private final Trainingsort trainingsort;

        TrainingsortLoeschenAsyncTask(Context context, Trainingsort trainingsort) {
            activityReference = new WeakReference<>(context);
            this.trainingsort = trainingsort;

        }

        @Override
        protected Void doInBackground(Void... args) {

            DataSource dataSource = DataSource.getInstance();
            dataSource.open();

            dataSource.deleteTrainingsort(trainingsort);

            //Bilddatei löschen
            ContextWrapper cw = new ContextWrapper(activityReference.get());
            File directory = cw.getDir("profilbilder", Context.MODE_PRIVATE);
            File bild = new File(directory, trainingsort.getTo_id() + "_" + trainingsort.getName() + ".png");
            boolean geloescht = bild.delete();

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {

            Activity activity = (Activity) activityReference.get();
            Objects.requireNonNull(activity).finish();
            EditTrainingsortActivity.getOnEditFinish().onEditFinish();
        }

        //Interface für den Löschen-Button um die darunterliegenden Activitys auf Klick zu beenden
        public interface OnLoeschenClick {
            void onLoeschenClick();
        }
    }
}
