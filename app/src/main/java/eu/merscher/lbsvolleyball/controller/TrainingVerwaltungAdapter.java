package eu.merscher.lbsvolleyball.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.database.DataSource;

public class TrainingVerwaltungAdapter extends BaseAdapter implements ListAdapter {

    private final Context context;
    private ArrayList<Long> trainingIDList;
    private ArrayList<String> trainingsDatumList;
    private ArrayList<String> trainingsortNameList;
    private ArrayList<Integer> trainingTeilnehmerAnzahl;
    private ArrayList<String> trainingBildList;
    DataSource dataSource;


    TrainingVerwaltungAdapter(ArrayList<Long> trainingIDList, ArrayList<String> trainingsDatumList, ArrayList<String> trainingsortNameList, ArrayList<Integer> trainingTeilnehmerAnzahl, ArrayList<String> trainingBildList, Context context) {
        this.trainingIDList = trainingIDList;
        this.trainingsDatumList = trainingsDatumList;
        this.trainingsortNameList = trainingsortNameList;
        this.trainingTeilnehmerAnzahl = trainingTeilnehmerAnzahl;
        this.trainingBildList = trainingBildList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return trainingIDList.size();
    }

    @Override
    public Object getItem(int pos) {
        return trainingIDList.get(pos);
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
            view = inflater.inflate(R.layout.activity_trainingverwaltung_list_view, null);
        }

        dataSource = DataSource.getInstance();
        dataSource.open();

        TextView datum = view.findViewById(R.id.trainingverwaltung_datum);
        Calendar calendar = Calendar.getInstance();
        String[] datumArray = trainingsDatumList.get(position).split("\\.");
        // -1 weil Monat nur von 0-11 geht
        calendar.set(Integer.parseInt(datumArray[2]), Integer.parseInt(datumArray[1]) - 1, Integer.parseInt(datumArray[0]));

        int tag = calendar.get(Calendar.DAY_OF_WEEK);
        String tagString;
        switch (tag) {
            default:
                tagString = "Sonntag, ";
                break;

            case 2:
                tagString = "Montag, ";
                break;

            case 3:
                tagString = "Dienstag, ";
                break;

            case 4:
                tagString = "Mittwoch, ";
                break;

            case 5:
                tagString = "Donnerstag, ";
                break;

            case 6:
                tagString = "Freitag, ";
                break;

            case 7:
                tagString = "Samstag, ";
                break;

        }
        datum.setText(String.format("%s %s", tagString, trainingsDatumList.get(position)));

        TextView trainingsort = view.findViewById(R.id.trainingverwaltung_trainingsort);
        trainingsort.setText(trainingsortNameList.get(position));

        TextView anzahl = view.findViewById(R.id.trainingverwaltung_anzahl);
        anzahl.setText(Integer.toString(trainingTeilnehmerAnzahl.get(position)));

        ImageView bild = view.findViewById(R.id.trainingverwaltung_mapView);

        if (trainingBildList.get(position) != null)
            bild.setImageBitmap(BitmapFactory.decodeFile(trainingBildList.get(position)));
        else
            bild.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.avatar_map));

        return view;

    }


}
