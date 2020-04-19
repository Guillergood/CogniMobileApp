package ugr.gbv.cognimobile.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.database.CognimobilePreferences;

public class SettingsFragments extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private Context context;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        PreferenceScreen prefScreen = getPreferenceScreen();
        int count = prefScreen.getPreferenceCount();

        context = getContext();
        if (context != null) {
            for (int i = 0; i < count; ++i) {
                Preference p = prefScreen.getPreference(i);
                final String key = p.getKey();
                Preference preference;
                preference = getPreferenceManager()
                        .findPreference(key);
                if (preference != null) {
                    if (getString(R.string.pref_notifications).equals(key)) {
                        preference.setDefaultValue(CognimobilePreferences.getNotifications(context));
                    }
                    if (getString(R.string.pref_config).equals(key)) {
                        String[] entries = getResources().getStringArray(R.array.configEntries);
                        preference.setDefaultValue(entries[CognimobilePreferences.getConfig(context)]);
                    }
                }
            }
        }

    }

    private void setPreferenceSummary(Preference preference, Object value) {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String frase = "Preference value was updated to: ";
        if (key.equals(context.getString(R.string.pref_notifications))) {
            boolean value = sharedPreferences.getBoolean(key, true);
            CognimobilePreferences.setNotifications(context, value);
            Log.i("PREFER", frase + value);
        }
        if (key.equals(context.getString(R.string.pref_config))) {
            String value = sharedPreferences.getString(context.getString(R.string.pref_config), "");
            int intValue = Integer.parseInt(value);
            CognimobilePreferences.setConfig(context, intValue);
            Log.i("PREFER", frase + value);
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}