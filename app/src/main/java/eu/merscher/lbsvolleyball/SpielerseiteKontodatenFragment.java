package eu.merscher.lbsvolleyball;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class SpielerseiteKontodatenFragment extends Fragment {

    private ArrayList<Buchung> buchungList;
    private Spieler spieler;

    public SpielerseiteKontodatenFragment() {
    }


    public SpielerseiteKontodatenFragment(ArrayList<Buchung> buchungList, Spieler spieler) {
        this.buchungList = buchungList;
        this.spieler = spieler;
    }

    public static SpielerseiteKontodatenFragment newInstance(ArrayList<Buchung> buchungList, Spieler spieler) {
        SpielerseiteKontodatenFragment fragment = new SpielerseiteKontodatenFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("buchungList", buchungList);
        bundle.putParcelable("spieler", spieler);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Spieler spieler;
        View view = inflater.inflate(R.layout.fragment_spielerseite_grunddaten_kontodaten, container, false);

        //spieler = getArguments().getParcelable("spieler");
        //ArrayList<Buchung> buchungList = getArguments().getParcelableArrayList("buchungList");
        //onAddBuchungClickListenerInFragment = getArguments().getParcelable("test");

        SpielerseiteKontodatenFragmentAdapter adapter = new SpielerseiteKontodatenFragmentAdapter(getActivity(), buchungList, spieler);
        RecyclerView recyclerView = view.findViewById(R.id.fragment_spielerseite_recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return view;

    }

}
