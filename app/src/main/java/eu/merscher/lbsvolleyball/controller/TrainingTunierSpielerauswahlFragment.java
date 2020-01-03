package eu.merscher.lbsvolleyball.controller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.database.DataSource;
import eu.merscher.lbsvolleyball.model.Spieler;


public class TrainingTunierSpielerauswahlFragment extends Fragment {

    private ArrayList<Spieler> spielerList;
    private OnSpielerClickListener onSpielerClickListener;
    private TrainingTunierActivity context;
    private String sortierungUser;

    public TrainingTunierSpielerauswahlFragment() {
    }

    TrainingTunierSpielerauswahlFragment(TrainingTunierActivity context, ArrayList<Spieler> spielerList, OnSpielerClickListener onSpielerClickListener, String sortierungUser) {
        this.context = context;
        this.spielerList = spielerList;
        this.onSpielerClickListener = onSpielerClickListener;
        this.sortierungUser = sortierungUser;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_training_tunier_spielerauswahl, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.spieltag_activity_spielerauswahl_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setHasFixedSize(true);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        recyclerView.setAdapter(new TrainingTunierSpielerauswahlFragmentAdapter(context, spielerList, onSpielerClickListener));
        recyclerView.setLayoutManager(layoutManager);

        return rootView;

    }


    public interface OnSpielerClickListener {
        void onSpielerClick();
    }

    private void getSpielerUndStartSpielerseite(String sortierungUser, int position) {

        ArrayList<Spieler> spielerList;

        DataSource dataSource = DataSource.getInstance();
        dataSource.open();

        switch (sortierungUser) {

            default: {
                spielerList = dataSource.getAllSpielerAbsteigendTeilnahme();
                break;
            }
            case "vname": {
                spielerList = dataSource.getAllSpielerAlphabetischVname();
                break;
            }
            case "name": {
                spielerList = dataSource.getAllSpielerAlphabetischName();
                break;
            }
        }

        Spieler spieler = spielerList.get(position);

        Intent data = new Intent(context, SpielerseiteActivity.class);
        data.putExtra("spieler", spieler);

        context.startActivity(data);

    }

    public class TrainingTunierSpielerauswahlFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


        private ArrayList<Spieler> spielerList;
        private OnSpielerClickListener onSpielerClickListener;
        private TrainingTunierActivity context;


        TrainingTunierSpielerauswahlFragmentAdapter(TrainingTunierActivity context, ArrayList<Spieler> spielerList, OnSpielerClickListener onSpielerClickListener) {
            this.context = context;
            this.spielerList = spielerList;
            this.onSpielerClickListener = onSpielerClickListener;

        }

        @NotNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {

            if (viewType == 1) {
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_training_tunier_spielerauswahl_item_button, parent, false);
                return new AddSpielerViewHolder(view2);
            }

            View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_training_tunier_spielerauswahl_item, parent, false);
            return new SpielerauswahlViewHolder(view1);
        }

        @Override
        public int getItemViewType(int position) {
            if (spielerList != null && position <= spielerList.size() - 1)
                return 0;
            else return 1;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

            switch (holder.getItemViewType()) {
                case 0:
                    SpielerauswahlViewHolder spielerauswahlViewHolder = (SpielerauswahlViewHolder) holder;
                    Spieler spieler = spielerList.get(position);

                    spielerauswahlViewHolder.textViewName.setText(spieler.getName());
                    spielerauswahlViewHolder.textViewVname.setText(spieler.getVname());

                    if (spieler.getFoto().equals("avatar_m"))
                        spielerauswahlViewHolder.spielerBild.setImageResource(R.drawable.avatar_m);

                    else {
                        spielerauswahlViewHolder.spielerBild.setImageBitmap(BitmapFactory.decodeFile(spieler.getFoto().replace(".png", "_klein.png")));
                    }


                    spielerauswahlViewHolder.spielerBild.setOnClickListener(v -> selectSpieler(spielerauswahlViewHolder));

                    spielerauswahlViewHolder.textViewName.setOnClickListener(v -> selectSpieler(spielerauswahlViewHolder));

                    spielerauswahlViewHolder.textViewVname.setOnClickListener(v -> selectSpieler(spielerauswahlViewHolder));


                    //Spieler-Select
                    spielerauswahlViewHolder.checkBox.setOnCheckedChangeListener(null);
                    spielerauswahlViewHolder.checkBox.setChecked(TrainingFragment.spielerIstSelected(spielerList.get(position)));
                    spielerauswahlViewHolder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {


                        if (isChecked)
                            TrainingFragment.addSelectedSpieler(spielerList.get(position));
                        else
                            TrainingFragment.uncheckSelectedSpieler(spielerList.get(position));

                        onSpielerClickListener.onSpielerClick();

                    });


                    spielerauswahlViewHolder.spielerBild.setOnTouchListener(new View.OnTouchListener() {

                        long then = 0;

                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                then = System.currentTimeMillis();
                            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                if ((System.currentTimeMillis() - then) > 1200) {
                                    getSpielerUndStartSpielerseite(sortierungUser, position);
                                    //onSpielerClickListener.onSpielerClick();
                                    return true;
                                }
                            }
                            return false;
                        }
                    });
                    break;

                case 1:
                    AddSpielerViewHolder addSpielerViewHolder = (AddSpielerViewHolder) holder;
                    addSpielerViewHolder.button.setOnClickListener(v -> {
                        onSpielerClickListener.onSpielerClick();
                        Intent intent = new Intent(context, AddSpielerActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                    });
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return spielerList.size() + 1;
        }

        private void selectSpieler(final SpielerauswahlViewHolder holder) {

            holder.checkBox.setChecked(!holder.checkBox.isChecked());
        }


        class SpielerauswahlViewHolder extends RecyclerView.ViewHolder {

            final TextView textViewName;
            final TextView textViewVname;
            final ImageView spielerBild;
            final CheckBox checkBox;


            SpielerauswahlViewHolder(View v) {
                super(v);
                textViewName = v.findViewById(R.id.activity_spieltag_spielerauswahl_item_name);
                textViewVname = v.findViewById(R.id.activity_spieltag_spielerauswahl_item_vname);
                spielerBild = v.findViewById(R.id.activity_spieltag_spielerauswahl_item_bild);
                checkBox = v.findViewById(R.id.activity_spieltag_spielerauswahl_item_checkbox);
            }
        }

        public class AddSpielerViewHolder extends RecyclerView.ViewHolder {

            public final FloatingActionButton button;

            AddSpielerViewHolder(View v) {
                super(v);
                button = v.findViewById(R.id.training_tunier_spielerauswahl_item_button);
            }
        }
    }
}
