package eu.merscher.lbsvolleyball;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class SpielerKontoListViewAdapter extends BaseAdapter {

    private ArrayList<Buchung> buchungList;
    private Activity activity;
    private Fragment fragment;
    private static DecimalFormat df = new DecimalFormat("0.00");


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

    private class ViewHolder {
        TextView buchungDatum;
        TextView buchungBetrag;
        TextView ktoSaldoNeu;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        ViewHolder holder;
        LayoutInflater inflater;
        if (activity == null)
            inflater = fragment.getLayoutInflater();
        else
            inflater = activity.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_row, null);
            holder = new ViewHolder();
            holder.buchungDatum = (TextView) convertView.findViewById(R.id.datum);
            holder.buchungBetrag = (TextView) convertView.findViewById(R.id.betrag);
            holder.ktoSaldoNeu = (TextView) convertView.findViewById(R.id.saldo);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Buchung buchung = buchungList.get(position);
        holder.buchungDatum.setText(buchung.getBu_date());
        holder.buchungBetrag.setText(df.format(buchung.getBu_btr()));
        holder.ktoSaldoNeu.setText(df.format(buchung.getKto_saldo_neu()));

        return convertView;
    }
}