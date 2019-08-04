package eu.merscher.lbsvolleyball;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class EditSpielerFragmentAdapter extends RecyclerView.Adapter<EditSpielerFragmentAdapter.ViewHolder> {


    private static final DecimalFormat df = new DecimalFormat("0.00");
    private static Buchung neusteSpielerBuchung;
    private static Buchung neusteTeamBuchung;
    private final LayoutInflater inflate;
    private final Context context;
    private final Spieler spieler;
    private ItemClickListener itemClickListener;


    EditSpielerFragmentAdapter(Context context, Spieler spieler) {
        this.inflate = LayoutInflater.from(context);
        this.context = context;
        this.spieler = spieler;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflate.inflate(R.layout.fragment_spielerverwaltung_edit_spieler_item, parent, false);
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

    static class SpielerUpdateAsyncTask extends AsyncTask<Void, Void, Void> {


        public final WeakReference<EditSpielerFragmentAdapter> activityReference;

        private final Spieler spieler;
        private final String name;
        private final String vname;
        private final String bdate;
        private final String userFotoAlsString;
        private final String foto;
        private final String mail;


        SpielerUpdateAsyncTask(EditSpielerFragmentAdapter context, Spieler spieler, String name, String vname, String bdate, String userFotoAlsString, String foto, String mail) {
            activityReference = new WeakReference<>(context);
            this.spieler = spieler;
            this.name = name;
            this.vname = vname;
            this.bdate = bdate;
            this.userFotoAlsString = userFotoAlsString;
            this.foto = foto;
            this.mail = mail;

        }

        @Override
        protected Void doInBackground(Void... args) {

            SpielerDataSource spielerDataSource = SpielerDataSource.getInstance();
            spielerDataSource.open();

            if (userFotoAlsString != null && TextUtils.isEmpty(mail)) {

                spielerDataSource.updateSpieler(spieler.getU_id(), name, vname, bdate, spieler.getTeilnahmen(), userFotoAlsString, null, spieler.getHat_buchung_mm());
                System.out.println("FALL 1");
            } else if (userFotoAlsString == null && TextUtils.isEmpty(mail)) {

                final int random = new Random().nextInt();
                if (random % 2 == 0)
                    spielerDataSource.updateSpieler(spieler.getU_id(), name, vname, bdate, spieler.getTeilnahmen(), foto, null, spieler.getHat_buchung_mm());
                else
                    spielerDataSource.updateSpieler(spieler.getU_id(), name, vname, bdate, spieler.getTeilnahmen(), foto, null, spieler.getHat_buchung_mm());

            } else {

                spielerDataSource.updateSpieler(spieler.getU_id(), name, vname, bdate, spieler.getTeilnahmen(), userFotoAlsString, mail, spieler.getHat_buchung_mm());

            }
            return null;
        }

        @Override
        public void onPostExecute(Void v) {

            EditSpielerFragmentAdapter activity = activityReference.get();
            if (activity == null) return;


            AddSpielerActivity.setUserFotoAlsString(null);
            Intent intent = new Intent(activity.context, SpielerVerwaltungActivity.class);
            activity.context.startActivity(intent);
        }
    }

    static class BuchenAsyncTask extends AsyncTask<Void, Void, Void> {


        public final WeakReference<EditSpielerFragmentAdapter> activityReference;

        private final Spieler spieler;


        BuchenAsyncTask(EditSpielerFragmentAdapter context, Spieler spieler) {
            activityReference = new WeakReference<>(context);
            this.spieler = spieler;

        }

        @Override
        protected Void doInBackground(Void... args) {

            SpielerDataSource spielerDataSource = SpielerDataSource.getInstance();
            BuchungDataSource buchungDataSource = BuchungDataSource.getInstance();
            spielerDataSource.open();
            buchungDataSource.open();

            neusteSpielerBuchung = buchungDataSource.getNeusteBuchungZuSpieler(spieler);
            neusteTeamBuchung = buchungDataSource.getNeusteBuchungZuTeamkonto();

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {

            if (neusteTeamBuchung != null) {

                double bu_btr = neusteSpielerBuchung.getKto_saldo_neu();
                double kto_saldo_alt = neusteTeamBuchung.getKto_saldo_neu();
                double kto_saldo_neu = kto_saldo_alt + bu_btr;

                new SetBuchungenAsyncTask(bu_btr, kto_saldo_alt, kto_saldo_neu);

            } else {

                double bu_btr = neusteSpielerBuchung.getKto_saldo_neu();
                double kto_saldo_alt = 0; //der vorherige Kto_Saldo_neu ist nicht vorhanden, da keine vorherige Buchung existiert, daher 0
                double kto_saldo_neu = kto_saldo_alt + bu_btr;

                new SetBuchungenAsyncTask(bu_btr, kto_saldo_alt, kto_saldo_neu);

            }
        }

    }

    static class SetBuchungenAsyncTask extends AsyncTask<Void, Void, Void> {


        private final double bu_btr;
        private final double kto_saldo_alt;
        private final double kto_saldo_neu;


        SetBuchungenAsyncTask(double bu_btr, double kto_saldo_alt, double kto_saldo_neu) {
            this.bu_btr = bu_btr;
            this.kto_saldo_alt = kto_saldo_alt;
            this.kto_saldo_neu = kto_saldo_neu;
        }

        @Override
        protected Void doInBackground(Void... args) {

            SpielerDataSource spielerDataSource = SpielerDataSource.getInstance();
            BuchungDataSource buchungDataSource = BuchungDataSource.getInstance();
            spielerDataSource.open();
            buchungDataSource.open();

            Calendar kalender = Calendar.getInstance();
            SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy");

            buchungDataSource.createBuchungAufTeamkonto(bu_btr, kto_saldo_alt, kto_saldo_neu, datumsformat.format(kalender.getTime()));

            return null;
        }

    }

    static class SpielerLoeschenAsyncTask extends AsyncTask<Void, Void, Void> {

        private final Spieler spieler;


        SpielerLoeschenAsyncTask(Spieler spieler) {
            this.spieler = spieler;

        }

        @Override
        protected Void doInBackground(Void... args) {

            SpielerDataSource spielerDataSource = SpielerDataSource.getInstance();
            spielerDataSource.open();
            spielerDataSource.deleteSpieler(spieler);

            return null;
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final EditText editTextName;
        final EditText editTextVname;
        final EditText editTextBdate;
        final EditText editTextMail;
        final Button buttonSpielerAendern;
        final Button buttonSpielerLoeschen;

        ViewHolder(final View view) {
            super(view);
            editTextName = view.findViewById(R.id.editText_name_edit);
            editTextVname = view.findViewById(R.id.editText_vname_edit);
            editTextBdate = view.findViewById(R.id.editText_bdate_edit);
            editTextMail = view.findViewById(R.id.editText_mail_edit);
            buttonSpielerAendern = view.findViewById(R.id.fragment_edit_spieler_button);
            buttonSpielerLoeschen = view.findViewById(R.id.fragment_edit_spieler_loeschen_button);

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

                    new SpielerUpdateAsyncTask(EditSpielerFragmentAdapter.this, spieler, name, vname, bdate, userFotoAlsString, foto, mail).execute();

                }

            });

            buttonSpielerLoeschen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    BuchungDataSource buchungDataSource = BuchungDataSource.getInstance();
                    buchungDataSource.open();

                    neusteSpielerBuchung = buchungDataSource.getNeusteBuchungZuSpieler(spieler);

                    if (neusteSpielerBuchung.getKto_saldo_neu() > 0) {

                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);

                        dialog.setIcon(R.drawable.kontoverwaltung_icon)
                                .setMessage(spieler.getVname() + " " + spieler.getName() + " hat einen Kontostand von " + df.format(neusteSpielerBuchung.getKto_saldo_neu()) + "€. Der Betrag wird auf das Teamkonto überwiesen.")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialoginterface, int i) {

                                        new BuchenAsyncTask(EditSpielerFragmentAdapter.this, spieler).execute();
                                        new SpielerLoeschenAsyncTask(spieler).execute();

                                        Intent intent = new Intent(context, SpielerVerwaltungActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        context.startActivity(intent);
                                    }
                                })
                                .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialoginterface, int i) {
                                        dialoginterface.cancel();
                                    }
                                }).show();
                    } else if (neusteSpielerBuchung.getKto_saldo_neu() < 0) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);

                        dialog.setIcon(R.drawable.kontoverwaltung_icon)
                                .setMessage(spieler.getVname() + " " + spieler.getName() + " hat einen Kontostand von " + df.format(neusteSpielerBuchung.getKto_saldo_neu()) + "€. Bitte Konto vorher ausgleichen.")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialoginterface, int i) {
                                        dialoginterface.cancel();
                                    }
                                }).show();
                    } else
                        new SpielerLoeschenAsyncTask(spieler).execute();

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