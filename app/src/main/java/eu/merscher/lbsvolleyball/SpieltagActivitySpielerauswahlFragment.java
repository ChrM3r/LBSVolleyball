package eu.merscher.lbsvolleyball;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class SpieltagActivitySpielerauswahlFragment extends Fragment implements SpieltagActivitySpielerauswahlFragmentAdapter.OnSpielerClickListener {


    private ArrayList<Spieler> spielerList;
    private OnSpielerClickListenerInFragment onSpielerClickListenerInFragment;

    public SpieltagActivitySpielerauswahlFragment() {
    }

    public SpieltagActivitySpielerauswahlFragment(ArrayList<Spieler> spielerList, OnSpielerClickListenerInFragment onSpielerClickListenerInFragment) {
        this.spielerList = spielerList;
        this.onSpielerClickListenerInFragment = onSpielerClickListenerInFragment;

    }

    public static SpieltagActivitySpielerauswahlFragment newInstance(ArrayList<Spieler> spielerList) {
        SpieltagActivitySpielerauswahlFragment fragment = new SpieltagActivitySpielerauswahlFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("spielerList", spielerList);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onSpielerClick(Spieler spieler) {
        onSpielerClickListenerInFragment.onSpielerClickInFragment(spieler);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_spieltag_spielerauswahl, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.spieltag_activity_spielerauswahl_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setHasFixedSize(true);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        ArrayList<Spieler> spielerListStatic = spielerList;
        if (spielerList != null) {

            if (spielerList.size() > 0) {
                recyclerView.setAdapter(new SpieltagActivitySpielerauswahlFragmentAdapter(spielerList, this));
            }
            recyclerView.setLayoutManager(layoutManager);
        }
        return rootView;

    }

    public interface OnSpielerClickListenerInFragment {
        void onSpielerClickInFragment(Spieler spieler);
    }

}
