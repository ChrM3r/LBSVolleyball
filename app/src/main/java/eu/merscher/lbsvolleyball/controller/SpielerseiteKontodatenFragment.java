package eu.merscher.lbsvolleyball.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.model.Buchung;
import eu.merscher.lbsvolleyball.model.Spieler;


public class SpielerseiteKontodatenFragment extends Fragment {

    private ArrayList<Buchung> buchungList;
    private Spieler spieler;

    public SpielerseiteKontodatenFragment() {
    }


    SpielerseiteKontodatenFragment(ArrayList<Buchung> buchungList, Spieler spieler) {
        this.buchungList = buchungList;
        this.spieler = spieler;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_spielerseite_grunddaten_kontodaten, container, false);

        SpielerseiteKontodatenFragmentAdapter adapter = new SpielerseiteKontodatenFragmentAdapter(getActivity(), buchungList, spieler);
        RecyclerView recyclerView = view.findViewById(R.id.fragment_spielerseite_recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return view;

    }

}
