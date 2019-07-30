package eu.merscher.lbsvolleyball;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SpieltagActivitySpielerauswahlFragmentAdapter extends RecyclerView.Adapter<SpieltagActivitySpielerauswahlViewHolder> {


    private ArrayList<Spieler> spielerList;
    private OnSpielerClickListener onSpielerClickListener;


    SpieltagActivitySpielerauswahlFragmentAdapter(ArrayList<Spieler> spielerList, OnSpielerClickListener onSpielerClickListener) {
        this.spielerList = spielerList;
        this.onSpielerClickListener = onSpielerClickListener;

    }

    @Override
    public SpieltagActivitySpielerauswahlViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_spieltag_spielerauswahl_item, parent, false);
        return new SpieltagActivitySpielerauswahlViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SpieltagActivitySpielerauswahlViewHolder holder, final int position) {


        holder.textViewName.setText(spielerList.get(position).getName());
        holder.textViewVname.setText(spielerList.get(position).getVname());

        if (spielerList.get(position).getFoto().equals("avatar_m"))
            holder.spielerBild.setImageResource(R.drawable.avatar_m);

        else if (spielerList.get(position).getFoto().equals("avatar_f"))
            holder.spielerBild.setImageResource(R.drawable.avatar_f);

        else
            holder.spielerBild.setImageBitmap(BitmapFactory.decodeFile(spielerList.get(position).getFoto()));

        holder.spielerBild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSpieler(holder, position);
                onSpielerClickListener.onSpielerClick(spielerList.get(position));
            }
        });


        holder.textViewName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSpieler(holder, position);
                onSpielerClickListener.onSpielerClick(spielerList.get(position));

            }
        });

        holder.textViewVname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSpieler(holder, position);
                onSpielerClickListener.onSpielerClick(spielerList.get(position));

            }
        });

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSpieler(holder, position);
                onSpielerClickListener.onSpielerClick(spielerList.get(position));

            }
        });
    }

    @Override
    public int getItemCount() {
        return spielerList.size();
    }

    private void selectSpieler(final SpieltagActivitySpielerauswahlViewHolder holder, final int position) {
        if (SpieltagActivity.spielerIstSelected(spielerList.get(position))) {
            holder.checkBox.setChecked(false);
            SpieltagActivity.uncheckSelectedSpieler(spielerList.get(position));
        } else {
            holder.checkBox.setChecked(true);
            SpieltagActivity.addSelectedSpieler(spielerList.get(position));
        }
    }


    public interface OnSpielerClickListener {
        void onSpielerClick(Spieler spieler);
    }

}