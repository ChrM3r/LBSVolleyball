package eu.merscher.lbsvolleyball.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.model.Spieler;

public class SpielerVerwaltungAdapter extends BaseAdapter implements ListAdapter {

    private final Context context;
    private ArrayList<Spieler> spielerList;
    private ArrayList<String> spielerNamen;
    private ArrayList<String> spielerGeburtstage;
    private ArrayList<Bitmap> spielerFotos;


    SpielerVerwaltungAdapter(ArrayList<Spieler> spielerList, ArrayList<String> spielerNamen, ArrayList<Bitmap> spielerFotos, ArrayList<String> spielerGeburtstage, Context context) {
        this.spielerList = spielerList;
        this.spielerNamen = spielerNamen;
        this.spielerFotos = spielerFotos;
        this.spielerGeburtstage = spielerGeburtstage;
        this.context = context;
    }

    @Override
    public int getCount() {
        return spielerNamen.size();
    }

    @Override
    public Object getItem(int pos) {
        return spielerNamen.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = Objects.requireNonNull(inflater).inflate(R.layout.activity_spielerverwaltung_list_view, null);
        }

        TextView listSpielerName = view.findViewById(R.id.list_item_string);
        listSpielerName.setText(spielerNamen.get(position));

        TextView listSpielerGeburtstage = view.findViewById(R.id.list_item_string_sub);
        listSpielerGeburtstage.setText(spielerGeburtstage.get(position));

        ImageView spielerBild = view.findViewById(R.id.spielerBild);

        if (spielerList.get(position).getFoto().equals("avatar_m"))
            spielerBild.setImageResource(R.drawable.avatar_m);
        else
            spielerBild.setImageBitmap(spielerFotos.get(position));

        return view;

    }


}
