package eu.merscher.lbsvolleyball.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
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
import eu.merscher.lbsvolleyball.model.Trainingsort;
import eu.merscher.lbsvolleyball.utilities.Utilities;


public class TrainingFragment extends Fragment {


    private static final DecimalFormat df = new DecimalFormat("0.00");
    private DataSource dataSource;
    private static ArrayList<Spieler> selectedSpieler = new ArrayList<>();
    public static Resources resources;
    private ArrayList<Trainingsort> trainingsortList;

    private TrainingFragmentAdapter adapter;

    public TrainingFragment() {
    }

    //Statische Methoden
    static void addSelectedSpieler(Spieler spieler) {
        selectedSpieler.add(spieler);
    }

    static boolean spielerIstSelected(Spieler spieler) {
        if (selectedSpieler.isEmpty())
            return false;
        else
            return selectedSpieler.contains(spieler);
    }

    static void uncheckSelectedSpieler(Spieler spieler) {
        selectedSpieler.remove(spieler);
    }

    private static ArrayList<Spieler> getSelectedSpieler() {
        return selectedSpieler;
    }

    @SuppressLint("SetTextI18n")
    void onSpielerClick() {

        adapter.setBetragJeSpieler();
        adapter.holder.anzahl_spieler.setText(Integer.toString(getSelectedSpieler().size()));
        adapter.holder.editTextPlatzkosten.clearFocus();
        adapter.holder.buttonAddSpieltag.requestFocus();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_spielerseite_grunddaten_kontodaten, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.fragment_spielerseite_recyclerView);
        adapter = new TrainingFragmentAdapter((TrainingTunierActivity) getContext(), (TrainingTunierActivity) getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return view;

    }


    public class TrainingFragmentAdapter extends RecyclerView.Adapter<TrainingFragmentAdapter.TrainingFragmentViewHolder> {


        private final LayoutInflater inflate;
        public TrainingTunierActivity context;
        private TrainingFragmentViewHolder holder = null;
        private Boolean kostenlos = false;
        private Trainingsort trainingsort;
        private TrainingTunierSpielerauswahlFragment.OnSpielerClickListener onSpielerClickListener;


        private TrainingFragmentAdapter(TrainingTunierActivity context, TrainingTunierSpielerauswahlFragment.OnSpielerClickListener onSpielerClickListener) {
            this.inflate = LayoutInflater.from(context);
            this.context = context;
            this.onSpielerClickListener = onSpielerClickListener;
        }


        private class TrainingFragmentViewHolder extends RecyclerView.ViewHolder {

            private Button buttonAddSpieltag;
            private EditText editTextPlatzkosten;
            private TextView betragJeSpieler;
            private Switch kostenlosSwitch;
            private Button trainingsort_anlegen;
            private ImageView mapView;
            private Spinner spinner;
            private LinearLayout layout_platzkosten;
            private DatePicker datePicker;
            private Switch datumSwitch;
            private TextView platzkosten_final;
            private TextView anzahl_spieler;

            TrainingFragmentViewHolder(View view) {

                super(view);
                buttonAddSpieltag = view.findViewById(R.id.fragment_training_button_add_spieltag);
                editTextPlatzkosten = view.findViewById(R.id.fragment_training_editText_platzkosten);
                betragJeSpieler = view.findViewById(R.id.fragment_training_textview_betrag_je_spieler);
                kostenlosSwitch = view.findViewById(R.id.fragment_training_kostenlosSwitch);
                trainingsort_anlegen = view.findViewById(R.id.fragment_training_trainingsort_anlegen);
                mapView = view.findViewById(R.id.fragment_training_mapView);
                spinner = view.findViewById(R.id.fragment_training_spinner);
                layout_platzkosten = view.findViewById(R.id.lin2);
                datePicker = view.findViewById(R.id.fragment_training_datum);
                datumSwitch = view.findViewById(R.id.fragment_training_heuteSwitch);
                platzkosten_final = view.findViewById(R.id.fragment_training_textview_platzkosten_final);
                anzahl_spieler = view.findViewById(R.id.fragment_training_textview_anzahl_spieler);
            }
        }

        @NotNull
        @Override
        public TrainingFragmentViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {

            View view = inflate.inflate(R.layout.fragment_training, parent, false);
            holder = new TrainingFragmentViewHolder(view);
            return holder;

        }

        @Override
        public void onBindViewHolder(TrainingFragmentViewHolder holder, int position) {

            resources = context.getResources();
            selectedSpieler.clear();


            DataSource dataSource = DataSource.getInstance();
            dataSource.open();

            //DatPicker
            Calendar kalender = Calendar.getInstance();
            holder.datePicker.updateDate(kalender.get(Calendar.YEAR), kalender.get(Calendar.MONTH), kalender.get(Calendar.DAY_OF_MONTH));

            holder.datumSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

                if (isChecked) {

                    holder.datePicker.updateDate(kalender.get(Calendar.YEAR), kalender.get(Calendar.MONTH), kalender.get(Calendar.DAY_OF_MONTH));
                    holder.datePicker.setVisibility(View.GONE);

                } else
                    holder.datePicker.setVisibility(View.VISIBLE);

            });
            holder.datumSwitch.setChecked(true);

            //Platzkosten aus Einstellungen auslesen
            platzKostenErmittelnUndSetzen();

            //Kostenlos-Switch
            holder.kostenlosSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {


                if (isChecked) {
                    kostenlos = true;
                    holder.betragJeSpieler.setText(resources.getText(R.string.betrag_0));
                    holder.editTextPlatzkosten.setText("");
                    holder.platzkosten_final.setText(resources.getText(R.string.betrag_0));

                    holder.layout_platzkosten.setVisibility(View.GONE);

                } else {
                    kostenlos = false;
                    holder.editTextPlatzkosten.setEnabled(true);
                    holder.editTextPlatzkosten.setFocusableInTouchMode(true);

                    holder.layout_platzkosten.setVisibility(View.VISIBLE);

                    platzKostenErmittelnUndSetzen();
                    setBetragJeSpieler();
                }
            });

            //Spieltag-Button
            holder.buttonAddSpieltag.setOnClickListener(v -> onSpieltagButtonClick());

            //Textformat Platzkosten
            holder.editTextPlatzkosten.setOnFocusChangeListener((v, hasFocus) -> {
                Utilities.formatNumericEditText(holder.editTextPlatzkosten);
                holder.platzkosten_final.setText(holder.editTextPlatzkosten.getText());
                setBetragJeSpieler();

            });
            holder.editTextPlatzkosten.setGravity(Gravity.END);

            //Trainingsort
            trainigsorteSpinnerUndMapBefuellen();

            //Spielerauswahl
            spielerAuswahlBefuellen();
        }



        private void spielerAuswahlBefuellen() {

            //Sortierung Einstellungen

            SharedPreferences einstellungen = PreferenceManager.getDefaultSharedPreferences(context);
            String einstellungen_sortierung_platzkosten = "einstellungen_sortierung_spielerauswahl";
            String sortierungUser = einstellungen.getString(einstellungen_sortierung_platzkosten, "teilnahmen");

            ArrayList<Spieler> list;

            dataSource = DataSource.getInstance();

            switch (Objects.requireNonNull(sortierungUser)) {
                case "vname": {
                    list = dataSource.getAllSpielerAlphabetischVname();
                    break;
                }
                case "name": {
                    list = dataSource.getAllSpielerAlphabetischName();
                    break;
                }
                default: {
                    list = dataSource.getAllSpielerAbsteigendTeilnahme();
                    break;
                }
            }

            //SpielerauswahlFragment laden
            context.fm = context.getSupportFragmentManager();
            context.fragment = new TrainingTunierSpielerauswahlFragment(context, list, onSpielerClickListener, sortierungUser);
            context.fm.beginTransaction().add(R.id.fragment_training_spielerauswahl_fragmentContainer, context.fragment).commitAllowingStateLoss();

        }

        private void platzKostenErmittelnUndSetzen() {
            //Einstellungen
            SharedPreferences einstellungen = PreferenceManager.getDefaultSharedPreferences(context);
            String einstellungen_platzkosten = "einstellungen_platzkosten";

            //Platzkosten
            String platzkostenUser = einstellungen.getString(einstellungen_platzkosten, "");
            if (!platzkostenUser.equals("")) {
                    if (Double.parseDouble(platzkostenUser.replace(",", ".")) > 0) {
                        holder.editTextPlatzkosten.setText(df.format(Float.valueOf(platzkostenUser.replace(",", "."))).replace('.', ','));
                        holder.platzkosten_final.setText(df.format(Float.valueOf(platzkostenUser.replace(",", "."))).replace('.', ','));
                        setBetragJeSpieler();
                    }
            } else holder.editTextPlatzkosten.setText("");

            holder.editTextPlatzkosten.setOnEditorActionListener((v, actionId, event) -> {

                if ((actionId == EditorInfo.IME_ACTION_DONE)) {

                    InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
                    Objects.requireNonNull(imm).hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;

            });
        }

        private void trainigsorteSpinnerUndMapBefuellen() {

            DataSource.initializeInstance(context);
            dataSource = DataSource.getInstance();

            trainingsortList = dataSource.getAllTrainingsort();
            ArrayList<String> spinnerList = new ArrayList<>();

            for (Trainingsort t : trainingsortList) {
                spinnerList.add(t.getName());
            }

            if (trainingsortList.isEmpty())
                spinnerList.add("Kein Ort angelegt.");

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, spinnerList);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.spinner.setAdapter(dataAdapter);

            holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                    if (trainingsortList.size() > 0) {
                        trainingsort = trainingsortList.get(position);
                        holder.mapView.setImageBitmap(BitmapFactory.decodeFile(trainingsort.getFoto()));
                    } else {
                        holder.mapView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.avatar_map));
                    }


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            if (trainingsortList.size() <= 0)
                holder.trainingsort_anlegen.setVisibility(View.VISIBLE);
            else
                holder.trainingsort_anlegen.setVisibility(View.GONE);

            holder.trainingsort_anlegen.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), AddTrainingsortActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            });

        }

        private void setBetragJeSpieler() {

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

        private void onSpieltagButtonClick() {


            //Datum aus Picker ermittlen

            int tag = holder.datePicker.getDayOfMonth();
            int monat = holder.datePicker.getMonth();
            int jahr = holder.datePicker.getYear();

            Calendar kalender = Calendar.getInstance();
            kalender.set(jahr, monat, tag);



            float platzkosten;
            if (!holder.editTextPlatzkosten.getText().toString().isEmpty())
                platzkosten = Float.parseFloat(holder.editTextPlatzkosten.getText().toString().replace(',', '.'));
            else
                platzkosten = 0;

            //Trainings-ID ermitteln

            long trainings_id_alt = dataSource.getNeusteTrainingsID();
            long trainings_id;

            dataSource.open();

            if (trainings_id_alt > 0) {
                trainings_id = trainings_id_alt + 1;
            } else
                trainings_id = 1;


            if (selectedSpieler.size() > 0 && platzkosten > 0 && !kostenlos && trainingsortList.size() > 0) {

                double bu_btr = platzkosten / selectedSpieler.size();

                //Datenbank-Einträge erzeugen

                trainingsort = dataSource.updateBesucheTrainingsort(trainingsort);

                for (int i = 0; i < selectedSpieler.size(); i++) {
                    if (i == selectedSpieler.size() - 1) {
                        new SpieltagBuchenAsyncTask(this, selectedSpieler.get(i), bu_btr, platzkosten, trainings_id, trainingsort, true, kalender).execute();
                    } else
                        new SpieltagBuchenAsyncTask(this, selectedSpieler.get(i), bu_btr, platzkosten, trainings_id, trainingsort, true, kalender).execute();

                }

                //Alles zurücksetzen

                holder.editTextPlatzkosten.setText("");
                holder.anzahl_spieler.setText("0");
                platzKostenErmittelnUndSetzen();
                selectedSpieler.clear();
                holder.kostenlosSwitch.setChecked(false);
                setBetragJeSpieler();

            } else if (selectedSpieler.size() <= 0) {

                Toast toast = Toast.makeText(context, "Es wurde kein Spieler ausgewählt.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 20);
                toast.show();

            } else if (platzkosten <= 0 && !kostenlos) {

                Toast toast = Toast.makeText(context, "Die Platzkosten wurden nicht erfasst", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 20);
                toast.show();

            } else if (platzkosten > 0 && kostenlos) {

                Toast toast = Toast.makeText(context, "Es wurden Platzkosten erfasst, obwohl >Kostenlos< gewählt wurde.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 20);
                toast.show();

            } else if (platzkosten <= 0 && kostenlos && trainingsortList.size() > 0) {

                trainingsort = dataSource.updateBesucheTrainingsort(trainingsort);

                for (int i = 0; i < selectedSpieler.size(); i++) {

                    if (i == selectedSpieler.size() - 1) {
                        new SpieltagBuchenAsyncTask(this, selectedSpieler.get(i), 0, platzkosten, trainings_id, trainingsort, true, kalender).execute();
                    } else
                        new SpieltagBuchenAsyncTask(this, selectedSpieler.get(i), 0, platzkosten, trainings_id, trainingsort, true, kalender).execute();

                }
                spielerAuswahlBefuellen();

                Toast toast = Toast.makeText(this.context, "Der kostenlose Spieltag wurde gebucht. Trainigsteilnahmen aktualisiert", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 20);
                toast.show();

                holder.kostenlosSwitch.setChecked(false);
            } else if (trainingsortList.size() <= 0) {

                Toast toast = Toast.makeText(context, "Es wurde kein Trainingsort ausgewählt", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 20);
                toast.show();
            }
        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }

    private static class SpieltagBuchenAsyncTask extends AsyncTask<Void, Void, Buchung> {

        private final WeakReference<TrainingFragmentAdapter> activityReference;
        private Spieler spieler;
        private Buchung buchung;
        private double bu_btr;
        private double platzkosten;
        private long trainings_id;
        private Trainingsort trainingsort;
        private boolean letzterSpieler;
        private Calendar kalender;

        SpieltagBuchenAsyncTask(TrainingFragmentAdapter context, Spieler spieler, double bu_btr, double platzkosten, long trainings_id, Trainingsort trainingsort, boolean letzterSpieler, Calendar kalender) {
            this.activityReference = new WeakReference<>(context);
            this.spieler = spieler;
            this.bu_btr = bu_btr;
            this.platzkosten = platzkosten;
            this.trainings_id = trainings_id;
            this.trainingsort = trainingsort;
            this.letzterSpieler = letzterSpieler;
            this.kalender = kalender;
        }


        @Override
        protected Buchung doInBackground(Void... args) {

            SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);

            DataSource dataSource = DataSource.getInstance();
            dataSource.open();

            //Trainingsort-ID
            long trainings_ort_id = trainingsort.getTo_id();


            if (bu_btr != 0) {

                if (spieler.getHat_buchung_mm() != null) {

                    double kto_saldo_alt = dataSource.getNeusteBuchungZuSpieler(spieler).getKto_saldo_neu();
                    double kto_saldo_neu = kto_saldo_alt - bu_btr;

                    buchung = dataSource.createBuchung(spieler.getS_id(), -bu_btr, kto_saldo_alt, kto_saldo_neu, datumsformat.format(kalender.getTime()), "X", trainings_id, null, null, -999, null, -999);
                    spieler = dataSource.updateTeilnahmenSpieler(spieler);
                    dataSource.createTraining(trainings_id, datumsformat.format(kalender.getTime()), trainings_ort_id, spieler.getS_id(), platzkosten, null);

                } else {

                    double kto_saldo_neu = -bu_btr;

                    buchung = dataSource.createBuchung(spieler.getS_id(), -bu_btr, 0, kto_saldo_neu, datumsformat.format(kalender.getTime()), "X", trainings_id, null, null, -999, null, -999);
                    spieler = dataSource.updateHatBuchungenMM(dataSource.updateTeilnahmenSpieler(spieler));
                    dataSource.createTraining(trainings_id, datumsformat.format(kalender.getTime()), trainings_ort_id, spieler.getS_id(), platzkosten, null);
                }


            } else {
                spieler = dataSource.updateTeilnahmenSpieler(spieler);
                dataSource.createTraining(trainings_id, datumsformat.format(kalender.getTime()), trainings_ort_id, spieler.getS_id(), 0, "X");
            }

            return buchung;
        }

        @Override
        public void onPostExecute(Buchung neusteBuchung) {

            TrainingFragmentAdapter activity = activityReference.get();

            if (letzterSpieler) {
                activity.spielerAuswahlBefuellen();

                Toast toast = Toast.makeText(activity.context, "Der Spieltag wurde gebucht.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 20);
                toast.show();
            }

            if (neusteBuchung != null) {
                if (neusteBuchung.getKto_saldo_neu() < 5)
                    new EMailSendenAsyncTask(spieler, df.format(neusteBuchung.getKto_saldo_neu())).execute();
            }


        }
    }

    private static class EMailSendenAsyncTask extends AsyncTask<Void, Void, Void> {


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
}
