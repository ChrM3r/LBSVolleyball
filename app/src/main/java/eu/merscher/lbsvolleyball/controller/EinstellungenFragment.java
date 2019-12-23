package eu.merscher.lbsvolleyball.controller;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import java.util.ArrayList;
import java.util.Objects;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.database.DataSource;
import eu.merscher.lbsvolleyball.model.Spieler;
import eu.merscher.lbsvolleyball.utilities.Utilities;

public class EinstellungenFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

        // Laden der Preference-Elemente aus einer XML-Datei
        addPreferencesFromResource(R.xml.preferences);

        // Mit einem Listener werden Einstellungsänderungen überwacht
        Preference einstellungen_backup = findPreference("einstellungen_backup");


        Objects.requireNonNull(einstellungen_backup).setOnPreferenceChangeListener((preference, newValue) -> {

            switch ((String) newValue) {

                case "exp_spieler": {
                    DataSource dataSource = DataSource.getInstance();
                    dataSource.open();
                    ArrayList<Spieler> spielerList;
                    ArrayList<String> spielerListString = new ArrayList<>();

                    spielerList = dataSource.getAllSpielerAlphabetischName();

                    for (Spieler spieler : spielerList) {
                        spielerListString.add(
                                spieler.getVname() + ";" +
                                        spieler.getName() + ";" +
                                        spieler.getBdate() + ";" +
                                        spieler.getMail() + ";");
                    }

                    Utilities.csvExport(spielerListString);
                    break;
                }

                case "imp_spieler": {

                    DataSource dataSource = DataSource.getInstance();
                    dataSource.open();

                    ArrayList<String> spielerListString = Utilities.csvImport();

                    for (String string : spielerListString) {

                        String[] spielerArray = string.split(";");

                        String vname = spielerArray[0].replace("{", "");
                        String name = spielerArray[1];
                        String bdate = spielerArray[2];
                        String mail = spielerArray[3];

                        dataSource.createSpieler(name, vname, bdate, 0, null, mail, null);
                    }

                    break;
                }

                default:
                    break;
            }

            return true;
        });

    }
}
