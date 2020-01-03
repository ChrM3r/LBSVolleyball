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
import eu.merscher.lbsvolleyball.model.Trainingsort;


public class EditTrainingsortFragment extends Fragment implements EditTrainingsortFragmentAdapter.OnSpeichernClick, EditTrainingsortFragmentAdapter.TrainingsortLoeschenAsyncTask.OnLoeschenClick {

    private final Trainingsort trainingsort;

    private static EditTrainingsortFragmentAdapter.OnSpeichernClick onSpeichernClick;
    private static EditTrainingsortFragmentAdapter.TrainingsortLoeschenAsyncTask.OnLoeschenClick onLoeschenClick;
    EditTrainingsortFragmentAdapter adapter;

    static EditTrainingsortFragmentAdapter.TrainingsortLoeschenAsyncTask.OnLoeschenClick getOnLoeschenClick() {
        return onLoeschenClick;
    }

    static EditTrainingsortFragmentAdapter.OnSpeichernClick getOnSpeichernClick() {
        return onSpeichernClick;
    }

    EditTrainingsortFragment(Trainingsort trainingsort) {
        this.trainingsort = trainingsort;
    }

    public EditTrainingsortFragmentAdapter getAdapter() {
        return adapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add_edit_spieler, container, false);

        onSpeichernClick = this;
        onLoeschenClick = this;

        adapter = new EditTrainingsortFragmentAdapter(getActivity(), trainingsort);
        RecyclerView recyclerView = rootView.findViewById(R.id.fragment_add_edit_spieler_recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return rootView;

    }

    @Override
    public void onSpeichernClick() {

        Objects.requireNonNull(getActivity()).finish();
    }

    @Override
    public void onLoeschenClick() {
        Objects.requireNonNull(getActivity()).finish();
        EditTrainingsortActivity.getOnEditFinish().onEditFinish();

    }

    public interface OnEditFinish {
        void onEditFinish();
    }
}
