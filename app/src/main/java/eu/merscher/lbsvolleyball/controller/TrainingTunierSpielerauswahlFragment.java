package eu.merscher.lbsvolleyball.controller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.database.DataSource;
import eu.merscher.lbsvolleyball.model.Spieler;


public class TrainingTunierSpielerauswahlFragment extends Fragment {

    private ArrayList<Spieler> spielerList;
    private OnSpielerClickListener onSpielerClickListener;
    private TrainingTunierActivity context;

    public TrainingTunierSpielerauswahlFragment() {
    }

    public TrainingTunierSpielerauswahlFragment(TrainingTunierActivity context, ArrayList<Spieler> spielerList, OnSpielerClickListener onSpielerClickListener) {
        this.context = context;
        this.spielerList = spielerList;
        this.onSpielerClickListener = onSpielerClickListener;

    }

    public static TrainingTunierSpielerauswahlFragment newInstance(ArrayList<Spieler> spielerList) {
        TrainingTunierSpielerauswahlFragment fragment = new TrainingTunierSpielerauswahlFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("spielerList", spielerList);
        fragment.setArguments(bundle);

        return fragment;
    }

//    public TrainingTunierSpielerauswahlFragment(TrainingTunierActivity context, ArrayList<Spieler> spielerList, OnSpielerClickListenerInFragment onSpielerClickListenerInFragment) {
//        this.context = context;
//        this.spielerList = spielerList;
//        this.onSpielerClickListenerInFragment = onSpielerClickListenerInFragment;
//
//    }

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

    static class GetSpielerAndStartSpielerseiteAsyncTask extends AsyncTask<Void, Void, ArrayList<Spieler>> {


        public final WeakReference<TrainingTunierActivity> activityReference;
        private final int position;
        private ArrayList<Spieler> spielerList = new ArrayList<>();

        GetSpielerAndStartSpielerseiteAsyncTask(TrainingTunierActivity context, int position) {
            activityReference = new WeakReference<>(context);
            this.position = position;

        }

        @Override
        protected ArrayList<Spieler> doInBackground(Void... args) {

            DataSource dataSource = DataSource.getInstance();
            dataSource.open();

            spielerList = dataSource.getAllSpielerAbsteigendTeilnahme();
            return spielerList;
        }

        @Override
        public void onPostExecute(ArrayList<Spieler> result) {

            TrainingTunierActivity activity = activityReference.get();

            if (activity == null || activity.isFinishing()) return;

            spielerList = result;

            Spieler spieler = spielerList.get(position);

            Intent data = new Intent(activity, SpielerseiteActivity.class);
            data.putExtra("spieler", spieler);
            activity.startActivity(data);

        }
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

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            switch (viewType) {
                case 0:
                    View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_training_tunier_spielerauswahl_item, parent, false);
                    return new SpielerauswahlViewHolder(view1);

                case 1:
                    View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_training_tunier_spielerauswahl_item_button, parent, false);
                    return new AddSpielerViewHolder(view2);

                default:
                    View view3 = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_training_tunier_spielerauswahl_item, parent, false);
                    return new SpielerauswahlViewHolder(view3);
            }
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
                    SpielerauswahlViewHolder holder1 = (SpielerauswahlViewHolder) holder;
                    Spieler spieler = spielerList.get(position);

                    holder1.textViewName.setText(spieler.getName());
                    holder1.textViewVname.setText(spieler.getVname());

                    if (spieler.getFoto().equals("avatar_m"))
                        holder1.spielerBild.setImageResource(R.drawable.avatar_m);

                    else {
                        holder1.spielerBild.setImageBitmap(BitmapFactory.decodeFile(spieler.getFoto().replace(".png", "_klein.png")));
                    }


                    holder1.spielerBild.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectSpieler(holder1, position);

                            onSpielerClickListener.onSpielerClick();

                        }
                    });


                    holder1.textViewName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectSpieler(holder1, position);
                            System.out.println("Test davor");
                            onSpielerClickListener.onSpielerClick();
                            System.out.println("Test dannach");

                        }
                    });

                    holder1.textViewVname.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectSpieler(holder1, position);
                            onSpielerClickListener.onSpielerClick();

                        }
                    });

                    holder1.checkBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectSpieler(holder1, position);
                            onSpielerClickListener.onSpielerClick();

                        }
                    });

//
//                holder1.spielerBild.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//                        new GetSpielerAndStartSpielerseiteAsyncTask(context, position).execute();
//                        return true;
//                    }
//                });

                    holder1.spielerBild.setOnTouchListener(new View.OnTouchListener() {

                        long then = 0;

                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                then = System.currentTimeMillis();
                            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                if ((System.currentTimeMillis() - then) > 1200) {
                                    new GetSpielerAndStartSpielerseiteAsyncTask(context, position).execute();
                                    onSpielerClickListener.onSpielerClick();
                                    return true;
                                }
                            }
                            return false;
                        }
                    });
                    break;

                case 1:
                    AddSpielerViewHolder holder2 = (AddSpielerViewHolder) holder;
                    holder2.button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onSpielerClickListener.onSpielerClick();
                            Intent intent = new Intent(context, AddSpielerActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);
                        }
                    });
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return spielerList.size() + 1;
        }

        private void selectSpieler(final SpielerauswahlViewHolder holder, final int position) {
            if (TrainingFragment.spielerIstSelected(spielerList.get(position))) {
                holder.checkBox.setChecked(false);
                TrainingFragment.uncheckSelectedSpieler(spielerList.get(position));
            } else {
                holder.checkBox.setChecked(true);
                TrainingFragment.addSelectedSpieler(spielerList.get(position));
            }
        }


        public class SpielerauswahlViewHolder extends RecyclerView.ViewHolder {

            public final TextView textViewName;
            public final TextView textViewVname;
            public final ImageView spielerBild;
            public final CheckBox checkBox;


            public SpielerauswahlViewHolder(View v) {
                super(v);
                textViewName = v.findViewById(R.id.activity_spieltag_spielerauswahl_item_name);
                textViewVname = v.findViewById(R.id.activity_spieltag_spielerauswahl_item_vname);
                spielerBild = v.findViewById(R.id.activity_spieltag_spielerauswahl_item_bild);
                checkBox = v.findViewById(R.id.activity_spieltag_spielerauswahl_item_checkbox);
            }
        }

        public class AddSpielerViewHolder extends RecyclerView.ViewHolder {

            public final FloatingActionButton button;

            public AddSpielerViewHolder(View v) {
                super(v);
                button = v.findViewById(R.id.training_tunier_spielerauswahl_item_button);
            }
        }


    }


}
