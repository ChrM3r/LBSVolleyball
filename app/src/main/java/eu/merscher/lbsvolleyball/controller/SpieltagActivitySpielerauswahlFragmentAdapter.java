package eu.merscher.lbsvolleyball.controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.model.Spieler;
import eu.merscher.lbsvolleyball.utilities.BitmapScaler;
import eu.merscher.lbsvolleyball.utilities.Utils;

import static eu.merscher.lbsvolleyball.controller.SpieltagActivity.resources;

public class SpieltagActivitySpielerauswahlFragmentAdapter extends RecyclerView.Adapter<SpieltagActivitySpielerauswahlFragmentAdapter.SpieltagActivitySpielerauswahlViewHolder> {


    private final ArrayList<Spieler> spielerList;
    private final OnSpielerClickListener onSpielerClickListener;
    private final SpieltagActivity context;

    SpieltagActivitySpielerauswahlFragmentAdapter(SpieltagActivity context, ArrayList<Spieler> spielerList, OnSpielerClickListener onSpielerClickListener) {
        this.context = context;
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

        new FotosSkalierenAsyncTask(context, spielerList.get(position), holder).execute();


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

    static class FotosSkalierenAsyncTask extends AsyncTask<Void, Void, Bitmap> {

        public final WeakReference<SpieltagActivity> activityReference;
        private final Spieler spieler;
        private final SpieltagActivitySpielerauswahlViewHolder holder;

        FotosSkalierenAsyncTask(SpieltagActivity context, Spieler spieler, SpieltagActivitySpielerauswahlViewHolder holder) {
            activityReference = new WeakReference<>(context);
            this.spieler = spieler;
            this.holder = holder;
        }


        @Override
        protected Bitmap doInBackground(Void... args) {

            Bitmap spielerBildOriginal;
            Bitmap spielerBildScaled;
            Uri uri;

            if (spieler.getFoto().equals("avatar_m"))
                spielerBildOriginal = BitmapFactory.decodeResource(resources, R.drawable.avatar_m);

            else if (spieler.getFoto().equals("avatar_f"))
                spielerBildOriginal = BitmapFactory.decodeResource(resources, R.drawable.avatar_f);
            else {
                spielerBildOriginal = BitmapFactory.decodeFile(spieler.getFoto());
                try {
                    uri = Uri.fromFile(new File(spieler.getFoto()));
                    spielerBildOriginal = Utils.handleSamplingAndRotationBitmap(activityReference.get(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            spielerBildScaled = BitmapScaler.scaleToFitWidth(spielerBildOriginal, 150);

            return spielerBildScaled;
        }

        @Override
        public void onPostExecute(Bitmap spielerBildScaled) {

            holder.spielerBild.setImageBitmap(spielerBildScaled);

        }

    }

    public class SpieltagActivitySpielerauswahlViewHolder extends RecyclerView.ViewHolder {

        public final TextView textViewName;
        public final TextView textViewVname;
        public final ImageView spielerBild;
        public final CheckBox checkBox;


        public SpieltagActivitySpielerauswahlViewHolder(View v) {
            super(v);
            textViewName = v.findViewById(R.id.activity_spieltag_spielerauswahl_item_name);
            textViewVname = v.findViewById(R.id.activity_spieltag_spielerauswahl_item_vname);
            spielerBild = v.findViewById(R.id.activity_spieltag_spielerauswahl_item_bild);
            checkBox = v.findViewById(R.id.activity_spieltag_spielerauswahl_item_checkbox);
        }
    }
}