package eu.merscher.lbsvolleyball;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class SpieltagActivity extends AppCompatActivity implements SpieltagActivitySpielerauswahlFragment.OnSpielerClickListenerInFragment {

    public static final ArrayList<Spieler> selectedSpieler = new ArrayList<>();
    private static final DecimalFormat df = new DecimalFormat("0.00");
    public static Resources resources;
    public static Context context;
    private Button buttonAddSpieltag;
    private NumericEditText editTextPlatzkosten;
    private TextView betragJeSpieler;
    private Switch kostenlosSwitch;
    private Boolean kostenlos = false;
    private Fragment fragment;
    private FragmentManager fm;
    private float platzkosten;
    private double bu_btr;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spieltag);

        resources = getResources();
        context = getApplicationContext();

        findViewsById();
        bottomNavBarInitialisieren();

        editTextPlatzkosten.setGravity(Gravity.END);

        SpielerDataSource.initializeInstance(this);
        BuchungDataSource.initializeInstance(this);


        new SpielerauswahlBefuellenAsyncTask(this).execute();

        //Kostenlos-Switch

        kostenlosSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {
                    kostenlos = isChecked;
                    betragJeSpieler.setText("0,00");
                    editTextPlatzkosten.setText("");
                    editTextPlatzkosten.setEnabled(false);
                    editTextPlatzkosten.setFocusable(false);
                } else {
                    kostenlos = false;
                    editTextPlatzkosten.setEnabled(true);
                    editTextPlatzkosten.setFocusableInTouchMode(true);
                    setBetragJeSpieler();
                }
            }
        });
        //Toolbar
        Toolbar toolbar = findViewById(R.id.activity_spieltag_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Spieltag");


        //Spieltag-Button
        buttonAddSpieltag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSpieltagButtonClick(v);
            }
        });


        //Textformat Platzkosten
        editTextPlatzkosten.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Utils.formatNumericEditText(editTextPlatzkosten);
                setBetragJeSpieler();

            }
        });
    }


    //Fokus des Edittexts ändern wenn Touch außerhalb des Edittext-Bereiches
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        setBetragJeSpieler();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    Log.d("focus", "touchevent");
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }

        return super.dispatchTouchEvent(event);
    }

    private void findViewsById() {

        buttonAddSpieltag = findViewById(R.id.activity_spieltag_button_add_spieltag);
        betragJeSpieler = findViewById(R.id.activity_spieltag_textview_betrag_je_spieler);
        editTextPlatzkosten = findViewById(R.id.activity_spieltag_editText_platzkosten);
        kostenlosSwitch = findViewById(R.id.activity_spieltag_kostenlosSwitch);

    }

    private void bottomNavBarInitialisieren() {

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_spieltag_bottom:
                        Intent intent1 = new Intent(SpieltagActivity.this, SpieltagActivity.class);
                        SpieltagActivity.this.startActivity(intent1);
                        break;
                    case R.id.action_spielerverwaltung_bottom:
                        Intent intent2 = new Intent(SpieltagActivity.this, SpielerVerwaltungActivity.class);
                        SpieltagActivity.this.startActivity(intent2);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onSpielerClickInFragment(Spieler spieler) {

        setBetragJeSpieler();
        editTextPlatzkosten.clearFocus();
        buttonAddSpieltag.requestFocus();
    }

    public void onSpieltagButtonClick(View v) {

        Calendar kalender = Calendar.getInstance();
        SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy");

        if (!editTextPlatzkosten.getText().toString().isEmpty())
            platzkosten = Float.parseFloat(editTextPlatzkosten.getText().toString().replace(',', '.'));
        else
            platzkosten = 0;

        InputMethodManager inputMethodManager;
        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if (getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        if (selectedSpieler.size() > 0 && platzkosten > 0 && !kostenlos) {

            bu_btr = platzkosten / selectedSpieler.size();

            //Datenbank-Einträge erzeugen


            for (Spieler s : selectedSpieler) {

                new SpieltagBuchenAsyncTask(this, s).execute();

            }

            new SpielerauswahlBefuellenAsyncTask(this).execute();

            Toast toast = Toast.makeText(this, "Der Spieltag wurde gebucht.", Toast.LENGTH_SHORT);
            toast.show();
            editTextPlatzkosten.setText("");
            betragJeSpieler.setText("0,00");
            kostenlosSwitch.setChecked(false);
            selectedSpieler.clear();

        } else if (selectedSpieler.size() <= 0) {

            Toast toast = Toast.makeText(this, "Es wurde kein Spieler ausgewählt.", Toast.LENGTH_SHORT);
            toast.show();

        } else if (platzkosten <= 0 && !kostenlos) {

            Toast toast = Toast.makeText(this, "Die Platzkosten wurden nicht erfasst", Toast.LENGTH_SHORT);
            toast.show();

        } else if (platzkosten > 0 && kostenlos) {

            Toast toast = Toast.makeText(this, "Es wurden Platzkosten erfasst, obwohl >Kostenlos< gewählt wurde.", Toast.LENGTH_SHORT);
            toast.show();

        } else if (platzkosten <= 0 && kostenlos) {

            for (Spieler s : selectedSpieler) {

                new SpieltagBuchenAsyncTask(this, s).execute();
            }

            new SpielerauswahlBefuellenAsyncTask(this).execute();

            Toast toast = Toast.makeText(this, "Der kostenlose Spieltag wurde gebucht. Trainigsteilnahmen aktualisiert", Toast.LENGTH_SHORT);
            toast.show();

            kostenlosSwitch.setChecked(false);
            selectedSpieler.clear();
        }
    }

    public void setBetragJeSpieler() {

        if (!editTextPlatzkosten.getText().toString().isEmpty()) {
            double platzkosten = Double.valueOf(editTextPlatzkosten.getText().toString().replace(',', '.'));
            int anzahl = selectedSpieler.size();

            if (anzahl > 0) {

                double betragDouble = platzkosten / anzahl;
                betragJeSpieler.setText(df.format(betragDouble).replace('.', ','));
            } else
                betragJeSpieler.setText(df.format(platzkosten).replace('.', ','));
        } else
            betragJeSpieler.setText("0,00");
    }

    static class SpielerauswahlBefuellenAsyncTask extends AsyncTask<Void, Void, ArrayList<Spieler>> {


        public final WeakReference<SpieltagActivity> activityReference;
        private ArrayList<Spieler> spielerList = new ArrayList<>();

        SpielerauswahlBefuellenAsyncTask(SpieltagActivity context) {
            activityReference = new WeakReference<>(context);

        }

        @Override
        protected ArrayList<Spieler> doInBackground(Void... args) {

            SpielerDataSource spielerDataSource = SpielerDataSource.getInstance();
            spielerDataSource.open();

            spielerList = spielerDataSource.getAllSpielerAbsteigendTeilnahme();
            return spielerList;
        }

        @Override
        public void onPostExecute(ArrayList<Spieler> result) {

            SpieltagActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            System.out.println("TESTASYNC");
            spielerList = result;

            //Spielerauswahl
            if (activity.fragment == null) {
                activity.fm = activity.getSupportFragmentManager();
                activity.fragment = new SpieltagActivitySpielerauswahlFragment(spielerList, activity);

                activity.fm.beginTransaction().add(R.id.activity_spieltag_spielerauswahl_fragmentContainer, activity.fragment).commitAllowingStateLoss();
            } else {
                activity.fm.beginTransaction().replace(R.id.activity_spieltag_spielerauswahl_fragmentContainer, new SpieltagActivitySpielerauswahlFragment(spielerList, activity)).commitAllowingStateLoss();

            }

        }
    }

    static class SpieltagBuchenAsyncTask extends AsyncTask<Void, Void, Buchung> {


        public final WeakReference<SpieltagActivity> activityReference;
        private Spieler spieler;

        SpieltagBuchenAsyncTask(SpieltagActivity context, Spieler spieler) {
            activityReference = new WeakReference<>(context);
            this.spieler = spieler;
        }


        @Override
        protected Buchung doInBackground(Void... args) {

            SpieltagActivity activity = activityReference.get();
            Calendar kalender = Calendar.getInstance();
            SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy");


            BuchungDataSource buchungDataSource = BuchungDataSource.getInstance();
            buchungDataSource.open();
            SpielerDataSource spielerDataSource = SpielerDataSource.getInstance();
            spielerDataSource.open();

            if (selectedSpieler.size() > 0 && activity.platzkosten > 0) {

                activity.bu_btr = activity.platzkosten / selectedSpieler.size();

                if (spieler.getHat_buchung_mm() != null) {

                    double kto_saldo_alt = buchungDataSource.getNeusteBuchungZuSpieler(spieler).getKto_saldo_neu();
                    double kto_saldo_neu = kto_saldo_alt - activity.bu_btr;

                    buchungDataSource.createBuchung(spieler.getU_id(), -activity.bu_btr, kto_saldo_alt, kto_saldo_neu, datumsformat.format(kalender.getTime()));
                    spieler = spielerDataSource.updateTeilnahmenSpieler(spieler);

                } else {

                    double kto_saldo_neu = -activity.bu_btr;
                    buchungDataSource.createBuchung(spieler.getU_id(), -activity.bu_btr, 0, kto_saldo_neu, datumsformat.format(kalender.getTime()));
                    spieler = spielerDataSource.updateHatBuchungenMM(spielerDataSource.updateTeilnahmenSpieler(spieler));
                }
            } else
                spieler = spielerDataSource.updateTeilnahmenSpieler(spieler);

            return buchungDataSource.getNeusteBuchungZuSpieler(spieler);
        }

        @Override
        public void onPostExecute(Buchung neusteBuchung) {

            if (neusteBuchung.getKto_saldo_neu() < 5)
                new EMailSendenAsyncTask(spieler, df.format(neusteBuchung.getKto_saldo_neu())).execute();
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

            System.out.println("MITTE");
            // Nachricht:
            String to = spieler.getMail();
            String from = "LBSVolleyball@merscher.eu";
            String subject = "Kontostand niedrig";
            Message msg = new MimeMessage(session);

            if (!to.isEmpty()) {
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
