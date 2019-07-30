package eu.merscher.lbsvolleyball;


import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class SpieltagActivitySpielerauswahlViewHolder extends RecyclerView.ViewHolder {

    public TextView textViewName;
    public TextView textViewVname;
    public ImageView spielerBild;
    public CheckBox checkBox;


    public SpieltagActivitySpielerauswahlViewHolder(View v) {
        super(v);
        textViewName = v.findViewById(R.id.activity_spieltag_spielerauswahl_item_name);
        textViewVname = v.findViewById(R.id.activity_spieltag_spielerauswahl_item_vname);
        spielerBild = v.findViewById(R.id.activity_spieltag_spielerauswahl_item_bild);
        checkBox = v.findViewById(R.id.activity_spieltag_spielerauswahl_item_checkbox);
    }
}