package eu.merscher.lbsvolleyball;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class AddSpielerFragmentAdapter extends RecyclerView.Adapter<AddSpielerFragmentAdapter.ViewHolder> {


    private final LayoutInflater inflate;
    private final Context context;
    private SpielerDataSource spielerDataSource;
    private BuchungDataSource buchungDataSource;
    private ItemClickListener itemClickListener;


    AddSpielerFragmentAdapter(Context context) {
        this.inflate = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflate.inflate(R.layout.fragment_spielerverwaltung_add_spieler_item, parent, false);
        spielerDataSource = SpielerDataSource.getInstance();
        buchungDataSource = BuchungDataSource.getInstance();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final EditText editTextName;
        final EditText editTextVname;
        final EditText editTextBdate;
        final EditText editTextMail;
        final Button buttonSpielerAnlegen;
        EditText editTextAddBuchung;

        ViewHolder(final View view) {
            super(view);
            editTextName = view.findViewById(R.id.editText_name_add);
            editTextVname = view.findViewById(R.id.editText_vname_add);
            editTextBdate = view.findViewById(R.id.editText_bdate_add);
            editTextMail = view.findViewById(R.id.editText_mail_add);
            editTextAddBuchung = view.findViewById(R.id.fragment_kontodaten_editText_addBuchung);
            buttonSpielerAnlegen = view.findViewById(R.id.fragement_add_spieler_button);

            editTextAddBuchung = view.findViewById(R.id.fragment_add_editText_AddBuchung);

            //Textformat Platzkosten
            editTextAddBuchung.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    Utils.formatNumericEditText(editTextAddBuchung);
                }
            });

            //Spieler anlegen
            buttonSpielerAnlegen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = editTextName.getText().toString();
                    String vname = editTextVname.getText().toString();
                    String bdate = editTextBdate.getText().toString();
                    String mail = editTextMail.getText().toString();
                    String userFotoAlsString = AddSpielerActivity.getUserFotoAlsString();

                    if (TextUtils.isEmpty(name)) {
                        editTextName.setError(context.getString(R.string.editText_errorMessage));
                        return;
                    }
                    if (TextUtils.isEmpty(vname)) {
                        editTextVname.setError(context.getString(R.string.editText_errorMessage));
                        return;
                    }

                    if (TextUtils.isEmpty(bdate)) {
                        editTextBdate.setError(context.getString(R.string.editText_errorMessage));
                        return;
                    }

                    Spieler neuerSpieler;

                    spielerDataSource.open();

                    if (userFotoAlsString != null && TextUtils.isEmpty(mail)) {

                        neuerSpieler = spielerDataSource.createSpieler(name, vname, bdate, 0, userFotoAlsString, null, null);
                        System.out.println("FALL 1");
                    } else if (userFotoAlsString == null && TextUtils.isEmpty(mail)) {

                        final int random = new Random().nextInt();
                        if (random % 2 == 0)
                            neuerSpieler = spielerDataSource.createSpieler(name, vname, bdate, 0, "avatar_m", null, null);
                        else
                            neuerSpieler = spielerDataSource.createSpieler(name, vname, bdate, 0, "avatar_f", null, null);

                    } else if (userFotoAlsString == null) {
                        final int random = new Random().nextInt();

                        if (random % 2 == 0)
                            neuerSpieler = spielerDataSource.createSpieler(name, vname, bdate, 0, "avatar_m", mail, null);
                        else
                            neuerSpieler = spielerDataSource.createSpieler(name, vname, bdate, 0, "avatar_f", mail, null);
                    } else {
                        neuerSpieler = spielerDataSource.createSpieler(name, vname, bdate, 0, userFotoAlsString, mail, null);
                    }

                    spielerDataSource.close();

                    AddSpielerActivity.setUserFotoAlsString(null);

                    DecimalFormat df = new DecimalFormat("0.00");

                    if (!TextUtils.isEmpty(editTextAddBuchung.getText())) {
                        String bu_btr_String = df.format(Double.parseDouble(editTextAddBuchung.getText().toString().replace(',', '.')));
                        double bu_btr = Double.parseDouble(bu_btr_String.replace(',', '.'));
                        double kto_saldo_alt;
                        double kto_saldo_neu;


                        Calendar kalender = Calendar.getInstance();
                        SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy");

                        buchungDataSource.open();
                        spielerDataSource.open();
                        kto_saldo_alt = 0; //der vorherige Kto_Saldo_neu ist nicht vorhanden, da keine vorherige Buchung existiert, daher 0
                        kto_saldo_neu = kto_saldo_alt + bu_btr;
                        buchungDataSource.createBuchung(neuerSpieler.getU_id(), bu_btr, kto_saldo_alt, kto_saldo_neu, datumsformat.format(kalender.getTime()));
                        spielerDataSource.updateHatBuchungenMM(neuerSpieler);
                        buchungDataSource.close();
                        spielerDataSource.close();
                    }

                    Intent intent = new Intent(context, SpielerVerwaltungActivity.class);
                    context.startActivity(intent);
                }

            });

        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null)
                itemClickListener.onItemClick(view, getAdapterPosition());
        }
    }


}
