package eu.merscher.lbsvolleyball;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;


public class SpielerseiteGrunddatenFragment extends Fragment {


    private Spieler spieler;
    private double kto_saldo_neu;
    private int teilnahmen;
    private static DecimalFormat df = new DecimalFormat("0.00");

    public SpielerseiteGrunddatenFragment() {
        // Required empty public constructor
    }
    public static SpielerseiteGrunddatenFragment newInstance(Spieler spieler, double kto_saldo_neu, int teilnahmen) {
        SpielerseiteGrunddatenFragment fragment = new SpielerseiteGrunddatenFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("spieler", spieler);
        bundle.putDouble("kto_saldo_neu", kto_saldo_neu);
        bundle.putInt("teilnahmen", teilnahmen);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_spielerseite_grunddaten, container, false);
        spieler = (Spieler) getArguments().getSerializable("spieler");
        kto_saldo_neu = getArguments().getDouble("kto_saldo_neu");
        teilnahmen = getArguments().getInt("teilnahmen");

        //Button buttonKontouebersicht = (Button) listItemView.findViewById(R.id.button_add_spieltag);

        TextView textViewName = rootView.findViewById(R.id.textView_name);
        TextView textViewVname = rootView.findViewById(R.id.textView_vname);
        TextView textViewBdate = rootView.findViewById(R.id.textView_bdate);
        TextView textViewKtoSaldo = rootView.findViewById(R.id.textview_kontostand_spieler);
        TextView textViewTeilnahmen = rootView.findViewById(R.id.trainingsteilnahmen);

        textViewName.setText(String.valueOf(spieler.getName()));
        textViewVname.setText(String.valueOf(spieler.getVname()));
        textViewBdate.setText(spieler.getBdate());
        textViewKtoSaldo.setText(df.format(kto_saldo_neu));
        textViewTeilnahmen.setText(Integer.toString(teilnahmen));

        return rootView;

    }

}
