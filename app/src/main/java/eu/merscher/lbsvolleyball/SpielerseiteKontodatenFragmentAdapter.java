package eu.merscher.lbsvolleyball;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

public class SpielerseiteKontodatenFragmentAdapter extends RecyclerView.Adapter<SpielerseiteKontodatenFragmentAdapter.ViewHolder> {


    private final ArrayList<Buchung> buchungList;
    private final Context context;
    private final LayoutInflater inflate;
    private Spieler spieler;
    private SpielerDataSource spielerDataSource;
    private BuchungDataSource buchungDataSource;
    private SpielerKontoListViewAdapter spielerKontoListViewAdapter;
    private boolean auszahlung = false;

    SpielerseiteKontodatenFragmentAdapter(Context context, ArrayList<Buchung> buchungList, Spieler spieler) {
        this.inflate = LayoutInflater.from(context);
        this.context = context;
        this.buchungList = buchungList;
        this.spieler = spieler;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflate.inflate(R.layout.fragment_spielerseite_kontodaten_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ViewHolder holder1 = holder;
        buchungDataSource = BuchungDataSource.getInstance();
        spielerDataSource = SpielerDataSource.getInstance();

        holder.auszahlungSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    auszahlung = isChecked;
                    setAuszahlungInText(holder);
                } else {
                    auszahlung = false;
                    setAuszahlungInText(holder);
                }
            }
        });

        holder.editTextAddBuchung.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                Utils.formatNumericEditText(holder.editTextAddBuchung);
                setAuszahlungInText(holder);
            }
        });

        holder.buttonAddBuchung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast toastError;
                if (holder.editTextAddBuchung.getText().toString().isEmpty()) {
                    toastError = Toast.makeText(context, "Es wurde kein Betrag zum Buchen erfasst!", Toast.LENGTH_SHORT);
                    toastError.show();
                } else {

                    DecimalFormat df = new DecimalFormat("0.00");

                    String bu_btr_String = df.format(Double.parseDouble(holder.editTextAddBuchung.getText().toString().replace(',', '.')));
                    double bu_btr = Double.parseDouble(bu_btr_String.replace(',', '.'));
                    double kto_saldo_alt;
                    double kto_saldo_neu;


                    Calendar kalender = Calendar.getInstance();
                    SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy");


                    System.out.println(spieler.getName() + " " + spieler.getHat_buchung_mm() + " " + spieler.getTeilnahmen());
                    buchungDataSource.open();
                    spielerDataSource.open();
                    if (spieler.getHat_buchung_mm() != null) { //Wenn Buchungen für den Spieler vorhanden sind...

                        System.out.println("1");
                        Buchung buchung = buchungDataSource.getNeusteBuchungZuSpieler(spieler);

                        kto_saldo_alt = buchung.getKto_saldo_neu(); //der vorherige Kto_Saldo_neu ist der neue Kto_Saldo_alt
                        kto_saldo_neu = kto_saldo_alt + bu_btr;
                        buchungDataSource.createBuchung(spieler.getU_id(), bu_btr, kto_saldo_alt, kto_saldo_neu, datumsformat.format(kalender.getTime()));

                    } else { //Wenn keine Buchung vorhaden ist, ist der Startsaldo 0

                        System.out.println("2");

                        kto_saldo_alt = 0; //der vorherige Kto_Saldo_neu ist nicht vorhanden, da keine vorherige Buchung existiert, daher 0
                        kto_saldo_neu = kto_saldo_alt + bu_btr;
                        buchungDataSource.createBuchung(spieler.getU_id(), bu_btr, kto_saldo_alt, kto_saldo_neu, datumsformat.format(kalender.getTime()));

                    }
                    spieler = spielerDataSource.updateHatBuchungenMM(spieler);

                    if (kto_saldo_neu < 5)
                        new EMailSendenAsyncTask(spieler, df.format(kto_saldo_neu)).execute();

                    Toast toast = Toast.makeText(context, "Buchung angelegt", Toast.LENGTH_SHORT);
                    toast.show();

                    spielerKontoListViewAdapter.updateBuchungen(buchungDataSource.getAllBuchungZuSpieler(spieler));

                    holder.editTextAddBuchung.setText("");
                    holder.editTextAddBuchung.clearFocus();
                    holder.buttonAddBuchung.requestFocus();
                    holder.auszahlungSwitch.setChecked(false);
                }
            }

        });

        spielerKontoListViewAdapter = new SpielerKontoListViewAdapter(context, buchungList);
        holder.buchungListView.setAdapter(spielerKontoListViewAdapter);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    private void setAuszahlungInText(ViewHolder holder) {

        String s = holder.editTextAddBuchung.getText().toString();
        if (auszahlung) {
            if (!s.isEmpty()) {
                if (!s.contains("-"))
                    s = "-" + s;
                holder.editTextAddBuchung.setText(s);
            }
        } else {
            if (!s.isEmpty()) {
                if (s.contains("-"))
                    s = s.replace("-", "");
                holder.editTextAddBuchung.setText(s);
            }
        }
    }


    public interface OnAddBuchungClickListener {
        void onAddBuchungClick(String kto_saldo_neu);
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        final ListView buchungListView;
        final EditText editTextAddBuchung;
        final FloatingActionButton buttonAddBuchung;
        final Switch auszahlungSwitch;


        ViewHolder(View view) {
            super(view);
            buchungListView = view.findViewById(R.id.listView_buchungen);
            editTextAddBuchung = view.findViewById(R.id.fragment_kontodaten_editText_addBuchung);
            buttonAddBuchung = view.findViewById(R.id.fragment_spielerkonto_addButton);
            auszahlungSwitch = view.findViewById(R.id.fragment_kontodaten_editText_switch);

        }

    }
}
