package eu.merscher.lbsvolleyball.controller;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
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


public class TunierFragmentAdapter extends RecyclerView.Adapter<TunierFragmentAdapter.ViewHolder> implements TrainingTunierSpielerauswahlFragment.OnSpielerClickListener, TrainingFragment.OnResume, OnMapReadyCallback {


    private static final DecimalFormat df = new DecimalFormat("0.00");
    /**
     * A list of locations to show in this ListView.
     */
    private static final NamedLocation[] LIST_LOCATIONS = new NamedLocation[]{
            new NamedLocation("Cape Town", new LatLng(-33.920455, 18.466941)),
            new NamedLocation("Beijing", new LatLng(39.937795, 116.387224)),
            new NamedLocation("Bern", new LatLng(46.948020, 7.448206)),
            new NamedLocation("Breda", new LatLng(51.589256, 4.774396)),
            new NamedLocation("Brussels", new LatLng(50.854509, 4.376678)),
            new NamedLocation("Copenhagen", new LatLng(55.679423, 12.577114)),
            new NamedLocation("Hannover", new LatLng(52.372026, 9.735672)),
            new NamedLocation("Helsinki", new LatLng(60.169653, 24.939480)),
            new NamedLocation("Hong Kong", new LatLng(22.325862, 114.165532)),
            new NamedLocation("Istanbul", new LatLng(41.034435, 28.977556)),
            new NamedLocation("Johannesburg", new LatLng(-26.202886, 28.039753)),
            new NamedLocation("Lisbon", new LatLng(38.707163, -9.135517)),
            new NamedLocation("London", new LatLng(51.500208, -0.126729)),
            new NamedLocation("Madrid", new LatLng(40.420006, -3.709924)),
            new NamedLocation("Mexico City", new LatLng(19.427050, -99.127571)),
            new NamedLocation("Moscow", new LatLng(55.750449, 37.621136)),
            new NamedLocation("New York", new LatLng(40.750580, -73.993584)),
            new NamedLocation("Oslo", new LatLng(59.910761, 10.749092)),
            new NamedLocation("Paris", new LatLng(48.859972, 2.340260)),
            new NamedLocation("Prague", new LatLng(50.087811, 14.420460)),
            new NamedLocation("Rio de Janeiro", new LatLng(-22.90187, -43.232437)),
            new NamedLocation("Rome", new LatLng(41.889998, 12.500162)),
            new NamedLocation("Sao Paolo", new LatLng(-22.863878, -43.244097)),
            new NamedLocation("Seoul", new LatLng(37.560908, 126.987705)),
            new NamedLocation("Stockholm", new LatLng(59.330650, 18.067360)),
            new NamedLocation("Sydney", new LatLng(-33.873651, 151.2068896)),
            new NamedLocation("Taipei", new LatLng(25.022112, 121.478019)),
            new NamedLocation("Tokyo", new LatLng(35.670267, 139.769955)),
            new NamedLocation("Tulsa Oklahoma", new LatLng(36.149777, -95.993398)),
            new NamedLocation("Vaduz", new LatLng(47.141076, 9.521482)),
            new NamedLocation("Vienna", new LatLng(48.209206, 16.372778)),
            new NamedLocation("Warsaw", new LatLng(52.235474, 21.004057)),
            new NamedLocation("Wellington", new LatLng(-41.286480, 174.776217)),
            new NamedLocation("Winnipeg", new LatLng(49.875832, -97.150726))
    };
    public static ArrayList<Spieler> selectedSpieler = new ArrayList<>();
    public static Resources resources;
    private static TrainingFragment.OnResume onResume;
    private static boolean shouldExecuteOnResume;
    private final LayoutInflater inflate;
    private final SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
    public Context context;
    public TrainingTunierActivity trainingTunierActivity;
    private ViewHolder holder;
    private Boolean kostenlos = false;
    private float platzkosten;
    private double bu_btr;
    private Calendar kalender = Calendar.getInstance();

    public TunierFragmentAdapter(Context context) {
        this.inflate = LayoutInflater.from(context);
        this.context = context;
        onResume = this;

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

    public static TrainingFragment.OnResume getOnResume() {
        return onResume;
    }

    @Override
    public void onResumeInterface() {
        if (shouldExecuteOnResume) {
            holder.kostenlosSwitch.setChecked(false);
            holder.editTextPlatzkosten.setText("");
            holder.betragJeSpieler.setText(resources.getText(R.string.betrag_0));
            new TunierFragmentAdapter.SpielerauswahlBefuellenAsyncTask((TrainingTunierActivity) context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            selectedSpieler.clear();
        } else
            shouldExecuteOnResume = true;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflate.inflate(R.layout.fragment_tunier, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        this.holder = holder;

        trainingTunierActivity = (TrainingTunierActivity) context;
        resources = context.getResources();

        selectedSpieler.clear();

        holder.editTextPlatzkosten.setGravity(Gravity.END);

        DataSource.initializeInstance(this.context);


        new TunierFragmentAdapter.SpielerauswahlBefuellenAsyncTask((TrainingTunierActivity) context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


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
                    holder.cardView.setLayoutParams(new LinearLayout.LayoutParams(
                            holder.cardView.getLayoutParams().width, 72));
                    holder.cardView.setMinimumHeight(72);
                    LinearLayout.MarginLayoutParams layoutParams =
                            (LinearLayout.MarginLayoutParams) holder.cardView.getLayoutParams();
                    layoutParams.setMargins(16, 16, 16, 0);
                    holder.cardView.requestLayout();

                } else {
                    kostenlos = false;
                    holder.editTextPlatzkosten.setEnabled(true);
                    holder.editTextPlatzkosten.setFocusableInTouchMode(true);
                    setBetragJeSpieler();
                    holder.cardView.setLayoutParams(new LinearLayout.LayoutParams(
                            holder.cardView.getLayoutParams().width, 256));
                    holder.cardView.setMinimumHeight(256);
                    LinearLayout.MarginLayoutParams layoutParams =
                            (LinearLayout.MarginLayoutParams) holder.cardView.getLayoutParams();
                    layoutParams.setMargins(16, 16, 16, 0);
                    holder.cardView.requestLayout();
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
    }

    private void setMapLocation() {
        if (holder.map == null) return;

        NamedLocation data = new NamedLocation("Hannover", new LatLng(52.372026, 9.735672));

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

    @Override
    public void onSpielerClick() {

        setBetragJeSpieler();
        holder.editTextPlatzkosten.clearFocus();
        holder.buttonAddSpieltag.requestFocus();
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

                new TunierFragmentAdapter.SpieltagBuchenAsyncTask(this, s).execute();
                dataSource.createTraining(trainings_id, datumsformat.format(kalender.getTime()), s.getU_id(), null);

            }

            new TunierFragmentAdapter.SpielerauswahlBefuellenAsyncTask((TrainingTunierActivity) context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            Toast toast = Toast.makeText(this.context, "Der Spieltag wurde gebucht.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
            holder.editTextPlatzkosten.setText("");
            holder.betragJeSpieler.setText(resources.getText(R.string.betrag_0));
            holder.kostenlosSwitch.setChecked(false);

        } else if (selectedSpieler.size() <= 0) {

            Toast toast = Toast.makeText(this.context, "Es wurde kein Spieler ausgewählt.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();

        } else if (platzkosten <= 0 && !kostenlos) {

            Toast toast = Toast.makeText(this.context, "Die Platzkosten wurden nicht erfasst", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();

        } else if (platzkosten > 0 && kostenlos) {

            Toast toast = Toast.makeText(this.context, "Es wurden Platzkosten erfasst, obwohl >Kostenlos< gewählt wurde.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();

        } else if (platzkosten <= 0 && kostenlos) {

            for (Spieler s : selectedSpieler) {

                new TunierFragmentAdapter.SpieltagBuchenAsyncTask(this, s).execute();
                dataSource.createTraining(trainings_id, datumsformat.format(kalender.getTime()), s.getU_id(), "X");

            }

            new TunierFragmentAdapter.SpielerauswahlBefuellenAsyncTask((TrainingTunierActivity) context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            Toast toast = Toast.makeText(this.context, "Der kostenlose Spieltag wurde gebucht. Trainigsteilnahmen aktualisiert", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();

            holder.kostenlosSwitch.setChecked(false);
        }
    }


    public void setBetragJeSpieler() {

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


    static class SpielerauswahlBefuellenAsyncTask extends AsyncTask<Void, Void, ArrayList<Spieler>> {


        public final WeakReference<TrainingTunierActivity> activityReference;
        private ArrayList<Spieler> spielerList = new ArrayList<>();

        SpielerauswahlBefuellenAsyncTask(TrainingTunierActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected ArrayList<Spieler> doInBackground(Void... args) {

            DataSource dataSource = DataSource.getInstance();
            dataSource.open();

            spielerList = dataSource.getAllSpielerAbsteigendTeilnahme();
            return spielerList;
        }

        @Override
        public void onPostExecute(ArrayList<Spieler> result) {

            TrainingTunierActivity activity = activityReference.get();
            if (activity == null) return;

            spielerList = result;

            //Spielerauswahl
            if (activity.fragment == null) {
                activity.fm = activity.getSupportFragmentManager();
                activity.fragment = new TrainingTunierSpielerauswahlFragment(activity, spielerList, activity);
                activity.fm.beginTransaction().add(R.id.fragment_training_spielerauswahl_fragmentContainer, activity.fragment).commitAllowingStateLoss();
            } else {
                activity.fm.beginTransaction().replace(R.id.fragment_training_spielerauswahl_fragmentContainer, new TrainingTunierSpielerauswahlFragment(activity, spielerList, activity)).commitAllowingStateLoss();

            }

        }
    }

    static class SpieltagBuchenAsyncTask extends AsyncTask<Void, Void, Buchung> {


        public final WeakReference<TunierFragmentAdapter> activityReference;
        private Spieler spieler;
        private Buchung buchung;

        SpieltagBuchenAsyncTask(TunierFragmentAdapter context, Spieler spieler) {
            activityReference = new WeakReference<>(context);
            this.spieler = spieler;
        }


        @Override
        protected Buchung doInBackground(Void... args) {

            TunierFragmentAdapter activity = activityReference.get();


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
                    new TunierFragmentAdapter.EMailSendenAsyncTask(spieler, df.format(neusteBuchung.getKto_saldo_neu())).execute();
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
            buttonAddSpieltag = view.findViewById(R.id.fragment_tunier_button_add_spieltag);
            editTextPlatzkosten = view.findViewById(R.id.fragment_tunier_editText_platzkosten);
            betragJeSpieler = view.findViewById(R.id.fragment_tunier_textview_betrag_je_spieler);
            kostenlosSwitch = view.findViewById(R.id.fragment_tunier_kostenlosSwitch);
            cardView = view.findViewById(R.id.fragment_tunier_cardVieW_platzkosten);
            mapView = view.findViewById(R.id.fragment_tunier_mapView);

        }

    }
}

