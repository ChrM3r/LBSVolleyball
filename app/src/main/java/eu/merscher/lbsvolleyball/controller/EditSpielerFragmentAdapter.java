package eu.merscher.lbsvolleyball.controller;

import android.content.Context;
import android.content.ContextWrapper;
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

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.database.DataSource;
import eu.merscher.lbsvolleyball.model.Buchung;
import eu.merscher.lbsvolleyball.model.Spieler;
import eu.merscher.lbsvolleyball.utilities.Utilities;

import static eu.merscher.lbsvolleyball.controller.EditSpielerFragment.onLoeschenClick;

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

    void setClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    static class SpielerLoeschenAsyncTask extends AsyncTask<Void, Void, Void> {

        public final WeakReference<EditSpielerFragmentAdapter> activityReference;

        private final Spieler spieler;
        private final OnLoeschenClick onLoeschenClick = EditSpielerFragment.getOnLoeschenClick();

        SpielerLoeschenAsyncTask(EditSpielerFragmentAdapter context, Spieler spieler) {
            activityReference = new WeakReference<>(context);
            this.spieler = spieler;

        }

        @Override
        protected Void doInBackground(Void... args) {

            DataSource dataSource = DataSource.getInstance();
            dataSource.open();

            dataSource.deleteSpieler(spieler);

            //Bilddatei löschen
            ContextWrapper cw = new ContextWrapper(activityReference.get().context);
            File directory = cw.getDir("profilbilder", Context.MODE_PRIVATE);
            File bild = new File(directory, spieler.getU_id() + "_" + spieler.getName() + ".png");
            boolean geloescht = bild.delete();

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {

            onLoeschenClick.onLoeschenClick();

        }

        //Interface für den Löschen-Button um die darunterliegenden Activitys auf Klick zu beenden
        public interface OnLoeschenClick {
            void onLoeschenClick();
        }
    }

    static class TeamkontoBuchenAsyncTask extends AsyncTask<Void, Void, Void> {


        private final double bu_btr;
        private final double kto_saldo_alt;
        private final double kto_saldo_neu;


        TeamkontoBuchenAsyncTask(double bu_btr, double kto_saldo_alt, double kto_saldo_neu) {
            this.bu_btr = bu_btr;
            this.kto_saldo_alt = kto_saldo_alt;
            this.kto_saldo_neu = kto_saldo_neu;
        }

        @Override
        protected Void doInBackground(Void... args) {

            DataSource dataSource = DataSource.getInstance();
            dataSource.open();

            Calendar kalender = Calendar.getInstance();
            SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);

            dataSource.createBuchungAufTeamkonto(bu_btr, kto_saldo_alt, kto_saldo_neu, datumsformat.format(kalender.getTime()), null, "X", null);

            return null;
        }

    }

    static class SpielerkontoBuchenAsyncTask extends AsyncTask<Void, Void, Void> {

        private final Spieler spieler;
        private final double bu_btr;
        private final double kto_saldo_alt;
        private final double kto_saldo_neu;


        SpielerkontoBuchenAsyncTask(Spieler spieler, double bu_btr, double kto_saldo_alt, double kto_saldo_neu) {
            this.spieler = spieler;
            this.bu_btr = bu_btr;
            this.kto_saldo_alt = kto_saldo_alt;
            this.kto_saldo_neu = kto_saldo_neu;
        }

        @Override
        protected Void doInBackground(Void... args) {

            DataSource dataSource = DataSource.getInstance();
            dataSource.open();

            Calendar kalender = Calendar.getInstance();
            SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);

            dataSource.createBuchung(spieler.getU_id(), bu_btr, kto_saldo_alt, kto_saldo_neu, datumsformat.format(kalender.getTime()), null, "X", null);

            return null;
        }

    }

    static class SpielerUpdateAsyncTask extends AsyncTask<Void, Void, Spieler> {


        public final WeakReference<EditSpielerFragmentAdapter> activityReference;

        private final Spieler spieler;
        private final String name;
        private final String vname;
        private final String bdate;
        private final String userFotoAlsString;
        private final String foto;
        private final String mail;

        private OnSpeichernClick onSpeichernClick = EditSpielerFragment.getOnSpeichernClick();



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
        protected Spieler doInBackground(Void... args) {

            DataSource dataSource = DataSource.getInstance();
            dataSource.open();

            Spieler updatedSpieler = spieler;


            if (EditSpielerActivity.getUserFotoAlsString() == null && !TextUtils.isEmpty(mail)) {
                updatedSpieler = dataSource.updateSpieler(spieler.getU_id(), name, vname, bdate, spieler.getTeilnahmen(), spieler.getFoto(), mail, spieler.getHat_buchung_mm());

            } else if (EditSpielerActivity.getUserFotoAlsString() == null && TextUtils.isEmpty(mail)) {

                updatedSpieler = dataSource.updateSpieler(spieler.getU_id(), name, vname, bdate, spieler.getTeilnahmen(), foto, null, spieler.getHat_buchung_mm());

            } else if (EditSpielerActivity.getUserFotoAlsString().equals("geloescht") && TextUtils.isEmpty(mail)) {

                updatedSpieler = dataSource.updateSpieler(spieler.getU_id(), name, vname, bdate, spieler.getTeilnahmen(), "avatar_m", null, spieler.getHat_buchung_mm());


            } else if (EditSpielerActivity.getUserFotoAlsString().equals("geloescht") && !TextUtils.isEmpty(mail)) {

                updatedSpieler = dataSource.updateSpieler(spieler.getU_id(), name, vname, bdate, spieler.getTeilnahmen(), "avatar_m", mail, spieler.getHat_buchung_mm());


            } else if (EditSpielerActivity.getUserFotoAlsString() != null && TextUtils.isEmpty(mail)) {

                updatedSpieler = dataSource.updateSpieler(spieler.getU_id(), name, vname, bdate, spieler.getTeilnahmen(), userFotoAlsString, null, spieler.getHat_buchung_mm());
                EditSpielerActivity.setUserFotoAlsString(Utilities.bildNachSpielerBenennen(activityReference.get().context, updatedSpieler));
                updatedSpieler = dataSource.updateFotoSpieler(updatedSpieler, EditSpielerActivity.getUserFotoAlsString());
                EditSpielerActivity.setUserFotoAlsString(null);

            } else if (EditSpielerActivity.getUserFotoAlsString() != null && !TextUtils.isEmpty(mail)) {

                updatedSpieler = dataSource.updateSpieler(spieler.getU_id(), name, vname, bdate, spieler.getTeilnahmen(), EditSpielerActivity.getUserFotoAlsString(), mail, spieler.getHat_buchung_mm());
                EditSpielerActivity.setUserFotoAlsString(Utilities.bildNachSpielerBenennen(activityReference.get().context, updatedSpieler));
                updatedSpieler = dataSource.updateFotoSpieler(updatedSpieler, EditSpielerActivity.getUserFotoAlsString());
                EditSpielerActivity.setUserFotoAlsString(null);

            }


            if (!spieler.getName().equals(name)) {
                String fotoNeu;
                fotoNeu = Utilities.bildNachNamensaenderungBenennen(activityReference.get().context, spieler, updatedSpieler);
                dataSource.updateFotoSpieler(updatedSpieler, fotoNeu);
            }

            if (EditSpielerActivity.getUserFotoAlsString() == null) {
                ContextWrapper cw = new ContextWrapper(activityReference.get().context);

                File directory = cw.getDir("profilbilder", Context.MODE_PRIVATE);
                File bild = new File(directory, updatedSpieler.getU_id() + "_" + updatedSpieler.getName() + ".png");
                String foto = bild.getAbsolutePath();

                return new Spieler(updatedSpieler.getU_id(), updatedSpieler.getName(), updatedSpieler.getVname(),
                        updatedSpieler.getBdate(), updatedSpieler.getTeilnahmen(),
                        foto, updatedSpieler.getMail(), updatedSpieler.getHat_buchung_mm());
            } else {
                ContextWrapper cw = new ContextWrapper(activityReference.get().context);
                File directory = cw.getDir("profilbilder", Context.MODE_PRIVATE);
                File bild = new File(directory, spieler.getU_id() + "_" + spieler.getName() + ".png");
                boolean geloescht = bild.delete();
                EditSpielerActivity.setUserFotoAlsString(null);
                return updatedSpieler;
            }
        }

        @Override
        public void onPostExecute(Spieler spieler) {

            EditSpielerFragmentAdapter activity = activityReference.get();
            if (activity == null) return;


            EditSpielerActivity.setUserFotoAlsString(null);
            Intent intent = new Intent(activity.context, SpielerseiteActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("spieler", spieler);
            activity.context.startActivity(intent);
            onSpeichernClick.onSpeichernClick();

        }

        public interface OnSpeichernClick {
            void onSpeichernClick();
        }
    }

    static class GetNeusteTeamBuchungenUndBereiteVorAsyncTask extends AsyncTask<Void, Void, Void> {


        public final WeakReference<EditSpielerFragmentAdapter> activityReference;

        private final Spieler spieler;


        GetNeusteTeamBuchungenUndBereiteVorAsyncTask(EditSpielerFragmentAdapter context, Spieler spieler) {
            activityReference = new WeakReference<>(context);
            this.spieler = spieler;

        }

        @Override
        protected Void doInBackground(Void... args) {

            DataSource dataSource = DataSource.getInstance();
            dataSource.open();

            neusteSpielerBuchung = dataSource.getNeusteBuchungZuSpieler(spieler);
            neusteTeamBuchung = dataSource.getNeusteBuchungZuTeamkonto();

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {

            if (neusteTeamBuchung != null) {

                double bu_btr = neusteSpielerBuchung.getKto_saldo_neu();
                double kto_saldo_alt = neusteTeamBuchung.getKto_saldo_neu();
                double kto_saldo_neu = kto_saldo_alt + bu_btr;

                new TeamkontoBuchenAsyncTask(bu_btr, kto_saldo_alt, kto_saldo_neu);

            } else {

                double bu_btr = neusteSpielerBuchung.getKto_saldo_neu();
                double kto_saldo_alt = 0; //der vorherige Kto_Saldo_neu ist nicht vorhanden, da keine vorherige Buchung existiert, daher 0
                double kto_saldo_neu = kto_saldo_alt + bu_btr;

                new TeamkontoBuchenAsyncTask(bu_btr, kto_saldo_alt, kto_saldo_neu);

            }
        }

    }

    static class GetNeusteSpielerBuchungenUndBereiteVorAsyncTask extends AsyncTask<Void, Void, Void> {


        public final WeakReference<EditSpielerFragmentAdapter> activityReference;

        private final Spieler spieler;


        GetNeusteSpielerBuchungenUndBereiteVorAsyncTask(EditSpielerFragmentAdapter context, Spieler spieler) {
            activityReference = new WeakReference<>(context);
            this.spieler = spieler;

        }

        @Override
        protected Void doInBackground(Void... args) {

            DataSource dataSource = DataSource.getInstance();
            dataSource.open();

            neusteSpielerBuchung = dataSource.getNeusteBuchungZuSpieler(spieler);

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {


            double bu_btr = neusteSpielerBuchung.getKto_saldo_neu();
            double kto_saldo_alt = neusteSpielerBuchung.getKto_saldo_neu();
            double kto_saldo_neu = kto_saldo_alt - bu_btr;

            new SpielerkontoBuchenAsyncTask(spieler, bu_btr, kto_saldo_alt, kto_saldo_neu);

        }

    }


    protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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

                    DataSource dataSource = DataSource.getInstance();
                    dataSource.open();

                    neusteSpielerBuchung = dataSource.getNeusteBuchungZuSpieler(spieler);

                    //Wenn keine Buchung vorhanden ist ist die Bu_ID der return Buchung -999
                    if (neusteSpielerBuchung.getBu_id() != -999) {

                        Double kto_saldo_neuAlsString = Double.valueOf(df.format(neusteSpielerBuchung.getKto_saldo_neu()).replace(',', '.'));

                        if (kto_saldo_neuAlsString == 0) {

                            AlertDialog.Builder dialog = new AlertDialog.Builder(context);

                            dialog.setTitle("Achtung")
                                    .setMessage(String.format(context.getResources().getString(R.string.dialogMessage_wirklichLoeschen), spieler.getVname(), spieler.getName()))
                                    .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    })
                                    .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int i) {
                                            new SpielerLoeschenAsyncTask(EditSpielerFragmentAdapter.this, spieler).execute();
                                            onLoeschenClick.onLoeschenClick();

                                            Intent intent = new Intent(context, SpielerVerwaltungActivity.class);
                                            context.startActivity(intent);
                                        }
                                    }).show();

                        } else if (kto_saldo_neuAlsString > 0) {

                            AlertDialog dialog = new AlertDialog.Builder(context).create();

                            dialog.setTitle("Achtung");
                            dialog.setMessage(String.format(context.getResources().getString(R.string.dialogMessage_BuchenBeiLoeschen), spieler.getVname(), spieler.getName(), df.format(neusteSpielerBuchung.getKto_saldo_neu())));
                            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Teamkonto", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialoginterface, int i) {

                                    new GetNeusteTeamBuchungenUndBereiteVorAsyncTask(EditSpielerFragmentAdapter.this, spieler).execute();
                                    new SpielerLoeschenAsyncTask(EditSpielerFragmentAdapter.this, spieler).execute();
                                    onLoeschenClick.onLoeschenClick();


                                    Intent intent = new Intent(context, SpielerVerwaltungActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    context.startActivity(intent);
                                }
                            });
                            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Bar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialoginterface, int i) {

                                    new GetNeusteSpielerBuchungenUndBereiteVorAsyncTask(EditSpielerFragmentAdapter.this, spieler).execute();
                                    new SpielerLoeschenAsyncTask(EditSpielerFragmentAdapter.this, spieler).execute();
                                    onLoeschenClick.onLoeschenClick();


                                    Intent intent = new Intent(context, SpielerVerwaltungActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    context.startActivity(intent);
                                }
                            });
                            dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Abbrechen", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialoginterface, int i) {
                                    dialoginterface.cancel();
                                }
                            });
                            dialog.show();

                        } else if (kto_saldo_neuAlsString < 0) {

                            AlertDialog.Builder dialog = new AlertDialog.Builder(context);

                            dialog.setTitle("Achtung")
                                    .setMessage(String.format(context.getResources().getString(R.string.dialogMessage_Ausgleichen), spieler.getVname(), spieler.getName(), df.format(neusteSpielerBuchung.getKto_saldo_neu())))
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialoginterface, int i) {
                                            dialoginterface.cancel();
                                        }
                                    }).show();
                        }

                    } else {
                        new SpielerLoeschenAsyncTask(EditSpielerFragmentAdapter.this, spieler).execute();
                        onLoeschenClick.onLoeschenClick();

                        Intent intent = new Intent(context, SpielerVerwaltungActivity.class);
                        context.startActivity(intent);
                    }

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