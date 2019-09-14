package eu.merscher.lbsvolleyball.controller;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import eu.merscher.lbsvolleyball.R;

public class EinstellungenFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

        // Laden der Preference-Elemente aus einer XML-Datei
        addPreferencesFromResource(R.xml.preferences);

        // Mit einem Listener werden Einstellungsänderungen überwacht
        Preference preference = findPreference("einstellungen_platzkosten");
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String preferenceKey = preference.getKey();
                String preferenceValue = (String) newValue;
                TrainingFragment.getOnEinstellungChange().onEinstellungChange(preferenceValue);
                return true;
            }
        });
    }

    public interface OnEinstellungChange {
        void onEinstellungChange(String value);
    }
}
