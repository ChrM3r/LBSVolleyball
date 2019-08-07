package eu.merscher.lbsvolleyball.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.model.Spieler;


public class EditSpielerFragment extends Fragment implements EditSpielerFragmentAdapter.SpielerUpdateAsyncTask.OnSpeichernClick, EditSpielerFragmentAdapter.SpielerLoeschenAsyncTask.OnLoeschenClick {

    private final Spieler spieler;
    public static EditSpielerFragmentAdapter.SpielerUpdateAsyncTask.OnSpeichernClick onSpeichernClick;
    public static EditSpielerFragmentAdapter.SpielerLoeschenAsyncTask.OnLoeschenClick onLoeschenClick;
    private OnEditFinish onEditFinish;

    public static EditSpielerFragmentAdapter.SpielerLoeschenAsyncTask.OnLoeschenClick getOnLoeschenClick() {
        return onLoeschenClick;
    }

    public static EditSpielerFragmentAdapter.SpielerUpdateAsyncTask.OnSpeichernClick getOnSpeichernClick() {
        return onSpeichernClick;
    }

    public EditSpielerFragment(Spieler spieler) {
        this.spieler = spieler;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_spielerverwaltung_add_edit_spieler, container, false);

        onSpeichernClick = this;
        onLoeschenClick = this;

        onEditFinish = SpielerseiteActivity.getOnEditFinish();

        EditSpielerFragmentAdapter adapter = new EditSpielerFragmentAdapter(getActivity(), spieler);
        RecyclerView recyclerView = rootView.findViewById(R.id.fragment_add_edit_spieler_recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return rootView;

    }

    @Override
    public void onSpeichernClick() {

        System.out.println("onSpeicherClick");
        getActivity().finish();
    }

    @Override
    public void onLoeschenClick() {
        getActivity().finish();
        onEditFinish.onEditFinish();

    }

    public interface OnEditFinish {
        void onEditFinish();
    }
}
