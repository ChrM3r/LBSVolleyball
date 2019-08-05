package eu.merscher.lbsvolleyball.controller;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
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

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.database.BuchungDataSource;
import eu.merscher.lbsvolleyball.database.SpielerDataSource;
import eu.merscher.lbsvolleyball.model.Buchung;
import eu.merscher.lbsvolleyball.model.Spieler;
import eu.merscher.lbsvolleyball.utilities.Utils;

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

        holder.ueberschrift1.setGravity(Gravity.CENTER);
        holder.ueberschrift2.setGravity(Gravity.CENTER);
        holder.ueberschrift3.setGravity(Gravity.CENTER);

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
                        buchungDataSource.createBuchung(spieler.getU_id(), bu_btr, kto_saldo_alt, kto_saldo_neu, datumsformat.format(kalender.getTime()), null, "X", null);

                    } else { //Wenn keine Buchung vorhaden ist, ist der Startsaldo 0

                        System.out.println("2");

                        kto_saldo_alt = 0; //der vorherige Kto_Saldo_neu ist nicht vorhanden, da keine vorherige Buchung existiert, daher 0
                        kto_saldo_neu = kto_saldo_alt + bu_btr;
                        buchungDataSource.createBuchung(spieler.getU_id(), bu_btr, kto_saldo_alt, kto_saldo_neu, datumsformat.format(kalender.getTime()), null, "X", null);

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

            if (to != null) {
                try {
                    msg.setFrom(new InternetAddress(from));
                    msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
                    msg.setSubject(subject);

                    msg.setText("Hi " + spieler.getVname()
                            + ",\n\nder Kontostand deines Spielerkontos beträgt " + kontostand
                            + "€. Bitte zahle demnächst wieder etwas ein.\n\nVielen Dank und sportliche Grüße\nLBS Volleyball App");

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
        final TextView ueberschrift1;
        final TextView ueberschrift2;
        final TextView ueberschrift3;


        ViewHolder(View view) {
            super(view);
            buchungListView = view.findViewById(R.id.listView_buchungen);
            editTextAddBuchung = view.findViewById(R.id.fragment_kontodaten_editText_addBuchung);
            buttonAddBuchung = view.findViewById(R.id.fragment_spielerkonto_addButton);
            auszahlungSwitch = view.findViewById(R.id.fragment_kontodaten_editText_switch);
            ueberschrift1 = view.findViewById(R.id.fragment_spielerseite_kontodaten_item_ueberschrift1);
            ueberschrift2 = view.findViewById(R.id.fragment_spielerseite_kontodaten_item_ueberschrift2);
            ueberschrift3 = view.findViewById(R.id.fragment_spielerseite_kontodaten_item_ueberschrift3);
        }

    }

    public class SpielerKontoListViewAdapter extends BaseAdapter {

        private final DecimalFormat df = new DecimalFormat("0.00");
        private ArrayList<Buchung> buchungList;
        private Activity activity;
        private Fragment fragment;
        private Context context;


        public SpielerKontoListViewAdapter(Activity activity, ArrayList<Buchung> buchungList) {
            super();
            this.activity = activity;
            this.buchungList = buchungList;
        }

        public SpielerKontoListViewAdapter(Fragment fragment, ArrayList<Buchung> buchungList) {
            super();
            this.fragment = fragment;
            this.buchungList = buchungList;
        }

        public SpielerKontoListViewAdapter(Context context, ArrayList<Buchung> buchungList) {
            super();
            this.context = context;
            this.buchungList = buchungList;
        }

        @Override
        public int getCount() {
            return buchungList.size();
        }

        @Override
        public Object getItem(int position) {
            return buchungList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {


            SpielerseiteKontodatenFragmentAdapter.SpielerKontoListViewAdapter.ViewHolder holder;
            LayoutInflater inflater;
            if (activity == null && fragment == null)
                inflater = LayoutInflater.from(context);
            else if (context == null && fragment == null)
                inflater = activity.getLayoutInflater();
            else
                inflater = fragment.getLayoutInflater();

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.fragment_spielerseite_kontodaten_item_listview_row, null);
                holder = new SpielerseiteKontodatenFragmentAdapter.SpielerKontoListViewAdapter.ViewHolder();
                holder.buchungDatum = convertView.findViewById(R.id.datum);
                holder.buchungBetrag = convertView.findViewById(R.id.betrag);
                holder.ktoSaldoNeu = convertView.findViewById(R.id.saldo);
                convertView.setTag(holder);
            } else {
                holder = (SpielerseiteKontodatenFragmentAdapter.SpielerKontoListViewAdapter.ViewHolder) convertView.getTag();
            }

            Buchung buchung = buchungList.get(position);
            holder.buchungDatum.setText(buchung.getBu_date());
            holder.buchungBetrag.setText(df.format(buchung.getBu_btr()));
            holder.ktoSaldoNeu.setText(df.format(buchung.getKto_saldo_neu()));

            holder.buchungBetrag.setGravity(Gravity.CENTER);
            holder.buchungDatum.setGravity(Gravity.CENTER);
            holder.ktoSaldoNeu.setGravity(Gravity.CENTER);

            return convertView;
        }

        public void updateBuchungen(ArrayList<Buchung> list) {
            this.buchungList = list;
            notifyDataSetChanged();
        }

        private class ViewHolder {
            TextView buchungDatum;
            TextView buchungBetrag;
            TextView ktoSaldoNeu;
        }
    }
}
