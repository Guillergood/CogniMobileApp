package ugr.gbv.cognimobile.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.activities.About;
import ugr.gbv.cognimobile.activities.LoginActivity;
import ugr.gbv.cognimobile.activities.ServerUrlRetrieval;
import ugr.gbv.cognimobile.database.CognimobilePreferences;
import ugr.gbv.cognimobile.interfaces.SettingsCallback;

/**
 * Fragment to display the Settings section in {@link ugr.gbv.cognimobile.activities.MainActivity}
 */
public class SettingsFragments extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private Context context;
    private SettingsCallback callback;
    public SettingsFragments(SettingsCallback callback) {
        this.callback = callback;
    }

    /**
     * Overrides {@link PreferenceFragmentCompat#onCreatePreferences(Bundle, String)}
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state,
     *                           this is the state.
     * @param rootKey            If non-null, this preference fragment should be rooted at the
     *                           {@link PreferenceScreen} with this key.
     */
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
                    if (getString(R.string.pref_logout).equals(key)){
                        preference.setOnPreferenceClickListener(preference1 -> {
                            CognimobilePreferences.setLogin(context, "");
                            Intent intent = new Intent(context, LoginActivity.class);
                            startActivity(intent);
                            callback.finishActivity();
                            return true;
                        });
                    }
                    if (getString(R.string.pref_change_server).equals(key)){
                        preference.setOnPreferenceClickListener(preference1 -> {
                            CognimobilePreferences.setServerUrl(context, "");
                            Intent intent = new Intent(context, ServerUrlRetrieval.class);
                            startActivity(intent);
                            callback.finishActivity();
                            return true;
                        });
                    }
                    if (getString(R.string.pref_about).equals(key)) {
                        preference.setOnPreferenceClickListener(preference1 -> {
                            Intent intent = new Intent(context, About.class);
                            startActivity(intent);
                            return true;
                        });
                    }
                }
            }
        }

    }

    /**
     * Overrides {@link SharedPreferences.OnSharedPreferenceChangeListener#onSharedPreferenceChanged(SharedPreferences, String)}
     *
     * @param sharedPreferences The {@link SharedPreferences} that received
     *                          the change.
     * @param key               The key of the preference that was changed, added, or
     *                          removed.
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(context.getString(R.string.pref_notifications))) {
            boolean value = sharedPreferences.getBoolean(key, true);
            CognimobilePreferences.setNotifications(context, value);
        }
        if (key.equals(context.getString(R.string.pref_config))) {
            String value = sharedPreferences.getString(context.getString(R.string.pref_config), "");
            int intValue = Integer.parseInt(value);
            CognimobilePreferences.setConfig(context, intValue);
        }


    }


    /**
     * Overrides {@link Fragment#onResume()}
     * to register the OnSharedPreferenceChangeListener
     */
    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * Overrides {@link Fragment#onPause()}
     * to unregister the OnSharedPreferenceChangeListener
     */
    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}