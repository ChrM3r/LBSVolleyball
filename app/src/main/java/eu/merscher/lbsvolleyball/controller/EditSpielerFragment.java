package eu.merscher.lbsvolleyball.controller;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.database.DataSource;
import eu.merscher.lbsvolleyball.model.Buchung;
import eu.merscher.lbsvolleyball.model.Spieler;
import eu.merscher.lbsvolleyball.utilities.Utilities;


public class EditSpielerFragment extends Fragment {

    private final Spieler spieler;
    private OnEditFinish onEditFinish;
    EditSpielerFragmentAdapter adapter;


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

        //onSpeichernClick = this;
        //onLoeschenClick = this;

        onEditFinish = SpielerseiteActivity.getOnEditFinish();

        adapter = new EditSpielerFragmentAdapter(getActivity(), spieler);
        RecyclerView recyclerView = rootView.findViewById(R.id.fragment_add_edit_spieler_recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return rootView;

    }

    public void onSpeichernClick() {

        System.out.println("onSpeicherClick");
        Objects.requireNonNull(getActivity()).finish();
    }

    public void onLoeschenClick() {
        Objects.requireNonNull(getActivity()).finish();
        onEditFinish.onEditFinish();

    }

    public interface OnEditFinish {
        void onEditFinish();
    }


    public class EditSpielerFragmentAdapter extends RecyclerView.Adapter<EditSpielerFragment.EditSpielerFragmentAdapter.ViewHolder> {

        private final DecimalFormat df = new DecimalFormat("0.00");
        private Buchung neusteSpielerBuchung;
        private Buchung neusteTeamBuchung;
        private final LayoutInflater inflate;
        private final Context context;
        private final Spieler spieler;
        private ViewHolder holder;

        EditSpielerFragmentAdapter(Context context, Spieler spieler) {
            this.inflate = LayoutInflater.from(context);
            this.context = context;
            this.spieler = spieler;
        }

        public EditSpielerFragmentAdapter.ViewHolder getHolder() {
            return holder;
        }

        public void setHolder(EditSpielerFragmentAdapter.ViewHolder holder) {
            this.holder = holder;
        }

        @NotNull
        @Override
        public EditSpielerFragmentAdapter.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {

            View view = inflate.inflate(R.layout.fragment_edit_spieler_item, parent, false);
            return new EditSpielerFragmentAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NotNull EditSpielerFragmentAdapter.ViewHolder holder, int position) {
            this.holder = holder;
        }

        @Override
        public int getItemCount() {
            return 1;
        }


        protected class ViewHolder extends RecyclerView.ViewHolder {
            final EditText editTextName;
            final EditText editTextVname;
            final EditText editTextBdate;
            final EditText editTextMail;
            final Button buttonSpielerLoeschen;
            final ImageButton buttonLBSMail;

            ViewHolder(final View view) {
                super(view);
                editTextName = view.findViewById(R.id.editText_name_edit);
                editTextVname = view.findViewById(R.id.editText_vname_edit);
                editTextBdate = view.findViewById(R.id.editText_bdate_edit);
                editTextMail = view.findViewById(R.id.editText_mail_edit);
                buttonSpielerLoeschen = view.findViewById(R.id.fragment_edit_spieler_loeschen_button);
                buttonLBSMail = view.findViewById(R.id.fragment_edit_spieler_button_lbsmail);

                editTextName.setText(spieler.getName());
                editTextVname.setText(spieler.getVname());
                editTextBdate.setText(spieler.getBdate());
                editTextMail.setText(spieler.getMail());


                //Textformat Geburtstag
                editTextBdate.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        String[] datumArray;
                        String tag;
                        String monat;
                        String jahr;
                        String geburttag;

                        String text = s.toString();
                        int laenge = text.length();

                        if (text.split("\\.").length == 3) {

                            datumArray = text.split("\\.");
                            //Wenn Datum im Format D.M.JJJJ
                            if (datumArray[2].length() == 4 && datumArray[1].length() == 1 && datumArray[0].length() == 1) {

                                tag = datumArray[0];
                                tag = "0" + tag;
                                monat = datumArray[1];
                                monat = "0" + monat;
                                jahr = datumArray[2];
                                geburttag = tag + "." + monat + "." + jahr;
                                editTextBdate.setText(geburttag);
                                editTextBdate.setSelection(editTextBdate.getText().length());


                                //Wenn Datum im Format D.MM.JJJJ
                            } else if (datumArray[2].length() == 4 && datumArray[1].length() == 2 && datumArray[0].length() == 1) {

                                tag = datumArray[0];
                                tag = "0" + tag;
                                monat = datumArray[1];
                                jahr = datumArray[2];
                                geburttag = tag + "." + monat + "." + jahr;
                                editTextBdate.setText(geburttag);
                                editTextBdate.setSelection(editTextBdate.getText().length());


                                //Wenn Datum im Format DD.M.JJJJ
                            } else if (datumArray[2].length() == 4 && datumArray[1].length() == 1 && datumArray[0].length() == 2) {

                                tag = datumArray[0];
                                monat = datumArray[1];
                                monat = "0" + monat;
                                jahr = datumArray[2];
                                geburttag = tag + "." + monat + "." + jahr;
                                editTextBdate.setText(geburttag);
                                editTextBdate.setSelection(editTextBdate.getText().length());


                            }
                        } else {

                            //Wenn Datum im Format DMJJJJ
                            if (!text.contains(".") && laenge == 6 && !text.substring(0, 2).contains("0") && (text.substring(2, 4).equals("19") || text.substring(2, 4).equals("20"))) {

                                tag = text.substring(0, 1);
                                tag = "0" + tag;
                                monat = text.substring(1, 2);
                                monat = "0" + monat;
                                jahr = text.substring(2, 6);

                                geburttag = tag + "." + monat + "." + jahr;
                                editTextBdate.setText(geburttag);
                                editTextBdate.setSelection(editTextBdate.getText().length());

                                //Wenn Datum im Format DMMJJJJ/
                            } else {

                                boolean jahreszahlanfang = !text.contains(".") && laenge == 7 && (text.substring(3, 5).equals("19") || text.substring(3, 5).equals("20"));

                                if (
                                        jahreszahlanfang &&
                                                (!text.substring(0, 4).contains("0") || text.substring(1, 3).equals("10"))) {

                                    tag = text.substring(0, 1);
                                    tag = "0" + tag;
                                    monat = text.substring(1, 3);
                                    jahr = text.substring(3, 7);

                                    geburttag = tag + "." + monat + "." + jahr;
                                    editTextBdate.setText(geburttag);
                                    editTextBdate.setSelection(editTextBdate.getText().length());

                                    //Wenn Datum im Format DDMJJJJ/
                                } else if (
                                        jahreszahlanfang
                                                && (!text.substring(0, 3).contains("0") || text.substring(0, 2).equals("10") || text.substring(0, 2).equals("20") || text.substring(0, 2).equals("30"))) {

                                    tag = text.substring(0, 2);
                                    monat = text.substring(2, 3);
                                    monat = "0" + monat;
                                    jahr = text.substring(3, 7);

                                    geburttag = tag + "." + monat + "." + jahr;
                                    editTextBdate.setText(geburttag);
                                    editTextBdate.setSelection(editTextBdate.getText().length());

                                    //Wenn Datum im Format DDMMJJJJ
                                } else if (!text.contains(".") && laenge == 8 && (text.substring(4, 6).equals("19") || text.substring(4, 6).equals("20"))) {

                                    tag = text.substring(0, 2);
                                    monat = text.substring(2, 4);
                                    jahr = text.substring(4, 8);

                                    geburttag = tag + "." + monat + "." + jahr;
                                    editTextBdate.setText(geburttag);
                                    editTextBdate.setSelection(editTextBdate.getText().length());
                                }
                            }
                        }
                    }
                });


                //LBS Mail einfügen
                buttonLBSMail.setOnClickListener(v -> {

                    String name = editTextName.getText().toString().toLowerCase();
                    String vname = editTextVname.getText().toString().toLowerCase();

                    if (TextUtils.isEmpty(vname)) {
                        editTextVname.setError(context.getString(R.string.editText_errorMessage_empty));
                        return;
                    }

                    if (TextUtils.isEmpty(name)) {
                        editTextName.setError(context.getString(R.string.editText_errorMessage_empty));
                        return;
                    }

                    String lbsMail = vname + "." + name + "@lbs-ost.de";

                    editTextMail.setText(lbsMail);
                });

                buttonSpielerLoeschen.setOnClickListener(v -> {

                    DataSource dataSource = DataSource.getInstance();
                    dataSource.open();
                    Calendar kalender = Calendar.getInstance();
                    SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);

                    neusteSpielerBuchung = dataSource.getNeusteBuchungZuSpieler(spieler);

                    if (neusteSpielerBuchung.getKto_saldo_neu() == null || Double.parseDouble(df.format(neusteSpielerBuchung.getKto_saldo_neu()).replace(",", ".")) == 0) {

                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);

                        dialog.setTitle("Achtung")
                                .setMessage(String.format(context.getResources().getString(R.string.dialogMessage_wirklichLoeschen), spieler.getVname(), spieler.getName()))
                                .setNegativeButton("Abbrechen", (dialog1, which) -> dialog1.cancel())
                                .setPositiveButton("Ja", (dialog12, i) -> {
                                    new SpielerLoeschenAsyncTask(EditSpielerFragmentAdapter.this, spieler).execute();
                                    onLoeschenClick();
                                    if (neusteSpielerBuchung.getKto_saldo_neu() > 0 || neusteSpielerBuchung.getKto_saldo_neu() < 0) {

                                        Buchung neusteTeamBuchung = dataSource.getNeusteBuchungZuTeamkonto();
                                        double bu_btr = neusteSpielerBuchung.getKto_saldo_neu();
                                        double kto_saldo_alt = neusteTeamBuchung.getKto_saldo_neu();
                                        double kto_saldo_neu = kto_saldo_alt + bu_btr;

                                        dataSource.createBuchungAufTeamkonto(bu_btr, kto_saldo_alt, kto_saldo_neu, datumsformat.format(kalender.getTime()), null, -999, null, null, -999, "X", spieler.getS_id());

                                    }
                                    Intent intent = new Intent(context, SpielerVerwaltungActivity.class);
                                    context.startActivity(intent);
                                }).show();

                    } else if (Double.parseDouble(df.format(neusteSpielerBuchung.getKto_saldo_neu()).replace(",", ".")) > 0) {

                        AlertDialog dialog = new AlertDialog.Builder(context).create();

                        dialog.setTitle("Achtung");
                        dialog.setMessage(String.format(context.getResources().getString(R.string.dialogMessage_BuchenBeiLoeschen), spieler.getVname(), spieler.getName(), df.format(neusteSpielerBuchung.getKto_saldo_neu())));
                        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Teamkonto", (dialoginterface, i) -> {

                            neusteSpielerBuchung = dataSource.getNeusteBuchungZuSpieler(spieler);
                            neusteTeamBuchung = dataSource.getNeusteBuchungZuTeamkonto();

                            if (neusteTeamBuchung != null) {

                                double bu_btr = neusteSpielerBuchung.getKto_saldo_neu();
                                double kto_saldo_alt = neusteTeamBuchung.getKto_saldo_neu();
                                double kto_saldo_neu = kto_saldo_alt + bu_btr;


                                dataSource.createBuchungAufTeamkonto(bu_btr, kto_saldo_alt, kto_saldo_neu, datumsformat.format(kalender.getTime()), null, -999, null, null, -999, "X", spieler.getS_id());


                            } else {

                                double bu_btr = neusteSpielerBuchung.getKto_saldo_neu();
                                double kto_saldo_alt = 0; //der vorherige Kto_Saldo_neu ist nicht vorhanden, da keine vorherige Buchung existiert, daher 0
                                double kto_saldo_neu = kto_saldo_alt + bu_btr;

                                dataSource.createBuchungAufTeamkonto(bu_btr, kto_saldo_alt, kto_saldo_neu, datumsformat.format(kalender.getTime()), null, -999, null, null, -999, "X", spieler.getS_id());

                            }

                            new SpielerLoeschenAsyncTask(EditSpielerFragmentAdapter.this, spieler).execute();
                            onLoeschenClick();

                            System.out.println("Teamkonto");
                            Intent intent = new Intent(context, SpielerVerwaltungActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);
                        });
                        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Bar", (dialoginterface, i) -> {

                            dataSource.open();
                            neusteSpielerBuchung = dataSource.getNeusteBuchungZuSpieler(spieler);

                            double bu_btr = Double.parseDouble(df.format(neusteSpielerBuchung.getKto_saldo_neu()).replace(",", "."));
                            double kto_saldo_alt = neusteSpielerBuchung.getKto_saldo_neu();
                            double kto_saldo_neu = kto_saldo_alt - bu_btr;

                            dataSource.createBuchung(spieler.getS_id(), bu_btr, kto_saldo_alt, kto_saldo_neu, datumsformat.format(kalender.getTime()), null, -999, null, null, -999, "X", spieler.getS_id());

                            if (bu_btr != neusteSpielerBuchung.getKto_saldo_neu()) {

                                Buchung neusteTeamBuchung = dataSource.getNeusteBuchungZuTeamkonto();
                                double differenz = neusteSpielerBuchung.getKto_saldo_neu() - bu_btr;
                                double saldoTeamAlt = neusteTeamBuchung.getKto_saldo_neu();
                                double saldoTeamNeu = saldoTeamAlt + differenz;

                                dataSource.createBuchungAufTeamkonto(differenz, saldoTeamAlt, saldoTeamNeu, datumsformat.format(kalender.getTime()), null, -999, null, null, -999, "X", spieler.getS_id());


                            }
                            new SpielerLoeschenAsyncTask(EditSpielerFragmentAdapter.this, spieler).execute();
                            onLoeschenClick();

                            System.out.println("Bar");

                            Intent intent = new Intent(context, SpielerVerwaltungActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);
                        });
                        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Abbrechen", (dialoginterface, i) -> dialoginterface.cancel());
                        dialog.show();

                    } else if (Double.parseDouble(df.format(neusteSpielerBuchung.getKto_saldo_neu()).replace(",", ".")) < 0) {

                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);

                        dialog.setTitle("Achtung")
                                .setMessage(String.format(context.getResources().getString(R.string.dialogMessage_Ausgleichen), spieler.getVname(), spieler.getName(), df.format(neusteSpielerBuchung.getKto_saldo_neu())))
                                .setPositiveButton("Ok", (dialoginterface, i) -> dialoginterface.cancel()).show();
                    }
                });

            }

            void onSpielerSpeichernClick() {
                String name = editTextName.getText().toString();
                String vname = editTextVname.getText().toString();
                String bdate = editTextBdate.getText().toString();
                String mail = editTextMail.getText().toString();
                String foto = spieler.getFoto();
                String userFotoAlsString = EditSpielerActivity.getUserFotoAlsString();


                if (TextUtils.isEmpty(vname)) {
                    editTextVname.setError(context.getString(R.string.editText_errorMessage_empty));
                    return;
                }

                if (TextUtils.isEmpty(name)) {
                    editTextName.setError(context.getString(R.string.editText_errorMessage_empty));
                    return;
                }

                if (TextUtils.isEmpty(bdate)) {
                    editTextBdate.setError(context.getString(R.string.editText_errorMessage_empty));
                    return;

                } else if (bdate.split("\\.").length != 3) {
                    editTextBdate.setError(context.getString(R.string.editText_errorMessage_bdate));
                    return;

                } else if (Integer.parseInt(bdate.split("\\.")[0]) > 31 || Integer.parseInt(bdate.split("\\.")[1]) > 12) {
                    editTextBdate.setError(context.getString(R.string.editText_errorMessage_bdate2));
                    return;
                }

                if (!TextUtils.isEmpty(mail)) {
                    if (!mail.contains("@") || !mail.split("@")[1].contains(".") || (mail.split("@")[1].split("\\.")[1] == null)) {
                        editTextMail.setError(context.getString(R.string.editText_errorMessageMail));
                        return;
                    }
                }


                new SpielerUpdateAsyncTask(EditSpielerFragmentAdapter.this, spieler, name, vname, bdate, userFotoAlsString, foto, mail).execute();
                onSpeichernClick();

            }
        }


    }

    static class SpielerUpdateAsyncTask extends AsyncTask<Void, Void, Spieler> {


        final WeakReference<EditSpielerFragmentAdapter> activityReference;

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
        protected Spieler doInBackground(Void... args) {

            DataSource dataSource = DataSource.getInstance();
            dataSource.open();

            Spieler updatedSpieler = spieler;


            if (EditSpielerActivity.getUserFotoAlsString() == null && !TextUtils.isEmpty(mail)) {
                updatedSpieler = dataSource.updateSpieler(spieler.getS_id(), name, vname, bdate, spieler.getTeilnahmen(), spieler.getFoto(), mail, spieler.getHat_buchung_mm());

            } else if (EditSpielerActivity.getUserFotoAlsString() == null && TextUtils.isEmpty(mail)) {

                updatedSpieler = dataSource.updateSpieler(spieler.getS_id(), name, vname, bdate, spieler.getTeilnahmen(), foto, null, spieler.getHat_buchung_mm());

            } else if (EditSpielerActivity.getUserFotoAlsString().equals("geloescht") && TextUtils.isEmpty(mail)) {

                updatedSpieler = dataSource.updateSpieler(spieler.getS_id(), name, vname, bdate, spieler.getTeilnahmen(), "avatar_m", null, spieler.getHat_buchung_mm());


            } else if (EditSpielerActivity.getUserFotoAlsString().equals("geloescht") && !TextUtils.isEmpty(mail)) {

                updatedSpieler = dataSource.updateSpieler(spieler.getS_id(), name, vname, bdate, spieler.getTeilnahmen(), "avatar_m", mail, spieler.getHat_buchung_mm());


            } else if (EditSpielerActivity.getUserFotoAlsString() != null && TextUtils.isEmpty(mail)) {

                updatedSpieler = dataSource.updateSpieler(spieler.getS_id(), name, vname, bdate, spieler.getTeilnahmen(), userFotoAlsString, null, spieler.getHat_buchung_mm());
                EditSpielerActivity.setUserFotoAlsString(Utilities.bildNachSpielerBenennen(activityReference.get().context, updatedSpieler));
                updatedSpieler = dataSource.updateFotoSpieler(updatedSpieler, EditSpielerActivity.getUserFotoAlsString());
                EditSpielerActivity.setUserFotoAlsString(null);

            } else if (EditSpielerActivity.getUserFotoAlsString() != null && !TextUtils.isEmpty(mail)) {

                updatedSpieler = dataSource.updateSpieler(spieler.getS_id(), name, vname, bdate, spieler.getTeilnahmen(), EditSpielerActivity.getUserFotoAlsString(), mail, spieler.getHat_buchung_mm());
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
                File bild = new File(directory, updatedSpieler.getS_id() + "_" + updatedSpieler.getName() + ".png");
                String foto = bild.getAbsolutePath();

                return new Spieler(updatedSpieler.getS_id(), updatedSpieler.getName(), updatedSpieler.getVname(),
                        updatedSpieler.getBdate(), updatedSpieler.getTeilnahmen(),
                        foto, updatedSpieler.getMail(), updatedSpieler.getHat_buchung_mm());
            } else {
                ContextWrapper cw = new ContextWrapper(activityReference.get().context);
                File directory = cw.getDir("profilbilder", Context.MODE_PRIVATE);
                File bild = new File(directory, spieler.getS_id() + "_" + spieler.getName() + ".png");
                boolean geloescht = bild.delete();
                Log.d("Spieler gelöscht.", Boolean.toString(geloescht));
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

        }

        public interface OnSpeichernClick {
            void onSpeichernClick();
        }
    }

    static class SpielerLoeschenAsyncTask extends AsyncTask<Void, Void, Void> {

        private final WeakReference<EditSpielerFragmentAdapter> activityReference;

        private final Spieler spieler;

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
            File bild = new File(directory, spieler.getS_id() + "_" + spieler.getName() + ".png");
            boolean geloescht = bild.delete();
            Log.d("Spieler gelöscht.", Boolean.toString(geloescht));
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {

        }
    }


}
