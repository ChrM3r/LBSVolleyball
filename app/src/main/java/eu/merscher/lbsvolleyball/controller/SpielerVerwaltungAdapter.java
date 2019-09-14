package eu.merscher.lbsvolleyball.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.model.Spieler;

public class SpielerVerwaltungAdapter extends BaseAdapter implements ListAdapter {

    private final Context context;
    private ArrayList<Spieler> spielerList = new ArrayList<Spieler>();
    private ArrayList<String> spielerNamen = new ArrayList<String>();
    private ArrayList<String> spielerGeburtstage = new ArrayList<String>();
    private ArrayList<Bitmap> spielerFotos = new ArrayList<Bitmap>();
    private ListView spielerListView;


    public SpielerVerwaltungAdapter(ArrayList<Spieler> spielerList, ArrayList<String> spielerNamen, ArrayList<Bitmap> spielerFotos, ArrayList<String> spielerGeburtstage, Context context) {
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activity_spielerverwaltung_list_view, null);
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
