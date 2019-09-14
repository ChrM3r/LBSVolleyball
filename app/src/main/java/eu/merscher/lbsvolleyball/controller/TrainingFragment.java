package eu.merscher.lbsvolleyball.controller;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.database.DataSource;
import eu.merscher.lbsvolleyball.model.Buchung;
import eu.merscher.lbsvolleyball.model.Spieler;
import eu.merscher.lbsvolleyball.utilities.Utilities;


public class TrainingFragment extends Fragment implements EinstellungenFragment.OnEinstellungChange {

    private static final DecimalFormat df = new DecimalFormat("0.00");
    /**
     * A list of locations to show in this ListView.
     */
    private static final NamedLocation[] LIST_LOCATIONS = new NamedLocation[]{
            new NamedLocation("BUGA Potsdam", new LatLng(52.4124881, 13.0491085)),
            new NamedLocation("Werder/Havel", new LatLng(52.384314, 12.9088907))
    };
    public static ArrayList<Spieler> selectedSpieler = new ArrayList<>();
    public static ArrayList<Spieler> spielerList = new ArrayList<>();
    public static Resources resources;
    private static EinstellungenFragment.OnEinstellungChange onEinstellungChange;
    //private static TrainingFragment.OnResume onResume;
    private static boolean shouldExecuteOnResume;
    private TrainingFragmentAdapter adapter;

    public TrainingFragment() {
    }

    //Statische Methoden
    public static void addSelectedSpieler(Spieler spieler) {
        selectedSpieler.add(spieler);
    }

    public static boolean spielerIstSelected(Spieler spieler) {
        if (selectedSpieler.isEmpty())
            return false;
        else
            return selectedSpieler.contains(spieler);
    }

    public static void uncheckSelectedSpieler(Spieler spieler) {
        selectedSpieler.remove(spieler);
    }

    public static ArrayList<Spieler> getSpielerList() {
        return spielerList;
    }

    public static void setSpielerList(ArrayList<Spieler> spielerList) {
        TrainingFragment.spielerList = spielerList;
    }

    public static EinstellungenFragment.OnEinstellungChange getOnEinstellungChange() {
        return onEinstellungChange;
    }

    @Override
    public void onEinstellungChange(String value) {

        if (value != null) {
            adapter.holder.editTextPlatzkosten.setText(df.format(Float.valueOf(value.replace(",", "."))).replace('.', ','));
            adapter.setBetragJeSpieler();
        }

    }

    public void onSpielerClick() {

        adapter.setBetragJeSpieler();
        adapter.holder.editTextPlatzkosten.clearFocus();
        adapter.holder.buttonAddSpieltag.requestFocus();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        onEinstellungChange = this;

        View view = inflater.inflate(R.layout.fragment_spielerseite_grunddaten_kontodaten, container, false);

        adapter = new TrainingFragmentAdapter((TrainingTunierActivity) getContext(), this, (TrainingTunierActivity) getActivity());
        RecyclerView recyclerView = view.findViewById(R.id.fragment_spielerseite_recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        if (shouldExecuteOnResume) {
            adapter.holder.kostenlosSwitch.setChecked(false);
            //holder.editTextPlatzkosten.setText("");
            //holder.betragJeSpieler.setText(resources.getText(R.string.betrag_0));
            new SpielerauswahlBefuellenAsyncTask((TrainingTunierActivity) this.getContext(), (TrainingTunierActivity) getActivity(), adapter.holder).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            selectedSpieler.clear();
            adapter.setBetragJeSpieler();

        } else
            shouldExecuteOnResume = true;
    }

    public interface OnResume {
        void onResumeInterface();
    }

    static class SpielerauswahlBefuellenAsyncTask extends AsyncTask<Void, Void, ArrayList<Spieler>> {


        public final WeakReference<TrainingTunierActivity> activityReference;
        private ArrayList<Spieler> list = new ArrayList<>();
        private TrainingTunierSpielerauswahlFragment.OnSpielerClickListener onSpielerClickListener;
        private TrainingFragmentAdapter.ViewHolder holder;

        SpielerauswahlBefuellenAsyncTask(TrainingTunierActivity context, TrainingTunierSpielerauswahlFragment.OnSpielerClickListener onSpielerClickListener, TrainingFragmentAdapter.ViewHolder holder) {
            activityReference = new WeakReference<>(context);
            this.onSpielerClickListener = onSpielerClickListener;
            this.holder = holder;

        }

        @Override
        protected ArrayList<Spieler> doInBackground(Void... args) {

            DataSource dataSource = DataSource.getInstance();
            dataSource.open();

            list = dataSource.getAllSpielerAbsteigendTeilnahme();
            TrainingFragment.setSpielerList(list);
            return list;
        }

        @Override
        public void onPostExecute(ArrayList<Spieler> list) {

            TrainingTunierActivity activity = activityReference.get();

            if (activity == null) return;

            //Spielerauswahl
            if (activity.fragment == null) {
                activity.fm = activity.getSupportFragmentManager();
                activity.fragment = new TrainingTunierSpielerauswahlFragment(activity, list, onSpielerClickListener);
                activity.fm.beginTransaction().add(R.id.fragment_training_spielerauswahl_fragmentContainer, activity.fragment).commitAllowingStateLoss();
            } else {
                activity.fm.beginTransaction().replace(R.id.fragment_training_spielerauswahl_fragmentContainer, new TrainingTunierSpielerauswahlFragment(activity, list, onSpielerClickListener)).commitAllowingStateLoss();
            }

        }
    }

    static class SpieltagBuchenAsyncTask extends AsyncTask<Void, Void, Buchung> {


        public final WeakReference<TrainingFragmentAdapter> activityReference;
        private Spieler spieler;
        private Buchung buchung;

        SpieltagBuchenAsyncTask(TrainingFragmentAdapter context, Spieler spieler) {
            activityReference = new WeakReference<>(context);
            this.spieler = spieler;
        }


        @Override
        protected Buchung doInBackground(Void... args) {

            TrainingFragmentAdapter activity = activityReference.get();


            Calendar kalender = Calendar.getInstance();
            SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);

            DataSource dataSource = DataSource.getInstance();
            dataSource.open();


            if (selectedSpieler.size() > 0 && activity.platzkosten > 0) {

                activity.bu_btr = activity.platzkosten / selectedSpieler.size();

                if (spieler.getHat_buchung_mm() != null) {

                    double kto_saldo_alt = dataSource.getNeusteBuchungZuSpieler(spieler).getKto_saldo_neu();
                    double kto_saldo_neu = kto_saldo_alt - activity.bu_btr;

                    buchung = dataSource.createBuchung(spieler.getU_id(), -activity.bu_btr, kto_saldo_alt, kto_saldo_neu, datumsformat.format(kalender.getTime()), "X", null, null);
                    spieler = dataSource.updateTeilnahmenSpieler(spieler);

                } else {

                    double kto_saldo_neu = -activity.bu_btr;
                    buchung = dataSource.createBuchung(spieler.getU_id(), -activity.bu_btr, 0, kto_saldo_neu, datumsformat.format(kalender.getTime()), "X", null, null);
                    spieler = dataSource.updateHatBuchungenMM(dataSource.updateTeilnahmenSpieler(spieler));
                }


            } else {
                spieler = dataSource.updateTeilnahmenSpieler(spieler);

            }

            return buchung;
        }

        @Override
        public void onPostExecute(Buchung neusteBuchung) {

            if (neusteBuchung != null) {
                if (neusteBuchung.getKto_saldo_neu() < 5)
                    new EMailSendenAsyncTask(spieler, df.format(neusteBuchung.getKto_saldo_neu())).execute();
            }
        }


    }

    static class EMailSendenAsyncTask extends AsyncTask<Void, Void, Void> {


        private final Spieler spieler;
        private final String kontostand;

        EMailSendenAsyncTask(Spieler spieler, String kontostand) {
            this.spieler = spieler;
            this.kontostand = kontostand;
        }


        @Override
        protected Void doInBackground(Void... args) {

            // SMTP Verbindung starten
            final String username = "lbsvolleyball@merscher.eu";
            final String password = "be1gvd1!b685+08787adklasdnl#++13nlandasd2";

            Properties props = new Properties();

            props.put("mail.smtp.host", "mail.merscher.eu");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "465");

            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });

            // Nachricht:
            String to = spieler.getMail();
            String from = "LBSVolleyball@merscher.eu";
            String subject = "Kontostand niedrig";
            Message msg = new MimeMessage(session);

            if (to != null) {
                try {
                    msg.setFrom(new InternetAddress(from));
                    msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
                    msg.setSubject(subject);

                    msg.setText("Hi " + spieler.getVname()
                            + ",\n\nder Kontostand deines Spielerkontos beträgt " + kontostand
                            + "€. Bitte zahle demnächst wieder etwas ein.\n\nVielen Dank und sportliche Grüße\nLBS Volleyball App-Admin");

                    Transport.send(msg);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
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

    public class TrainingFragmentAdapter extends RecyclerView.Adapter<TrainingFragmentAdapter.ViewHolder> implements OnMapReadyCallback {


        private final LayoutInflater inflate;
        private final SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
        public TrainingTunierActivity context;
        private ViewHolder holder;
        private Boolean kostenlos = false;
        private float platzkosten;
        private double bu_btr;
        private Calendar kalender = Calendar.getInstance();
        private EinstellungenFragment.OnEinstellungChange onEinstellungChange;
        private TrainingTunierSpielerauswahlFragment.OnSpielerClickListener onSpielerClickListener;


        public TrainingFragmentAdapter(TrainingTunierActivity context, EinstellungenFragment.OnEinstellungChange onEinstellungChange, TrainingTunierSpielerauswahlFragment.OnSpielerClickListener onSpielerClickListener) {
            this.inflate = LayoutInflater.from(context);
            this.context = context;
            this.onEinstellungChange = onEinstellungChange;
            this.onSpielerClickListener = onSpielerClickListener;
            //onResume = this;

        }


//    public static TrainingFragment.OnResume getOnResume() {
//        return onResume;
//    }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = inflate.inflate(R.layout.fragment_training, parent, false);
            return new ViewHolder(view);

        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            this.holder = holder;
            resources = context.getResources();
            selectedSpieler.clear();

            holder.editTextPlatzkosten.setGravity(Gravity.END);

            DataSource.initializeInstance(this.context);

            DataSource dataSource = DataSource.getInstance();
            dataSource.open();

            ArrayList<Spieler> list = new ArrayList<>();

            list = dataSource.getAllSpielerAbsteigendTeilnahme();
            TrainingFragment.setSpielerList(list);


            //Spielerauswahl
            if (context.fragment == null) {
                context.fm = context.getSupportFragmentManager();
                context.fragment = new TrainingTunierSpielerauswahlFragment(context, list, onSpielerClickListener);
                context.fm.beginTransaction().add(R.id.fragment_training_spielerauswahl_fragmentContainer, context.fragment).commitAllowingStateLoss();
            } else {
                context.fm.beginTransaction().replace(R.id.fragment_training_spielerauswahl_fragmentContainer, new TrainingTunierSpielerauswahlFragment(context, list, onSpielerClickListener)).commitAllowingStateLoss();
            }


            //Kostenlos-Switch

            holder.kostenlosSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                    if (isChecked) {
                        kostenlos = isChecked;
                        holder.betragJeSpieler.setText(resources.getText(R.string.betrag_0));
                        holder.editTextPlatzkosten.setText("");
                        holder.editTextPlatzkosten.setEnabled(false);
                        holder.editTextPlatzkosten.setFocusable(false);

                        holder.cardView.setLayoutParams(new LinearLayout.LayoutParams(holder.cardView.getLayoutParams().width, 72));
                        holder.cardView.setMinimumHeight(72);

                        LinearLayout.MarginLayoutParams layoutParams = (LinearLayout.MarginLayoutParams) holder.cardView.getLayoutParams();
                        layoutParams.setMargins(16, 32, 16, 0);

                        holder.cardView.requestLayout();

                    } else {
                        kostenlos = false;
                        holder.editTextPlatzkosten.setEnabled(true);
                        holder.editTextPlatzkosten.setFocusableInTouchMode(true);

                        holder.cardView.setLayoutParams(new LinearLayout.LayoutParams(holder.cardView.getLayoutParams().width, 256));
                        holder.cardView.setMinimumHeight(256);

                        LinearLayout.MarginLayoutParams layoutParams = (LinearLayout.MarginLayoutParams) holder.cardView.getLayoutParams();
                        layoutParams.setMargins(16, 32, 16, 0);

                        holder.cardView.requestLayout();
                        setBetragJeSpieler();
                    }
                }
            });

            //Spieltag-Button
            holder.buttonAddSpieltag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSpieltagButtonClick(v);
                }
            });


            //Textformat Platzkosten
            holder.editTextPlatzkosten.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    Utilities.formatNumericEditText(holder.editTextPlatzkosten);
                    setBetragJeSpieler();

                }
            });

            //Platzkosten aus Einstellungen

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.context);
            String einstellungen_platzkosten = "einstellungen_platzkosten";
            String platzkostenEinstellungen = sharedPrefs.getString(einstellungen_platzkosten, "");
            if (platzkostenEinstellungen != null) {
                if (!platzkostenEinstellungen.isEmpty()) {
                    if (Double.parseDouble(platzkostenEinstellungen) > 0) {
                        holder.editTextPlatzkosten.setText(df.format(Float.valueOf(platzkostenEinstellungen.replace(",", "."))).replace('.', ','));
                        setBetragJeSpieler();
                    }
                }
            }
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
            MapsInitializer.initialize(context);
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


        @Override
        public int getItemCount() {
            return 1;
        }


        public void onSpieltagButtonClick(View v) {

            if (!holder.editTextPlatzkosten.getText().toString().isEmpty())
                platzkosten = Float.parseFloat(holder.editTextPlatzkosten.getText().toString().replace(',', '.'));
            else
                platzkosten = 0;

            long trainings_id;
            DataSource dataSource = DataSource.getInstance();
            dataSource.open();

            if (dataSource.getNeustesTrainingsID() != -999) {
                trainings_id = dataSource.getNeustesTrainingsID() + 1;
            } else
                trainings_id = 1;


//
//        InputMethodManager inputMethodManager;
//        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//
//        if (getCurrentFocus() != null) {
//            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
//        }

            if (selectedSpieler.size() > 0 && platzkosten > 0 && !kostenlos) {

                bu_btr = platzkosten / selectedSpieler.size();

                //Datenbank-Einträge erzeugen

                for (Spieler s : selectedSpieler) {

                    new SpieltagBuchenAsyncTask(this, s).execute();
                    dataSource.createTraining(trainings_id, datumsformat.format(kalender.getTime()), s.getU_id(), null);
                }

                new SpielerauswahlBefuellenAsyncTask(context, (TrainingTunierActivity) getActivity(), holder).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                Toast toast = Toast.makeText(context, "Der Spieltag wurde gebucht.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
                holder.editTextPlatzkosten.setText("");
                holder.betragJeSpieler.setText(resources.getText(R.string.betrag_0));
                holder.kostenlosSwitch.setChecked(false);

            } else if (selectedSpieler.size() <= 0) {

                Toast toast = Toast.makeText(context, "Es wurde kein Spieler ausgewählt.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();

            } else if (platzkosten <= 0 && !kostenlos) {

                Toast toast = Toast.makeText(context, "Die Platzkosten wurden nicht erfasst", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();

            } else if (platzkosten > 0 && kostenlos) {

                Toast toast = Toast.makeText(context, "Es wurden Platzkosten erfasst, obwohl >Kostenlos< gewählt wurde.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();

            } else if (platzkosten <= 0 && kostenlos) {

                for (Spieler s : selectedSpieler) {

                    new SpieltagBuchenAsyncTask(this, s).execute();
                    dataSource.createTraining(trainings_id, datumsformat.format(kalender.getTime()), s.getU_id(), "X");

                }

                new SpielerauswahlBefuellenAsyncTask(context, (TrainingTunierActivity) getActivity(), holder).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                Toast toast = Toast.makeText(this.context, "Der kostenlose Spieltag wurde gebucht. Trainigsteilnahmen aktualisiert", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();

                holder.kostenlosSwitch.setChecked(false);
            }
        }

        public void setBetragJeSpieler() {

            System.out.println("setBetragJeSpieler");
            if (!holder.editTextPlatzkosten.getText().toString().isEmpty()) {
                double platzkosten = Double.valueOf(holder.editTextPlatzkosten.getText().toString().replace(',', '.'));
                int anzahl = selectedSpieler.size();

                if (anzahl > 0) {

                    double betragDouble = platzkosten / anzahl;
                    holder.betragJeSpieler.setText(df.format(betragDouble).replace('.', ','));
                } else
                    holder.betragJeSpieler.setText(df.format(platzkosten).replace('.', ','));
            } else
                holder.betragJeSpieler.setText(resources.getText(R.string.betrag_0));
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            private Button buttonAddSpieltag;
            private EditText editTextPlatzkosten;
            private TextView betragJeSpieler;
            private Switch kostenlosSwitch;
            private CardView cardView;
            private MapView mapView;
            private GoogleMap map;

            ViewHolder(View view) {
                super(view);
                buttonAddSpieltag = view.findViewById(R.id.fragment_training_button_add_spieltag);
                editTextPlatzkosten = view.findViewById(R.id.fragment_training_editText_platzkosten);
                betragJeSpieler = view.findViewById(R.id.fragment_training_textview_betrag_je_spieler);
                kostenlosSwitch = view.findViewById(R.id.fragment_training_kostenlosSwitch);
                cardView = view.findViewById(R.id.fragment_training_cardVieW_platzkosten);
                mapView = view.findViewById(R.id.fragment_training_mapView);
            }

        }

    }

}
