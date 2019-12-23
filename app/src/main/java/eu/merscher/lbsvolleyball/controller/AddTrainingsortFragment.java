package eu.merscher.lbsvolleyball.controller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.Objects;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.database.DataSource;
import eu.merscher.lbsvolleyball.model.Trainingsort;
import eu.merscher.lbsvolleyball.utilities.Utilities;


public class AddTrainingsortFragment extends Fragment {

    //public static Activity activity = null;

    public AddTrainingsortFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add_edit_spieler, container, false);
        //activity = this.getActivity();

        AddTrainingsortFragmentAdapter adapter = new AddTrainingsortFragmentAdapter(this);
        RecyclerView recyclerView = rootView.findViewById(R.id.fragment_add_edit_spieler_recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return rootView;

    }

    public class AddTrainingsortFragmentAdapter extends RecyclerView.Adapter<AddTrainingsortFragmentAdapter.ViewHolder> {


        private final LayoutInflater inflate;
        private final AddTrainingsortFragment context;


        AddTrainingsortFragmentAdapter(AddTrainingsortFragment context) {
            this.inflate = LayoutInflater.from(context.getContext());
            this.context = context;
        }

        @NotNull
        @Override
        public AddTrainingsortFragmentAdapter.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {

            View view = inflate.inflate(R.layout.fragment_add_trainingsort_item, parent, false);
            return new AddTrainingsortFragmentAdapter.ViewHolder(view);
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

            ViewHolder(final View view) {
                super(view);
                editTextName = view.findViewById(R.id.editText_name_trainingsort_add);
                editTextStrasse = view.findViewById(R.id.editText_strasse_add);
                editTextPlz = view.findViewById(R.id.editText_plz_add);
                editTextOrt = view.findViewById(R.id.editText_ort_add);
                buttonTrainingsortAnlegen = view.findViewById(R.id.fragement_add_trainingsort_button);


                //Spieler anlegen
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

                    new TrainingsortFotoSpeichernAsyncTask(context, name, strasse, plz, ort).execute();
                });

            }

        }


    }

    private static class TrainingsortFotoSpeichernAsyncTask extends AsyncTask<Void, Void, LatLng> {


        private final WeakReference<AddTrainingsortFragment> activityReference;
        private String name;
        private String strasse;
        private String plz;
        private String ort;

        private TrainingsortFotoSpeichernAsyncTask(AddTrainingsortFragment context, String name, String strasse, String plz, String ort) {
            activityReference = new WeakReference<>(context);
            this.name = name;
            this.strasse = strasse;
            this.plz = plz;
            this.ort = ort;

        }

        @Override
        public LatLng doInBackground(Void... args) {

            AddTrainingsortFragment addTrainingsortFragment = activityReference.get();

            String adresse = strasse + ", " + plz + " " + ort;

            Bitmap trainingsortBild;

            LatLng latLngTrainingsort = Utilities.getLocationFromAddress(addTrainingsortFragment.getContext(), adresse);

            if (Objects.requireNonNull(latLngTrainingsort).longitude == -999) {
                Toast toast = Toast.makeText(addTrainingsortFragment.getContext(), "Fehler bei der Adressermittlung. Ist die Adresse korrekt eingegeben?", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.BOTTOM, 0, 100);
                toast.show();
            } else {
                try {
                    trainingsortBild = Utilities.getMapBildAusURL(Objects.requireNonNull(latLngTrainingsort).latitude, Objects.requireNonNull(latLngTrainingsort).longitude, 500, 500);
                    String pfad = Utilities.bildTrainingsortSpeichern(addTrainingsortFragment.getContext(), trainingsortBild);

                    AddTrainingsortActivity.setTrainingsortFotoAlsString(pfad);

                } catch (Exception e) {
                    e.printStackTrace();
                    AddTrainingsortActivity.setTrainingsortFotoAlsString(null);
                }
            }


            return latLngTrainingsort;
        }

        @Override
        public void onPostExecute(LatLng result) {

            AddTrainingsortFragment addTrainingsortFragment = activityReference.get();
            Trainingsort neuerTrainingsort;

            DataSource dataSource = DataSource.getInstance();
            dataSource.open();

            if (AddTrainingsortActivity.getTrainingsortFotoAlsString() != null) {

                neuerTrainingsort = dataSource.createTrainingsort(name, strasse, plz, ort, AddTrainingsortActivity.getTrainingsortFotoAlsString(), result.latitude, result.longitude, 0);
                AddTrainingsortActivity.setTrainingsortFotoAlsString(Utilities.bildNachTrainingsortBenennen(addTrainingsortFragment.getContext(), neuerTrainingsort));
                dataSource.updateFotoTrainingsort(neuerTrainingsort, AddTrainingsortActivity.getTrainingsortFotoAlsString());

            } else {

                dataSource.createTrainingsort(name, strasse, plz, ort, "avatar_maps", result.latitude, result.longitude, 0);

            }

            AddTrainingsortActivity.setTrainingsortFotoAlsString(null);


            Toast toast = Toast.makeText(addTrainingsortFragment.getContext(), "Der Trainingsort wurde angelegt.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();

            Intent intent = new Intent(addTrainingsortFragment.getContext(), TrainingsortVerwaltungActivity.class);
            Objects.requireNonNull(addTrainingsortFragment.getActivity()).finish();
            addTrainingsortFragment.startActivity(intent);
        }
    }
}
