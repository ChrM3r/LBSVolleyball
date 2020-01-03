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


public class EditSpielerFragment extends Fragment implements EditSpielerFragmentAdapter.SpielerUpdateAsyncTask.OnSpeichernClick, EditSpielerFragmentAdapter.SpielerLoeschenAsyncTask.OnLoeschenClick {

    private final Spieler spieler;
    private static EditSpielerFragmentAdapter.SpielerUpdateAsyncTask.OnSpeichernClick onSpeichernClick;
    static EditSpielerFragmentAdapter.SpielerLoeschenAsyncTask.OnLoeschenClick onLoeschenClick;
    private OnEditFinish onEditFinish;
    EditSpielerFragmentAdapter adapter;

    static EditSpielerFragmentAdapter.SpielerLoeschenAsyncTask.OnLoeschenClick getOnLoeschenClick() {
        return onLoeschenClick;
    }

    static EditSpielerFragmentAdapter.SpielerUpdateAsyncTask.OnSpeichernClick getOnSpeichernClick() {
        return onSpeichernClick;
    }

    public EditSpielerFragmentAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(EditSpielerFragmentAdapter adapter) {
        this.adapter = adapter;
    }

    EditSpielerFragment(Spieler spieler) {
        this.spieler = spieler;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add_edit_spieler, container, false);

        onSpeichernClick = this;
        onLoeschenClick = this;

        onEditFinish = SpielerseiteActivity.getOnEditFinish();

        adapter = new EditSpielerFragmentAdapter(getActivity(), spieler);
        RecyclerView recyclerView = rootView.findViewById(R.id.fragment_add_edit_spieler_recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return rootView;

    }

    @Override
    public void onSpeichernClick() {

        System.out.println("onSpeicherClick");
        Objects.requireNonNull(getActivity()).finish();
    }

    @Override
    public void onLoeschenClick() {
        Objects.requireNonNull(getActivity()).finish();
        onEditFinish.onEditFinish();

    }

    public interface OnEditFinish {
        void onEditFinish();
    }


}
