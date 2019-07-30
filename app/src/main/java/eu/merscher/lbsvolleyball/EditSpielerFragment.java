package eu.merscher.lbsvolleyball;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class EditSpielerFragment extends Fragment {

    private Spieler spieler;

    public EditSpielerFragment(Spieler spieler) {
        this.spieler = spieler;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_spielerverwaltung_add_edit_spieler, container, false);


        EditSpielerFragmentAdapter adapter = new EditSpielerFragmentAdapter(getActivity(), spieler);
        RecyclerView recyclerView = rootView.findViewById(R.id.fragment_add_edit_spieler_recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return rootView;

    }

}
