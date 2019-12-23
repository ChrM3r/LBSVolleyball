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

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.model.Trainingsort;

public class TrainingsortVerwaltungAdapter extends BaseAdapter implements ListAdapter {

    private final Context context;
    private ArrayList<String> trainingsortNamen;
    private ArrayList<Bitmap> trainingsortFotos;
    private ArrayList<Trainingsort> trainingsortList;


    TrainingsortVerwaltungAdapter(ArrayList<Trainingsort> trainingsortList, ArrayList<String> trainingsortNamen, ArrayList<Bitmap> trainingsortFotos, Context context) {
        this.trainingsortList = trainingsortList;
        this.trainingsortNamen = trainingsortNamen;
        this.trainingsortFotos = trainingsortFotos;
        this.context = context;
    }

    @Override
    public int getCount() {
        return trainingsortList.size();
    }

    @Override
    public Object getItem(int pos) {
        return trainingsortNamen.get(pos);
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
            view = inflater.inflate(R.layout.activity_trainingsortverwaltung_list_view, null);
        }

        TextView trainingsortName = view.findViewById(R.id.trainingsort_name);
        trainingsortName.setText(trainingsortNamen.get(position));

        TextView trainingsortOrt = view.findViewById(R.id.trainingsort_ort);
        String ort = trainingsortList.get(position).getStrasse() + " ," + trainingsortList.get(position).getPlz() + " " + trainingsortList.get(position).getOrt();
        trainingsortOrt.setText(ort);

        ImageView trainingsortBild = view.findViewById(R.id.trainingsortBild);

        if (trainingsortList.get(position).getFoto().equals("avatar_map"))
            trainingsortBild.setImageResource(R.drawable.avatar_map);
        else
            trainingsortBild.setImageBitmap(trainingsortFotos.get(position));

        return view;

    }


}
