package eu.merscher.lbsvolleyball;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class SpielerseiteKontodatenFragment extends Fragment {
    private ArrayList<Spieler> spielerList = new ArrayList<Spieler>();
    private Spieler spieler;

    public SpielerseiteKontodatenFragment() {
        // Required empty public constructor
    }
    public static SpielerseiteKontodatenFragment newInstance(ArrayList<Buchung> buchungList) {
        SpielerseiteKontodatenFragment fragment = new SpielerseiteKontodatenFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("buchungList", buchungList);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_spielerseite_kontodaten, container, false);

        DecimalFormat df = new DecimalFormat("0.00");

        ArrayList<Buchung> buchungList = getArguments().getParcelableArrayList("buchungList");

        SpielerKontoListViewAdapter buchungArrayAdapter = new SpielerKontoListViewAdapter(this, buchungList);

        ListView buchungDatumListView = (ListView) rootView.findViewById(R.id.listView_buchungen);
        buchungDatumListView.setAdapter(buchungArrayAdapter);

        return rootView;

    }

}

