package eu.merscher.lbsvolleyball.controller;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import java.util.ArrayList;
import java.util.Objects;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.database.DataSource;
import eu.merscher.lbsvolleyball.model.Buchung;
import eu.merscher.lbsvolleyball.model.Spieler;
import eu.merscher.lbsvolleyball.model.Training;
import eu.merscher.lbsvolleyball.model.Trainingsort;
import eu.merscher.lbsvolleyball.utilities.Utilities;

public class EinstellungenFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

        // Laden der Preference-Elemente aus einer XML-Datei
        addPreferencesFromResource(R.xml.preferences);

        // Mit einem Listener werden Einstellungsänderungen überwacht
        Preference einstellungen_backup = findPreference("einstellungen_backup");


        Objects.requireNonNull(einstellungen_backup).setOnPreferenceChangeListener((preference, newValue) -> {

            DataSource dataSource = DataSource.getInstance();
            dataSource.open();

            switch ((String) newValue) {

                case "exp_spieler": {

                    ArrayList<Spieler> spielerList;
                    ArrayList<String> spielerListString = new ArrayList<>();

                    spielerList = dataSource.getAllSpieler();

                    for (Spieler spieler : spielerList) {
                        spielerListString.add(
                                spieler.getVname() + ";" +
                                        spieler.getName() + ";" +
                                        spieler.getBdate() + ";" +
                                        spieler.getTeilnahmen() + ";" +
                                        spieler.getMail() + ";" +
                                        spieler.getHat_buchung_mm() + ";");
                    }

                    boolean erfolg = Utilities.csvExport(Objects.requireNonNull(getContext()), "spieler", spielerListString);

                    if (erfolg) {

                        Toast toast = Toast.makeText(getContext(), "Es wurden " + spielerList.size() + " Spieler exportiert", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 20);
                        toast.show();

                    } else {

                        Toast toast = Toast.makeText(getContext(), "Fehler beim Export!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 20);
                        toast.show();
                    }

                    break;
                }

                case "imp_spieler": {

                    ArrayList<String> spielerListString = Utilities.csvImport(Objects.requireNonNull(getContext()), "spieler");
                    ArrayList<Spieler> spielerList = new ArrayList<>();

                    if (spielerListString != null) {
                        for (String string : spielerListString) {

                            String[] spielerArray = string.split(";");

                            String vname = spielerArray[0].replace("{", "");
                            String name = spielerArray[1];
                            String bdate = spielerArray[2];
                            int teilnahmen = Integer.parseInt(spielerArray[3]);
                            String mail = spielerArray[4];
                            String hat_buchung_mm = spielerArray[5];

                            if (mail.equals("null)"))
                                mail = null;
                            if (hat_buchung_mm.equals("null"))
                                hat_buchung_mm = null;

                            Spieler spieler = dataSource.createSpieler(name, vname, bdate, teilnahmen, "avatar_m", mail, hat_buchung_mm);
                            spielerList.add(spieler);
                        }

                        Toast toast = Toast.makeText(getContext(), "Es wurde(n) " + spielerList.size() + " Spieler importiert", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 20);
                        toast.show();
                    } else {
                        Toast toast = Toast.makeText(getContext(), "Fehler beim Import!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 20);
                        toast.show();
                    }
                    break;
                }

                case "exp_buchung": {

                    ArrayList<Buchung> buchungList;
                    ArrayList<String> buchungListString = new ArrayList<>();

                    buchungList = dataSource.getAllBuchungen();

                    for (Buchung buchung : buchungList) {
                        buchungListString.add(
                                buchung.getS_id() + ";" +
                                        buchung.getBu_btr() + ";" +
                                        buchung.getKto_saldo_alt() + ";" +
                                        buchung.getKto_saldo_neu() + ";" +
                                        buchung.getBu_date() + ";" +
                                        buchung.getIst_training_mm() + ";" +
                                        buchung.getTraining_id() + ";" +
                                        buchung.getIst_manuell_mm() + ";" +
                                        buchung.getIst_tunier_mm() + ";" +
                                        buchung.getTunier_id() + ";" +
                                        buchung.getIst_geloeschter_spieler_mm() + ";" +
                                        buchung.getGeloeschter_s_id() + ";");
                    }

                    boolean erfolg = Utilities.csvExport(Objects.requireNonNull(getContext()), "buchung", buchungListString);

                    if (erfolg) {

                        Toast toast = Toast.makeText(getContext(), "Es wurde(n) " + buchungList.size() + " Buchung(en) exportiert", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 20);
                        toast.show();

                    } else {

                        Toast toast = Toast.makeText(getContext(), "Fehler beim Export!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 20);
                        toast.show();
                    }

                    break;
                }

                case "imp_buchung": {

                    ArrayList<String> buchungListString = Utilities.csvImport(Objects.requireNonNull(getContext()), "buchung");
                    ArrayList<Buchung> buchungList = new ArrayList<>();

                    if (buchungListString != null) {
                        for (String string : buchungListString) {

                            String[] buchungArray = string.split(";");

                            long s_id = Long.parseLong(buchungArray[0].replace("{", ""));
                            double bu_btr = Double.parseDouble(buchungArray[1]);
                            double kto_saldo_alt = Double.parseDouble(buchungArray[2]);
                            double kto_saldo_neu = Double.parseDouble(buchungArray[3]);
                            String bu_date = buchungArray[4];
                            String ist_training_mm = buchungArray[5];
                            long training_id = Long.parseLong(buchungArray[6]);
                            String ist_manuell_mm = buchungArray[7];
                            String ist_tunier_mm = buchungArray[8];
                            long tunier_id = Long.parseLong(buchungArray[9]);
                            String ist_geloeschter_spieler_mm = buchungArray[10];
                            long geloeschter_s_id = Long.parseLong(buchungArray[11]);

                            if (ist_training_mm.equals("null)"))
                                ist_training_mm = null;
                            if (ist_manuell_mm.equals("null"))
                                ist_manuell_mm = null;
                            if (ist_geloeschter_spieler_mm.equals("null"))
                                ist_geloeschter_spieler_mm = null;
                            if (ist_tunier_mm.equals("null"))
                                ist_tunier_mm = null;

                            Buchung buchung = dataSource.createBuchung(s_id, bu_btr, kto_saldo_alt, kto_saldo_neu, bu_date, ist_training_mm, training_id, ist_manuell_mm, ist_tunier_mm, tunier_id, ist_geloeschter_spieler_mm, geloeschter_s_id);
                            buchungList.add(buchung);
                        }

                        Toast toast = Toast.makeText(getContext(), "Es wurde(n) " + buchungList.size() + " Buchung(en) importiert", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 20);
                        toast.show();
                    } else {
                        Toast toast = Toast.makeText(getContext(), "Fehler beim Import!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 20);
                        toast.show();
                    }
                    break;
                }

                case "exp_training": {

                    ArrayList<Training> trainingList;
                    ArrayList<String> trainingListString = new ArrayList<>();

                    trainingList = dataSource.getAllTraining();

                    for (Training training : trainingList) {
                        trainingListString.add(
                                training.getTraining_id() + ";" +
                                        training.getTraining_dtm() + ";" +
                                        training.getTrainings_ort_id() + ";" +
                                        training.getTrainings_tn() + ";" +
                                        training.getPlatzkosten() + ";" +
                                        training.getTraining_id() + ";" +
                                        training.getIst_kostenlos_mm() + ";");
                    }

                    boolean erfolg = Utilities.csvExport(Objects.requireNonNull(getContext()), "training", trainingListString);

                    if (erfolg) {

                        Toast toast = Toast.makeText(getContext(), "Es wurde(n) " + dataSource.getAllTrainingsID().size() + " Training(s) exportiert", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 20);
                        toast.show();

                    } else {

                        Toast toast = Toast.makeText(getContext(), "Fehler beim Export!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 20);
                        toast.show();
                    }

                    break;
                }

                case "imp_training": {

                    ArrayList<String> trainingListString = Utilities.csvImport(Objects.requireNonNull(getContext()), "training");
                    ArrayList<Training> trainingList = new ArrayList<>();

                    if (trainingListString != null) {
                        for (String string : trainingListString) {

                            String[] trainingArray = string.split(";");

                            long training_id = Long.parseLong(trainingArray[0].replace("{", ""));
                            String training_dtm = trainingArray[1];
                            long trainings_ort_id = Long.parseLong(trainingArray[2].replace("{", ""));
                            long trainings_tn = Long.parseLong(trainingArray[3].replace("{", ""));
                            double platzkosten = Double.parseDouble(trainingArray[4]);
                            String ist_kostenlos_mm = trainingArray[5];


                            if (ist_kostenlos_mm.equals("null)"))
                                ist_kostenlos_mm = null;

                            Training training = dataSource.createTraining(training_id, training_dtm, trainings_ort_id, trainings_tn, platzkosten, ist_kostenlos_mm);
                            trainingList.add(training);
                        }

                        Toast toast = Toast.makeText(getContext(), "Es wurde(n) " + trainingList.size() + " Training(s) importiert", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 20);
                        toast.show();
                    } else {
                        Toast toast = Toast.makeText(getContext(), "Fehler beim Import!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 20);
                        toast.show();
                    }
                    break;
                }

                case "exp_trainingsort": {

                    ArrayList<Trainingsort> trainingsorteList;
                    ArrayList<String> trainingsorteListString = new ArrayList<>();

                    trainingsorteList = dataSource.getAllTrainingsort();


                    for (Trainingsort trainingsort : trainingsorteList) {
                        trainingsorteListString.add(
                                trainingsort.getName() + ";" +
                                        trainingsort.getName() + ";" +
                                        trainingsort.getStrasse() + ";" +
                                        trainingsort.getPlz() + ";" +
                                        trainingsort.getLatitude() + ";" +
                                        trainingsort.getLongitude() + ";" +
                                        trainingsort.getBesuche());
                    }

                    boolean erfolg = Utilities.csvExport(Objects.requireNonNull(getContext()), "trainingsort", trainingsorteListString);

                    if (erfolg) {

                        Toast toast = Toast.makeText(getContext(), "Es wurde(n) " + trainingsorteList.size() + " Trainingsort(e) exportiert", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 20);
                        toast.show();

                    } else {

                        Toast toast = Toast.makeText(getContext(), "Fehler beim Export!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 20);
                        toast.show();
                    }

                    break;
                }

                case "imp_trainingsort": {

                    ArrayList<String> trainingsortListString = Utilities.csvImport(Objects.requireNonNull(getContext()), "spieler");
                    ArrayList<Trainingsort> trainingsortList = new ArrayList<>();

                    if (trainingsortListString != null) {
                        for (String string : trainingsortListString) {

                            String[] spielerArray = string.split(";");

                            String name = spielerArray[0].replace("{", "");
                            String strasse = spielerArray[1];
                            String plz = spielerArray[2];
                            String ort = spielerArray[2];
                            double latitude = Double.parseDouble(spielerArray[2]);
                            double longitude = Double.parseDouble(spielerArray[2]);
                            int besuche = Integer.parseInt(spielerArray[3]);

                            Trainingsort trainingsort = dataSource.createTrainingsort(name, strasse, plz, ort, "avatar_map", latitude, longitude, besuche);
                            trainingsortList.add(trainingsort);
                        }

                        Toast toast = Toast.makeText(getContext(), "Es wurde(n) " + trainingsortList.size() + " Trainingsort(e) importiert", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 20);
                        toast.show();
                    } else {
                        Toast toast = Toast.makeText(getContext(), "Fehler beim Import!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 20);
                        toast.show();
                    }
                    break;
                }
            }

            return true;
        });

    }
}
