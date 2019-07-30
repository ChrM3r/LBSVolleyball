package eu.merscher.lbsvolleyball;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SpielerseiteKontodatenFragmentAdapter extends RecyclerView.Adapter<SpielerseiteKontodatenFragmentAdapter.ViewHolder> {


    private ArrayList<Buchung> buchungList;
    private Spieler spieler;
    private Context context;
    private SpielerDataSource spielerDataSource;
    private BuchungDataSource buchungDataSource;
    private LayoutInflater inflate;
    private SpielerKontoListViewAdapter spielerKontoListViewAdapter;

    SpielerseiteKontodatenFragmentAdapter(Context context, ArrayList<Buchung> buchungList, Spieler spieler) {
        this.inflate = LayoutInflater.from(context);
        this.context = context;
        this.buchungList = buchungList;
        this.spieler = spieler;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflate.inflate(R.layout.fragment_spielerseite_kontodaten_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        buchungDataSource = new BuchungDataSource(context);
        spielerDataSource = new SpielerDataSource(context);

        holder.buttonAddBuchung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast toastError;
                if (holder.editTextAddBuchung.getText().toString().isEmpty()) {
                    toastError = Toast.makeText(context, "Es wurde kein Betrag zum Buchen erfasst!", Toast.LENGTH_SHORT);
                    toastError.setGravity(Gravity.CENTER, 0, 0);

                    toastError.show();
                } else {


                    DecimalFormat df = new DecimalFormat("0.00");

                    String bu_btr_String = df.format(Double.parseDouble(holder.editTextAddBuchung.getText().toString().replace(',', '.')));
                    double bu_btr = Double.parseDouble(bu_btr_String.replace(',', '.'));
                    double kto_saldo_alt;
                    double kto_saldo_neu;


                    Calendar kalender = Calendar.getInstance();
                    SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy");


                    buchungDataSource.open();
                    spielerDataSource.open();
                    if (spieler.getHat_buchung_mm() != null) { //Wenn Buchungen für den Spieler vorhanden sind...

                        Buchung buchung = buchungDataSource.getNeusteBuchungZuSpieler(spieler);

                        kto_saldo_alt = buchung.getKto_saldo_neu(); //der vorherige Kto_Saldo_neu ist der neue Kto_Saldo_alt
                        kto_saldo_neu = kto_saldo_alt + bu_btr;

                        buchungDataSource.createBuchung(spieler.getU_id(), bu_btr, kto_saldo_alt, kto_saldo_neu, datumsformat.format(kalender.getTime()));

                    } else { //Wenn keine Buchung vorhaden ist, ist der Startsaldo 0

                        kto_saldo_alt = 0; //der vorherige Kto_Saldo_neu ist nicht vorhanden, da keine vorherige Buchung existiert, daher 0
                        kto_saldo_neu = kto_saldo_alt + bu_btr;

                        buchungDataSource.createBuchung(spieler.getU_id(), bu_btr, kto_saldo_alt, kto_saldo_neu, datumsformat.format(kalender.getTime()));
                        spielerDataSource.updateHatBuchungenMM(spieler);
                    }

                    Toast toast = Toast.makeText(context, "Buchung angelegt", Toast.LENGTH_SHORT);
                    toast.show();

                    spielerKontoListViewAdapter.updateBuchungen(buchungDataSource.getAllBuchungZuSpieler(spieler));

                    buchungDataSource.close();
                    spielerDataSource.close();

                    holder.editTextAddBuchung.setText("");
                    holder.editTextAddBuchung.clearFocus();
                    holder.buttonAddBuchung.requestFocus();
                }
            }

        });

        spielerKontoListViewAdapter = new SpielerKontoListViewAdapter(context, buchungList);
        holder.buchungListView.setAdapter(spielerKontoListViewAdapter);
    }

    @Override
    public int getItemCount() {
        return 1;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ListView buchungListView;
        EditText editTextAddBuchung;
        FloatingActionButton buttonAddBuchung;


        ViewHolder(View view) {
            super(view);
            buchungListView = view.findViewById(R.id.listView_buchungen);
            editTextAddBuchung = view.findViewById(R.id.fragment_kontodaten_editText_addBuchung);
            buttonAddBuchung = view.findViewById(R.id.fragment_spielerkonto_addButton);
        }

    }

    public interface OnAddBuchungClickListener {
        void onAddBuchungClick(String kto_saldo_neu);
    }

}
