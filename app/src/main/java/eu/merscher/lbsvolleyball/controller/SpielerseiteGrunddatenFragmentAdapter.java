package eu.merscher.lbsvolleyball.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.model.Grunddaten;
import eu.merscher.lbsvolleyball.model.Spieler;

public class SpielerseiteGrunddatenFragmentAdapter extends RecyclerView.Adapter<SpielerseiteGrunddatenFragmentAdapter.ViewHolder> implements SpielerseiteKontodatenFragmentAdapter.OnAddBuchungClickListener {


    private static final DecimalFormat df = new DecimalFormat("0.00");
    private final Grunddaten grunddaten;
    private final LayoutInflater inflate;
    private ViewHolder holder;


    public SpielerseiteGrunddatenFragmentAdapter(Context context, Grunddaten grunddaten) {
        this.inflate = LayoutInflater.from(context);
        Context context1 = context;
        this.grunddaten = grunddaten;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflate.inflate(R.layout.fragment_spielerseite_grunddaten_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        this.holder = holder;
        String ktoSaldoAlsString = df.format(grunddaten.getKto_saldo_neu());
        String teilnahmenAlsString = String.valueOf(grunddaten.getTeilnahmen());
        Spieler spieler = grunddaten.getSpieler();

        holder.textViewName.setText(spieler.getName());
        holder.textViewVname.setText(spieler.getVname());
        holder.textViewBdate.setText(spieler.getBdate());
        if (spieler.getMail() != null)
            holder.textViewMail.setText(spieler.getMail());
        holder.textViewKtoSaldo.setText(ktoSaldoAlsString.replace('.', ','));
        holder.textViewTeilnahmen.setText(teilnahmenAlsString);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public void onAddBuchungClick(String kto_saldo_neu) {

        System.out.println("HALOO***#############################################");
        holder.textViewKtoSaldo.setText(kto_saldo_neu.replace('.', ','));

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textViewName;
        final TextView textViewVname;
        final TextView textViewBdate;
        final TextView textViewMail;
        final TextView textViewKtoSaldo;
        final TextView textViewTeilnahmen;

        ViewHolder(View view) {
            super(view);
            textViewName = view.findViewById(R.id.fragment_spielerseite_grunddaten_textView_name);
            textViewVname = view.findViewById(R.id.fragment_spielerseite_grunddaten_textView_vname);
            textViewBdate = view.findViewById(R.id.fragment_spielerseite_grunddaten_textView_bdate);
            textViewMail = view.findViewById(R.id.fragment_spielerseite_grunddaten_textView_mail);
            textViewKtoSaldo = view.findViewById(R.id.fragment_spielerseite_grunddaten_textview_kontostand_spieler);
            textViewTeilnahmen = view.findViewById(R.id.fragment_spielerseite_grunddaten_textView_trainingsteilnahmen);
        }

    }
}
