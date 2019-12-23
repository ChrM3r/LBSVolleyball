package eu.merscher.lbsvolleyball.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.model.Spieler;

public class SpielerseiteGrunddatenFragmentAdapter extends RecyclerView.Adapter<SpielerseiteGrunddatenFragmentAdapter.ViewHolder> implements SpielerseiteKontodatenFragmentAdapter.OnBuchungListener {


    private static final DecimalFormat df = new DecimalFormat("0.00");
    private Spieler spieler;
    private double kto_saldo_neu;
    private int teilnahmen;
    private final LayoutInflater inflate;
    private ViewHolder holder;
    private static SpielerseiteKontodatenFragmentAdapter.OnBuchungListener onBuchungListener;


    static SpielerseiteKontodatenFragmentAdapter.OnBuchungListener getOnBuchungListener() {
        return onBuchungListener;
    }

    SpielerseiteGrunddatenFragmentAdapter(Context context, Spieler spieler, double kto_saldo_neu, int teilnahmen) {
        this.inflate = LayoutInflater.from(context);
        this.spieler = spieler;
        this.kto_saldo_neu = kto_saldo_neu;
        this.teilnahmen = teilnahmen;
    }

    @Override
    public void onBuchung(String saldo) {
        holder.textViewKtoSaldo.setText(saldo.replace('.', ','));
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {

        View view = inflate.inflate(R.layout.fragment_spielerseite_grunddaten_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        this.holder = holder;
        onBuchungListener = this;
        String ktoSaldoAlsString = df.format(kto_saldo_neu);
        String teilnahmenAlsString = String.valueOf(teilnahmen);

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
