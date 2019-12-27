package eu.merscher.lbsvolleyball.controller;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.database.DataSource;
import eu.merscher.lbsvolleyball.model.Spieler;
import eu.merscher.lbsvolleyball.utilities.Utilities;


public class AddSpielerFragment extends Fragment {

    private AddSpielerFragmentAdapter adapter;

    public AddSpielerFragment() {
    }

    AddSpielerFragmentAdapter getAddSpielerFragmentAdapter() {
        return adapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add_edit_spieler, container, false);


        adapter = new AddSpielerFragmentAdapter(getActivity());
        RecyclerView recyclerView = rootView.findViewById(R.id.fragment_add_edit_spieler_recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return rootView;

    }

    public class AddSpielerFragmentAdapter extends RecyclerView.Adapter<AddSpielerFragment.AddSpielerFragmentAdapter.ViewHolder> {


        private final LayoutInflater inflate;
        private final Context context;
        private DataSource dataSource;
        private ViewHolder viewHolder;


        AddSpielerFragmentAdapter(Context context) {
            this.inflate = LayoutInflater.from(context);
            this.context = context;
        }

        ViewHolder getViewHolder() {
            return viewHolder;
        }

        @NotNull
        @Override
        public AddSpielerFragmentAdapter.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {

            View view = inflate.inflate(R.layout.fragment_add_spieler_item, parent, false);
            dataSource = DataSource.getInstance();

            viewHolder = new AddSpielerFragmentAdapter.ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NotNull AddSpielerFragmentAdapter.ViewHolder holder, int position) {
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements AddSpielerActivity.AddSpieler {
            final EditText editTextName;
            final EditText editTextVname;
            final EditText editTextBdate;
            final EditText editTextMail;
            //final Button buttonSpielerAnlegen;
            final ImageButton buttonLBSMail;
            EditText editTextAddBuchung;

            ViewHolder(final View view) {
                super(view);
                editTextName = view.findViewById(R.id.editText_name_add);
                editTextVname = view.findViewById(R.id.editText_vname_add);
                editTextBdate = view.findViewById(R.id.editText_bdate_add);
                editTextMail = view.findViewById(R.id.editText_mail_add);
                editTextAddBuchung = view.findViewById(R.id.fragment_kontodaten_editText_addBuchung);
                //buttonSpielerAnlegen = view.findViewById(R.id.fragement_add_spieler_button);
                buttonLBSMail = view.findViewById(R.id.fragment_add_spieler_button_lbsmail);
                editTextAddBuchung = view.findViewById(R.id.fragment_add_editText_AddBuchung);


                //Textformat Platzkosten
                editTextAddBuchung.setOnFocusChangeListener((v, hasFocus) -> Utilities.formatNumericEditText(editTextAddBuchung));

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

                                //                     0123456
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
            }

            //Spieler anlegen
            @Override
            public void onAddSpieler() {

                String name = editTextName.getText().toString();
                String vname = editTextVname.getText().toString();
                String bdate = editTextBdate.getText().toString();
                String mail = editTextMail.getText().toString();

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
                }

                if (!TextUtils.isEmpty(mail) && !mail.contains("@") && !mail.contains(".")) {
                    editTextBdate.setError(context.getString(R.string.editText_errorMessageMail));
                    return;
                }

                Spieler neuerSpieler;

                dataSource.open();

                if (AddSpielerActivity.getUserFotoAlsString() != null && TextUtils.isEmpty(mail)) {

                    neuerSpieler = dataSource.createSpieler(name, vname, bdate, 0, AddSpielerActivity.getUserFotoAlsString(), null, null);
                    AddSpielerActivity.setUserFotoAlsString(Utilities.bildNachSpielerBenennen(getContext(), neuerSpieler));
                    neuerSpieler = dataSource.updateFotoSpieler(neuerSpieler, AddSpielerActivity.getUserFotoAlsString());

                } else if (AddSpielerActivity.getUserFotoAlsString() == null && TextUtils.isEmpty(mail)) {

                    neuerSpieler = dataSource.createSpieler(name, vname, bdate, 0, "avatar_m", null, null);

                } else if (AddSpielerActivity.getUserFotoAlsString() == null) {

                    neuerSpieler = dataSource.createSpieler(name, vname, bdate, 0, "avatar_m", mail, null);

                } else {
                    neuerSpieler = dataSource.createSpieler(name, vname, bdate, 0, AddSpielerActivity.getUserFotoAlsString(), mail, null);
                    AddSpielerActivity.setUserFotoAlsString(Utilities.bildNachSpielerBenennen(getContext(), neuerSpieler));
                    neuerSpieler = dataSource.updateFotoSpieler(neuerSpieler, AddSpielerActivity.getUserFotoAlsString());
                }

                AddSpielerActivity.setUserFotoAlsString(null);

                DecimalFormat df = new DecimalFormat("0.00");

                if (!TextUtils.isEmpty(editTextAddBuchung.getText())) {
                    String bu_btr_String = df.format(Double.parseDouble(editTextAddBuchung.getText().toString().replace(',', '.')));
                    double bu_btr = Double.parseDouble(bu_btr_String.replace(',', '.'));
                    double kto_saldo_alt;
                    double kto_saldo_neu;


                    Calendar kalender = Calendar.getInstance();
                    SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);

                    kto_saldo_alt = 0; //der vorherige Kto_Saldo_neu ist nicht vorhanden, da keine vorherige Buchung existiert, daher 0
                    kto_saldo_neu = kto_saldo_alt + bu_btr;
                    dataSource.createBuchung(neuerSpieler.getS_id(), bu_btr, kto_saldo_alt, kto_saldo_neu, datumsformat.format(kalender.getTime()), null, -999, "X", null, -999);
                    dataSource.updateHatBuchungenMM(neuerSpieler);
                    dataSource.close();
                }

                Objects.requireNonNull(getActivity()).finish();

            }
        }
    }
}
