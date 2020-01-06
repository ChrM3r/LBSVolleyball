package eu.merscher.lbsvolleyball.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.database.DataSource;
import eu.merscher.lbsvolleyball.model.Buchung;
import eu.merscher.lbsvolleyball.utilities.Utilities;

public class TeamkontoKontodatenFragmentAdapter extends RecyclerView.Adapter<TeamkontoKontodatenFragmentAdapter.ViewHolder> {

    private DataSource dataSource;

    private final ArrayList<Buchung> buchungList;
    private final Context context;
    private final LayoutInflater inflate;
    private SpielerKontoListViewAdapter spielerKontoListViewAdapter;
    private boolean auszahlung = false;
    private boolean istAusgeklappt = false;

    TeamkontoKontodatenFragmentAdapter(Context context, ArrayList<Buchung> buchungList) {
        this.inflate = LayoutInflater.from(context);
        this.context = context;
        this.buchungList = buchungList;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {

        View view = inflate.inflate(R.layout.fragment_teamkonto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {

        dataSource = DataSource.getInstance();

        DecimalFormat df = new DecimalFormat("0.00");

        if (dataSource.getNeusteBuchungZuTeamkonto() != null) {

            double kto_saldo_neu = dataSource.getNeusteBuchungZuTeamkonto().getKto_saldo_neu();
            String ktoSaldoString = df.format(kto_saldo_neu).replace(".", ",");

            if ((ktoSaldoString.equals("-0,00") || ktoSaldoString.equals("0,00")) && (kto_saldo_neu != 0)) {
                holder.kontosaldo.setText(context.getResources().getString(R.string.rund_0));
            } else
                holder.kontosaldo.setText(ktoSaldoString);
        } else
            holder.kontosaldo.setText(context.getResources().getString(R.string.betrag_0));


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

                String bu_btr_String = df.format(Double.parseDouble(holder.editTextAddBuchung.getText().toString().replace(',', '.')));
                double bu_btr = Double.parseDouble(bu_btr_String.replace(',', '.'));
                double kto_saldo_alt;
                double kto_saldo_neu;


                Calendar kalender = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy");


                dataSource.open();

                if (dataSource.getNeusteBuchungZuTeamkonto() != null) { //Wenn Buchungen für den Spieler vorhanden sind...

                    Buchung buchung = dataSource.getNeusteBuchungZuTeamkonto();

                    kto_saldo_alt = buchung.getKto_saldo_neu(); //der vorherige Kto_Saldo_neu ist der neue Kto_Saldo_alt
                    kto_saldo_neu = kto_saldo_alt + bu_btr;
                    dataSource.createBuchungAufTeamkonto(bu_btr, kto_saldo_alt, kto_saldo_neu, datumsformat.format(kalender.getTime()), null, -999, "X", null, -999, null, -999);

                } else { //Wenn keine Buchung vorhaden ist, ist der Startsaldo 0


                    kto_saldo_alt = 0; //der vorherige Kto_Saldo_neu ist nicht vorhanden, da keine vorherige Buchung existiert, daher 0
                    kto_saldo_neu = kto_saldo_alt + bu_btr;
                    dataSource.createBuchungAufTeamkonto(bu_btr, kto_saldo_alt, kto_saldo_neu, datumsformat.format(kalender.getTime()), null, -999, "X", null, -999, null, -999);

                }

                Toast toast = Toast.makeText(context, "Buchung angelegt", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();

                ArrayList<Buchung> buchungListNeu = dataSource.getAllBuchungZuTeamkonto();
                SpielerseiteActivity.setBuchungList(buchungListNeu);

                if (buchungListNeu.size() > 0) {
                    holder.cardView_legende.setVisibility(View.VISIBLE);
                    holder.cardView_header.setVisibility(View.VISIBLE);
                    holder.cardView_listview.setVisibility(View.VISIBLE);
                }

                if (buchungListNeu.size() > 5) {

                    holder.mehrBuchungen.setVisibility(View.VISIBLE);
                    holder.mehrBuchungenText.setVisibility(View.VISIBLE);
                    holder.mehrBuchungen.setImageResource(R.drawable.icon_down_arrow);
                    holder.mehrBuchungenText.setText(String.format(context.getResources().getString(R.string.weitere_Buchungen), buchungListNeu.size() - 5));

                    ArrayList<Buchung> buchungListKlein = new ArrayList<>();

                    for (int i = 0; i < 5; i++) {
                        buchungListKlein.add(buchungListNeu.get(i));
                    }
                    spielerKontoListViewAdapter.updateBuchungen(buchungListKlein);
                    Utilities.setListViewHeightNachInhalt(holder.buchungListView);

                    holder.mehrBuchungen.setOnClickListener(v -> {

                        if (!istAusgeklappt) {

                            istAusgeklappt = true;
                            holder.mehrBuchungen.setImageResource(R.drawable.icon_up_arrow);


                            spielerKontoListViewAdapter.updateBuchungen(buchungListNeu);
                            Utilities.setListViewHeightNachInhalt(holder.buchungListView);

                            holder.mehrBuchungenText.setVisibility(View.GONE);


                        } else {
                            ArrayList<Buchung> buchungListKleinNeu = new ArrayList<>();

                            istAusgeklappt = false;
                            holder.mehrBuchungen.setImageResource(R.drawable.icon_down_arrow);


                            for (int i = 0; i < 5; i++) {
                                buchungListKleinNeu.add(buchungListNeu.get(i));
                            }
                            spielerKontoListViewAdapter.updateBuchungen(buchungListKleinNeu);
                            Utilities.setListViewHeightNachInhalt(holder.buchungListView);
                            holder.mehrBuchungenText.setVisibility(View.VISIBLE);
                            holder.mehrBuchungenText.setText(String.format(context.getResources().getString(R.string.weitere_Buchungen), buchungListNeu.size() - 5));
                        }
                    });
                } else {
                    spielerKontoListViewAdapter.updateBuchungen(buchungListNeu);
                    Utilities.setListViewHeightNachInhalt(holder.buchungListView);
                }

                holder.kontosaldo.setText(df.format(dataSource.getNeusteBuchungZuTeamkonto().getKto_saldo_neu()));
                holder.editTextAddBuchung.setText("");
                holder.editTextAddBuchung.clearFocus();
                holder.buttonAddBuchung.requestFocus();
                holder.auszahlungSwitch.setChecked(false);
            }
        });

        holder.buchungListView.setOnItemClickListener(null);
        holder.buchungListView.setEnabled(false);

        if (buchungList.size() > 0) {
            holder.cardView_legende.setVisibility(View.VISIBLE);
            holder.cardView_header.setVisibility(View.VISIBLE);
            holder.cardView_listview.setVisibility(View.VISIBLE);
        }

        if (buchungList.size() > 5) {

            holder.mehrBuchungen.setVisibility(View.VISIBLE);
            holder.mehrBuchungenText.setVisibility(View.VISIBLE);

            holder.mehrBuchungenText.setText(String.format(context.getResources().getString(R.string.weitere_Buchungen), buchungList.size() - 5));
            ArrayList<Buchung> buchungListKlein = new ArrayList<>();

            for (int i = 0; i < 5; i++) {
                buchungListKlein.add(buchungList.get(i));
            }
            spielerKontoListViewAdapter = new SpielerKontoListViewAdapter(context, buchungListKlein);
            holder.buchungListView.setAdapter(spielerKontoListViewAdapter);
            Utilities.setListViewHeightNachInhalt(holder.buchungListView);


            holder.mehrBuchungen.setOnClickListener(v -> {

                if (!istAusgeklappt) {

                    istAusgeklappt = true;
                    holder.mehrBuchungen.setImageResource(R.drawable.icon_up_arrow);
                    spielerKontoListViewAdapter.updateBuchungen(buchungList);
                    Utilities.setListViewHeightNachInhalt(holder.buchungListView);
                    holder.mehrBuchungenText.setVisibility(View.GONE);


                } else {
                    ArrayList<Buchung> buchungListKleinNeu = new ArrayList<>();

                    istAusgeklappt = false;
                    holder.mehrBuchungen.setImageResource(R.drawable.icon_down_arrow);
                    holder.mehrBuchungenText.setVisibility(View.VISIBLE);
                    holder.mehrBuchungenText.setText(String.format(context.getResources().getString(R.string.weitere_Buchungen), buchungList.size() - 5));


                    for (int i = 0; i < 5; i++) {
                        buchungListKleinNeu.add(buchungList.get(i));
                    }
                    spielerKontoListViewAdapter.updateBuchungen(buchungListKleinNeu);
                    Utilities.setListViewHeightNachInhalt(holder.buchungListView);

                }
            });
            holder.mehrBuchungenText.setOnClickListener(v -> {

            });
        } else {
            spielerKontoListViewAdapter = new SpielerKontoListViewAdapter(context, buchungList);
            holder.buchungListView.setAdapter(spielerKontoListViewAdapter);
            Utilities.setListViewHeightNachInhalt(holder.buchungListView);

        }


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

    public class ViewHolder extends RecyclerView.ViewHolder {

        final TextView kontosaldo;
        final ListView buchungListView;
        final EditText editTextAddBuchung;
        final FloatingActionButton buttonAddBuchung;
        final Switch auszahlungSwitch;
        final ImageButton mehrBuchungen;
        final TextView mehrBuchungenText;
        final CardView cardView_legende;
        final CardView cardView_header;
        final CardView cardView_listview;


        ViewHolder(View view) {
            super(view);
            kontosaldo = view.findViewById(R.id.kontostand_teamkonto);
            buchungListView = view.findViewById(R.id.listView_buchungen_teamkonto);
            editTextAddBuchung = view.findViewById(R.id.teamkonto_kontodaten_editText_addBuchung);
            auszahlungSwitch = view.findViewById(R.id.teamkonto_kontodaten_editText_switch);
            buttonAddBuchung = view.findViewById(R.id.teamkonto_addButton);
            mehrBuchungen = view.findViewById(R.id.mehrBuchungen_teamkonto);
            mehrBuchungenText = view.findViewById(R.id.mehrBuchungenText_teamkonto);
            cardView_legende = view.findViewById(R.id.card5_teamkonto);
            cardView_header = view.findViewById(R.id.card3_teamkonto);
            cardView_listview = view.findViewById(R.id.card2_teamkonto);
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


            TeamkontoKontodatenFragmentAdapter.SpielerKontoListViewAdapter.ViewHolder holder;
            LayoutInflater inflater;
            if (activity == null && fragment == null)
                inflater = LayoutInflater.from(context);
            else if (context == null && fragment == null)
                inflater = activity.getLayoutInflater();
            else
                inflater = fragment.getLayoutInflater();

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.fragment_spielerseite_kontodaten_item_listview_row, null);
                holder = new TeamkontoKontodatenFragmentAdapter.SpielerKontoListViewAdapter.ViewHolder();
                holder.buchungDatum = convertView.findViewById(R.id.datum);
                holder.buchungBetrag = convertView.findViewById(R.id.betrag);
                holder.ktoSaldoNeu = convertView.findViewById(R.id.saldo);
                holder.buchungTyp = convertView.findViewById(R.id.typ);


                convertView.setTag(holder);
            } else {
                holder = (TeamkontoKontodatenFragmentAdapter.SpielerKontoListViewAdapter.ViewHolder) convertView.getTag();
            }

            Buchung buchung = buchungList.get(position);
            holder.buchungDatum.setText(buchung.getBu_date());

            if (buchung.getIst_manuell_mm() != null)
                holder.buchungTyp.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.typ_manuell));
            else if (buchung.getIst_training_mm() != null)
                holder.buchungTyp.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.typ_training));
            else if (buchung.getIst_tunier_mm() != null)
                holder.buchungTyp.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.typ_tunier));
            else if (buchung.getIst_geloeschter_spieler_mm() != null)
                holder.buchungTyp.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.typ_spieler_geloescht));


            if ((df.format(buchung.getBu_btr()).equals("-0,00") || df.format(buchung.getBu_btr()).equals("0,00")) && !(df.format(buchung.getKto_saldo_neu()).equals("-0,00") || df.format(buchung.getKto_saldo_neu()).equals("0,00")) && (buchung.getBu_btr() != 0 && buchung.getKto_saldo_neu() != 0)) {
                holder.buchungBetrag.setText(context.getResources().getString(R.string.rund_0));
                holder.ktoSaldoNeu.setText(df.format(buchung.getKto_saldo_neu()));

            } else if (!(df.format(buchung.getBu_btr()).equals("-0,00") || df.format(buchung.getBu_btr()).equals("0,00")) && (df.format(buchung.getKto_saldo_neu()).equals("-0,00") || df.format(buchung.getKto_saldo_neu()).equals("0,00")) && (buchung.getBu_btr() != 0 && buchung.getKto_saldo_neu() != 0)) {
                holder.buchungBetrag.setText(df.format(buchung.getBu_btr()));
                holder.ktoSaldoNeu.setText(context.getResources().getString(R.string.rund_0));

            } else if ((df.format(buchung.getBu_btr()).equals("-0,00") || df.format(buchung.getBu_btr()).equals("0,00")) && (df.format(buchung.getKto_saldo_neu()).equals("-0,00") || df.format(buchung.getKto_saldo_neu()).equals("0,00")) && (buchung.getBu_btr() != 0 && buchung.getKto_saldo_neu() != 0)) {
                holder.buchungBetrag.setText(context.getResources().getString(R.string.rund_0));
                holder.ktoSaldoNeu.setText(context.getResources().getString(R.string.rund_0));

            } else {
                holder.buchungBetrag.setText(df.format(buchung.getBu_btr()));
                holder.ktoSaldoNeu.setText(df.format(buchung.getKto_saldo_neu()));
            }


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
