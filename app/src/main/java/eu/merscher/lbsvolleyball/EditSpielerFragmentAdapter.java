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
import java.util.Random;

public class EditSpielerFragmentAdapter extends RecyclerView.Adapter<EditSpielerFragmentAdapter.ViewHolder> {


    private static DecimalFormat df = new DecimalFormat("0.00");
    private LayoutInflater inflate;
    private Context context;
    private Spieler spieler;
    private SpielerDataSource spielerDataSource;
    private BuchungDataSource buchungDataSource;
    private ItemClickListener itemClickListener;


    EditSpielerFragmentAdapter(Context context, Spieler spieler) {
        this.inflate = LayoutInflater.from(context);
        this.context = context;
        this.spieler = spieler;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflate.inflate(R.layout.fragment_spielerverwaltung_edit_spieler_item, parent, false);
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
        EditText editTextName;
        EditText editTextVname;
        EditText editTextBdate;
        EditText editTextMail;
        Button buttonSpielerAendern;

        ViewHolder(final View view) {
            super(view);
            editTextName = view.findViewById(R.id.editText_name_edit);
            editTextVname = view.findViewById(R.id.editText_vname_edit);
            editTextBdate = view.findViewById(R.id.editText_bdate_edit);
            editTextMail = view.findViewById(R.id.editText_mail_edit);
            buttonSpielerAendern = view.findViewById(R.id.fragement_edit_spieler_button);

            editTextName.setText(spieler.getName());
            editTextVname.setText(spieler.getVname());
            editTextBdate.setText(spieler.getBdate());
            editTextMail.setText(spieler.getMail());


            buttonSpielerAendern.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = editTextName.getText().toString();
                    String vname = editTextVname.getText().toString();
                    String bdate = editTextBdate.getText().toString();
                    String mail = editTextMail.getText().toString();
                    String foto = spieler.getFoto();
                    String userFotoAlsString = EditSpielerActivity.getUserFotoAlsString();

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

                    Spieler updatedSpieler;

                    spielerDataSource.open();

                    if (userFotoAlsString != null && TextUtils.isEmpty(mail)) {

                        updatedSpieler = spielerDataSource.updateSpieler(spieler.getU_id(), name, vname, bdate, 0, userFotoAlsString, null, null);
                        System.out.println("FALL 1");
                    } else if (userFotoAlsString == null && TextUtils.isEmpty(mail)) {

                        final int random = new Random().nextInt();
                        if (random % 2 == 0)
                            updatedSpieler = spielerDataSource.updateSpieler(spieler.getU_id(), name, vname, bdate, 0, foto, null, null);
                        else
                            updatedSpieler = spielerDataSource.updateSpieler(spieler.getU_id(), name, vname, bdate, 0, foto, null, null);

                    } else {

                        updatedSpieler = spielerDataSource.updateSpieler(spieler.getU_id(), name, vname, bdate, 0, userFotoAlsString, mail, null);

                    }

                    spielerDataSource.close();

                    AddSpielerActivity.setUserFotoAlsString(null);
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

//TODO:
//Spieler löschen implementieren

//    DecimalFormat df = new DecimalFormat("0.00");
//
//                    if (!TextUtils.isEmpty(editTextAddBuchung.getText())) {
//                            double bu_btr = Double.parseDouble(df.format(Double.parseDouble(editTextAddBuchung.getText().toString().replace(',', '.'))));
//                            double kto_saldo_alt;
//                            double kto_saldo_neu;
//
//
//                            Calendar kalender = Calendar.getInstance();
//                            SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy");
//
//
//                            kto_saldo_alt = 0; //der vorherige Kto_Saldo_neu ist nicht vorhanden, da keine vorherige Buchung existiert, daher 0
//                            kto_saldo_neu = kto_saldo_alt + bu_btr;
//                            buchungDataSource.createBuchung(updatedSpieler.getU_id(), bu_btr, kto_saldo_alt, kto_saldo_neu, datumsformat.format(kalender.getTime()));
//                            spielerDataSource.updateHatBuchungenMM(updatedSpieler);
//                            buchungDataSource.close();
//                            }