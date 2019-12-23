package eu.merscher.lbsvolleyball.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.model.Trainingsort;


public class TrainingsortFragment extends Fragment {

    private Trainingsort trainingsort;

    public TrainingsortFragment() {
    }

    TrainingsortFragment(Trainingsort trainingsort) {
        this.trainingsort = trainingsort;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_spielerseite_grunddaten_kontodaten, container, false);


        TrainingsortFragmentAdapter adapter = new TrainingsortFragmentAdapter(getActivity(), trainingsort);
        RecyclerView recyclerView = rootView.findViewById(R.id.fragment_spielerseite_recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return rootView;

    }

}
