package eu.merscher.lbsvolleyball.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.model.Spieler;


public class SpielerseiteGrunddatenFragment extends Fragment {

    public SpielerseiteGrunddatenFragment() {
    }

    static SpielerseiteGrunddatenFragment newInstance(Spieler spieler, double kto_saldo_neu, int teilnahmen) {
        SpielerseiteGrunddatenFragment fragment = new SpielerseiteGrunddatenFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("spieler", spieler);
        bundle.putDouble("kto_saldo_neu", kto_saldo_neu);
        bundle.putInt("teilnahmen", teilnahmen);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Spieler spieler;
        double kto_saldo_neu;
        int teilnahmen;

        View rootView = inflater.inflate(R.layout.fragment_spielerseite_grunddaten_kontodaten, container, false);

        spieler = Objects.requireNonNull(getArguments()).getParcelable("spieler");
        kto_saldo_neu = getArguments().getDouble("kto_saldo_neu");
        teilnahmen = getArguments().getInt("teilnahmen");


        SpielerseiteGrunddatenFragmentAdapter adapter = new SpielerseiteGrunddatenFragmentAdapter(getActivity(), spieler, kto_saldo_neu, teilnahmen);
        RecyclerView recyclerView = rootView.findViewById(R.id.fragment_spielerseite_recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return rootView;

    }

}
