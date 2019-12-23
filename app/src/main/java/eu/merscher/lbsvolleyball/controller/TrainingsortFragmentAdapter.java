package eu.merscher.lbsvolleyball.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.model.Trainingsort;

public class TrainingsortFragmentAdapter extends RecyclerView.Adapter<TrainingsortFragmentAdapter.ViewHolder> {


    private final Trainingsort trainingsort;
    private final LayoutInflater inflate;


    TrainingsortFragmentAdapter(Context context, Trainingsort trainingsort) {
        this.inflate = LayoutInflater.from(context);
        this.trainingsort = trainingsort;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {

        View view = inflate.inflate(R.layout.fragment_trainingsort_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String besucheAlsString = String.valueOf(trainingsort.getBesuche());

        holder.textViewName.setText(trainingsort.getName());
        holder.textViewStrasse.setText(trainingsort.getStrasse());
        holder.textViewPlz.setText(trainingsort.getPlz());
        holder.textViewOrt.setText(trainingsort.getOrt());
        holder.textViewBesuche.setText(besucheAlsString);
    }

    @Override
    public int getItemCount() {
        return 1;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textViewName;
        final TextView textViewStrasse;
        final TextView textViewPlz;
        final TextView textViewOrt;
        final TextView textViewBesuche;

        ViewHolder(View view) {
            super(view);
            textViewName = view.findViewById(R.id.fragment_trainingsort_textView_name);
            textViewStrasse = view.findViewById(R.id.fragment_trainingsort_textView_strasse);
            textViewPlz = view.findViewById(R.id.fragment_trainingsort_textView_plz);
            textViewOrt = view.findViewById(R.id.fragment_trainingsort_textView_ort);
            textViewBesuche = view.findViewById(R.id.fragment_trainingsort_textView_besuche);
        }

    }
}
