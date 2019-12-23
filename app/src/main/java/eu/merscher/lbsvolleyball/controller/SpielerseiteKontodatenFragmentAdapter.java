package eu.merscher.lbsvolleyball.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

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
import eu.merscher.lbsvolleyball.database.DataSource;
import eu.merscher.lbsvolleyball.model.Buchung;
import eu.merscher.lbsvolleyball.model.Spieler;
import eu.merscher.lbsvolleyball.utilities.Utilities;

public class SpielerseiteKontodatenFragmentAdapter extends RecyclerView.Adapter<SpielerseiteKontodatenFragmentAdapter.ViewHolder> {

    private DataSource dataSource;

    private final ArrayList<Buchung> buchungList;
    private final Context context;
    private final LayoutInflater inflate;
    private Spieler spieler;

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        dataSource = DataSource.getInstance();

        holder.ueberschrift1.setGravity(Gravity.CENTER);
        holder.ueberschrift2.setGravity(Gravity.CENTER);
        holder.ueberschrift3.setGravity(Gravity.CENTER);

        holder.auszahlungSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                auszahlung = true;
                setAuszahlungInText(holder);
            } else {
                auszahlung = false;
                setAuszahlungInText(holder);
            }
        });

        holder.editTextAddBuchung.setOnFocusChangeListener((v, hasFocus) -> {

            Utilities.formatNumericEditText(holder.editTextAddBuchung);
            setAuszahlungInText(holder);
        });

        holder.buttonAddBuchung.setOnClickListener(view -> {

            Toast toastError;
            if (holder.editTextAddBuchung.getText().toString().isEmpty()) {
                toastError = Toast.makeText(context, "Es wurde kein Betrag zum Buchen erfasst!", Toast.LENGTH_SHORT);
                toastError.setGravity(Gravity.BOTTOM, 0, 0);
                toastError.show();
            } else {

                DecimalFormat df = new DecimalFormat("0.00");

                String bu_btr_String = df.format(Double.parseDouble(holder.editTextAddBuchung.getText().toString().replace(',', '.')));
                double bu_btr = Double.parseDouble(bu_btr_String.replace(',', '.'));
                double kto_saldo_alt;
                double kto_saldo_neu;


                Calendar kalender = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy");


                System.out.println(spieler.getName() + " " + spieler.getHat_buchung_mm() + " " + spieler.getTeilnahmen());
                dataSource.open();

                if (spieler.getHat_buchung_mm() != null) { //Wenn Buchungen für den Spieler vorhanden sind...

                    System.out.println("1");
                    Buchung buchung = dataSource.getNeusteBuchungZuSpieler(spieler);

                    kto_saldo_alt = buchung.getKto_saldo_neu(); //der vorherige Kto_Saldo_neu ist der neue Kto_Saldo_alt
                    kto_saldo_neu = kto_saldo_alt + bu_btr;
                    dataSource.createBuchung(spieler.getS_id(), bu_btr, kto_saldo_alt, kto_saldo_neu, datumsformat.format(kalender.getTime()), null, -999, "X", null, -999);

                } else { //Wenn keine Buchung vorhaden ist, ist der Startsaldo 0

                    System.out.println("2");

                    kto_saldo_alt = 0; //der vorherige Kto_Saldo_neu ist nicht vorhanden, da keine vorherige Buchung existiert, daher 0
                    kto_saldo_neu = kto_saldo_alt + bu_btr;
                    dataSource.createBuchung(spieler.getS_id(), bu_btr, kto_saldo_alt, kto_saldo_neu, datumsformat.format(kalender.getTime()), null, -999, "X", null, -999);

                }
                spieler = dataSource.updateHatBuchungenMM(spieler);

                if (kto_saldo_neu < 5)
                    new EMailSendenAsyncTask(spieler, df.format(kto_saldo_neu)).execute();

                Toast toast = Toast.makeText(context, "Buchung angelegt", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();

                ArrayList<Buchung> buchungListNeu = dataSource.getAllBuchungZuSpieler(spieler);
                SpielerseiteActivity.setBuchungList(buchungListNeu);

                if (buchungListNeu.size() > 5) {

                    holder.mehrBuchungen.setVisibility(View.VISIBLE);
                    holder.mehrBuchungenText.setVisibility(View.VISIBLE);
                    holder.mehrBuchungen.setImageResource(R.drawable.icon_down_arrow);
                    holder.mehrBuchungenText.setText(String.format(SpielerseiteActivity.resources.getString(R.string.weitere_Buchungen), buchungListNeu.size() - 5));

                    ArrayList<Buchung> buchungListKlein = new ArrayList<>();

                    for (int i = 0; i < 5; i++) {
                        buchungListKlein.add(buchungListNeu.get(i));
                    }
                    spielerKontoListViewAdapter.updateBuchungen(buchungListKlein);

                    holder.mehrBuchungen.setOnClickListener(v -> {
                        ArrayList<Buchung> buchungListGross = new ArrayList<>();

                        if (!istAusgeklappt) {

                            istAusgeklappt = true;
                            holder.mehrBuchungen.setImageResource(R.drawable.icon_up_arrow);

                            if (buchungListNeu.size() > 10) {
                                for (int i = 0; i < 10; i++) {
                                    buchungListGross.add(buchungListNeu.get(i));
                                }
                                spielerKontoListViewAdapter.updateBuchungen(buchungListGross);
                                holder.mehrBuchungenText.setText(String.format(SpielerseiteActivity.resources.getString(R.string.weitere_Buchungen), buchungListNeu.size() - 10));

                            } else {
                                spielerKontoListViewAdapter.updateBuchungen(buchungListNeu);
                                holder.mehrBuchungenText.setText(SpielerseiteActivity.resources.getString(R.string.keine_weiteren_Buchungen));
                            }

                        } else {
                            ArrayList<Buchung> buchungListKleinNeu = new ArrayList<>();

                            istAusgeklappt = false;
                            holder.mehrBuchungen.setImageResource(R.drawable.icon_down_arrow);


                            for (int i = 0; i < 5; i++) {
                                buchungListKleinNeu.add(buchungListNeu.get(i));
                            }
                            spielerKontoListViewAdapter.updateBuchungen(buchungListKleinNeu);
                            holder.mehrBuchungenText.setText(String.format(SpielerseiteActivity.resources.getString(R.string.weitere_Buchungen), buchungListNeu.size() - 5));
                        }
                    });
                } else
                    spielerKontoListViewAdapter.updateBuchungen(buchungListNeu);


                //Kto_Saldo_Neu auf Grundseitenfragment aktualisieren
                onBuchungListener = SpielerseiteGrunddatenFragmentAdapter.getOnBuchungListener();
                onBuchungListener.onBuchung(df.format(dataSource.getNeusteBuchungZuSpieler(spieler).getKto_saldo_neu()));

                holder.editTextAddBuchung.setText("");
                holder.editTextAddBuchung.clearFocus();
                holder.buttonAddBuchung.requestFocus();
                holder.auszahlungSwitch.setChecked(false);
            }
        });

        if (buchungList.size() > 5) {

            holder.mehrBuchungen.setVisibility(View.VISIBLE);
            holder.mehrBuchungenText.setVisibility(View.VISIBLE);

            holder.mehrBuchungenText.setText(String.format(SpielerseiteActivity.resources.getString(R.string.weitere_Buchungen), buchungList.size() - 5));
            ArrayList<Buchung> buchungListKlein = new ArrayList<>();

            for (int i = 0; i < 5; i++) {
                buchungListKlein.add(buchungList.get(i));
            }
            spielerKontoListViewAdapter = new SpielerKontoListViewAdapter(context, buchungListKlein);
            holder.buchungListView.setAdapter(spielerKontoListViewAdapter);

            holder.mehrBuchungen.setOnClickListener(v -> {
                ArrayList<Buchung> buchungListGross = new ArrayList<>();

                if (!istAusgeklappt) {

                    istAusgeklappt = true;
                    holder.mehrBuchungen.setImageResource(R.drawable.icon_up_arrow);


                    if (buchungList.size() > 10) {
                        for (int i = 0; i < 10; i++) {
                            buchungListGross.add(buchungList.get(i));
                        }
                        spielerKontoListViewAdapter.updateBuchungen(buchungListGross);
                        holder.mehrBuchungenText.setText(String.format(SpielerseiteActivity.resources.getString(R.string.weitere_Buchungen), buchungList.size() - 10));

                    } else {
                        spielerKontoListViewAdapter.updateBuchungen(buchungList);
                        holder.mehrBuchungenText.setText(SpielerseiteActivity.resources.getString(R.string.keine_weiteren_Buchungen));
                    }

                } else {
                    ArrayList<Buchung> buchungListKleinNeu = new ArrayList<>();

                    istAusgeklappt = false;
                    holder.mehrBuchungen.setImageResource(R.drawable.icon_down_arrow);
                    holder.mehrBuchungenText.setText(String.format(SpielerseiteActivity.resources.getString(R.string.weitere_Buchungen), buchungList.size() - 5));


                    for (int i = 0; i < 5; i++) {
                        buchungListKleinNeu.add(buchungList.get(i));
                    }
                    spielerKontoListViewAdapter.updateBuchungen(buchungListKleinNeu);

                }
            });
            holder.mehrBuchungenText.setOnClickListener(v -> {

            });
        } else {
            spielerKontoListViewAdapter = new SpielerKontoListViewAdapter(context, buchungList);
            holder.buchungListView.setAdapter(spielerKontoListViewAdapter);
        }


    }

    private SpielerKontoListViewAdapter spielerKontoListViewAdapter;
    private OnBuchungListener onBuchungListener;
    private boolean auszahlung = false;
    private boolean istAusgeklappt = false;

    SpielerseiteKontodatenFragmentAdapter(Context context, ArrayList<Buchung> buchungList, Spieler spieler) {
        this.inflate = LayoutInflater.from(context);
        this.context = context;
        this.buchungList = buchungList;
        this.spieler = spieler;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {

        View view = inflate.inflate(R.layout.fragment_spielerseite_kontodaten_item, parent, false);
        return new ViewHolder(view);
    }

    public interface OnBuchungListener {
        void onBuchung(String saldo);
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
        final TextView ueberschrift4;
        final ImageButton mehrBuchungen;
        final TextView mehrBuchungenText;


        ViewHolder(View view) {
            super(view);
            buchungListView = view.findViewById(R.id.listView_buchungen);
            editTextAddBuchung = view.findViewById(R.id.fragment_kontodaten_editText_addBuchung);
            buttonAddBuchung = view.findViewById(R.id.fragment_spielerkonto_addButton);
            auszahlungSwitch = view.findViewById(R.id.fragment_kontodaten_editText_switch);
            ueberschrift1 = view.findViewById(R.id.fragment_spielerseite_kontodaten_item_ueberschrift1);
            ueberschrift2 = view.findViewById(R.id.fragment_spielerseite_kontodaten_item_ueberschrift2);
            ueberschrift3 = view.findViewById(R.id.fragment_spielerseite_kontodaten_item_ueberschrift3);
            ueberschrift4 = view.findViewById(R.id.fragment_spielerseite_kontodaten_item_ueberschrift4);
            mehrBuchungen = view.findViewById(R.id.mehrBuchungen);
            mehrBuchungen.setVisibility(View.INVISIBLE);
            mehrBuchungenText = view.findViewById(R.id.mehrBuchungenText);
            mehrBuchungenText.setVisibility(View.INVISIBLE);
        }

    }

    public class SpielerKontoListViewAdapter extends BaseAdapter {

        private final DecimalFormat df = new DecimalFormat("0.00");
        private ArrayList<Buchung> buchungList;
        private Activity activity = null;
        private Fragment fragment = null;
        private Context context;


        SpielerKontoListViewAdapter(Context context, ArrayList<Buchung> buchungList) {
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

        @SuppressLint("InflateParams")
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
                holder.buchungTyp = convertView.findViewById(R.id.typ);


                convertView.setTag(holder);
            } else {
                holder = (SpielerseiteKontodatenFragmentAdapter.SpielerKontoListViewAdapter.ViewHolder) convertView.getTag();
            }

            Buchung buchung = buchungList.get(position);
            holder.buchungDatum.setText(buchung.getBu_date());

            if (buchung.getIst_manuell_mm() != null) {
                holder.buchungTyp.setImageBitmap(BitmapFactory.decodeResource(SpielerseiteActivity.resources, R.drawable.typ_manuell));
            } else if (buchung.getIst_training_mm() != null) {
                holder.buchungTyp.setImageBitmap(BitmapFactory.decodeResource(SpielerseiteActivity.resources, R.drawable.typ_training));
            } else if (buchung.getIst_tunier_mm() != null) {
                holder.buchungTyp.setImageBitmap(BitmapFactory.decodeResource(SpielerseiteActivity.resources, R.drawable.typ_tunier));
            }

            holder.buchungBetrag.setText(df.format(buchung.getBu_btr()));
            holder.ktoSaldoNeu.setText(df.format(buchung.getKto_saldo_neu()));

            holder.buchungBetrag.setGravity(Gravity.END);
            holder.buchungDatum.setGravity(Gravity.START);
            holder.ktoSaldoNeu.setGravity(Gravity.END);
            //holder.buchungTyp.setForegroundGravity(Gravity.CENTER);

            if (buchung.getBu_btr() < 0)
                holder.buchungBetrag.setTextColor(Color.parseColor("#AE1732"));
            else
                holder.buchungBetrag.setTextColor(Color.parseColor("#86C06A"));


            if (buchung.getKto_saldo_neu() < 0)
                holder.ktoSaldoNeu.setTextColor(Color.parseColor("#AE1732"));
            else
                holder.ktoSaldoNeu.setTextColor(Color.parseColor("#1B1B1B"));

            return convertView;
        }

        void updateBuchungen(ArrayList<Buchung> list) {
            this.buchungList = list;
            notifyDataSetChanged();
        }

        private class ViewHolder {
            TextView buchungDatum;
            TextView buchungBetrag;
            TextView ktoSaldoNeu;
            ImageView buchungTyp;
        }

    }
}
