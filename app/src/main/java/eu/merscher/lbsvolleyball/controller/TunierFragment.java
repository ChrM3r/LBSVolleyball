package eu.merscher.lbsvolleyball.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import eu.merscher.lbsvolleyball.R;


public class TunierFragment extends Fragment {

    public TunierFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_spielerseite_grunddaten_kontodaten, container, false);

        TunierFragmentAdapter adapter = new TunierFragmentAdapter(getActivity());
        RecyclerView recyclerView = view.findViewById(R.id.fragment_spielerseite_recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        TunierFragmentAdapter.getOnResume().onResumeInterface();
    }

    public interface OnResume {
        void onResumeInterface();
    }
}
