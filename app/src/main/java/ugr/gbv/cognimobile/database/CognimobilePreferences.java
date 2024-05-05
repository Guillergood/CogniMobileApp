package ugr.gbv.cognimobile.database;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.dto.TestDTO;


/**
 * Class to store all User preferences
 */
public class CognimobilePreferences {

    private static TestDTO mockedHttp;
    /**
     * Get preference for the first time launch
     *
     * @param context Context required for the {@link PreferenceManager}
     * @return true if it is the first time launch, false if not.
     */
    public static boolean getFirstTimeLaunch(Context context) {
        /* Key for accessing the preference for first time launch */
        String firstTimeLaunchKey = context.getString(R.string.pref_first_time);

        /* As usual, we use the default SharedPreferences to access the user's preferences */
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);


        /* If a value is stored with the key, we extract it here. If not, use a default. */
        return sp.getBoolean(firstTimeLaunchKey, true);
    }


    /**
     * Set preference for the first time launch
     *
     * @param context Context required for the {@link PreferenceManager}
     * @param value   to be introduced into the preferences.
     */
    public static void setFirstTimeLaunch(Context context, boolean value) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        /* Key for accessing the preference for showing notifications */
        String firstTimeLaunchKey = context.getString(R.string.pref_first_time);


        editor.putBoolean(firstTimeLaunchKey, value);
        editor.apply();
    }

    /**
     * Get preference for the input configuration in the test, the possible values are:
     * {@link ugr.gbv.cognimobile.fragments.Task#DEFAULT}
     * {@link ugr.gbv.cognimobile.fragments.Task#ONLY_TEXT}
     * {@link ugr.gbv.cognimobile.fragments.TextTask#ONLY_LANGUAGE}
     *
     * @param context Context required for the {@link PreferenceManager}
     */
    public static int getConfig(Context context) {
        /* Key for accessing the preference for first time launch */
        String config = context.getString(R.string.pref_config_value);

        /* As usual, we use the default SharedPreferences to access the user's preferences */
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);


        /* If a value is stored with the key, we extract it here. If not, use a default. */
        return sp.getInt(config, 0);
    }

    /**
     * Set preference for the input configuration in the test, the possible values are:
     * {@link ugr.gbv.cognimobile.fragments.Task#DEFAULT}
     * {@link ugr.gbv.cognimobile.fragments.Task#ONLY_TEXT}
     * {@link ugr.gbv.cognimobile.fragments.TextTask#ONLY_LANGUAGE}
     *
     * @param context Context required for the {@link PreferenceManager}
     * @param value   to be introduced into the preferences.
     */
    public static void setConfig(Context context, int value) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        /* Key for accessing the preference for showing notifications */
        String config = context.getString(R.string.pref_config_value);


        editor.putInt(config, value);
        editor.apply();
    }

    /**
     * Get preference for the notifications
     *
     * @param context Context required for the {@link PreferenceManager}
     * @return true if the user has notifications enabled, false if not.
     */
    public static boolean getNotifications(Context context) {
        /* Key for accessing the preference for first time launch */
        String config = context.getString(R.string.pref_notifications);

        /* As usual, we use the default SharedPreferences to access the user's preferences */
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);


        /* If a value is stored with the key, we extract it here. If not, use a default. */
        return sp.getBoolean(config, true);
    }

    /**
     * Set preference for the notifications
     *
     * @param context Context required for the {@link PreferenceManager}
     * @param value   to be introduced into the preferences.
     */
    public static void setNotifications(Context context, boolean value) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        /* Key for accessing the preference for showing notifications */
        String config = context.getString(R.string.pref_notifications);


        editor.putBoolean(config, value);
        editor.apply();
    }

    /**
     * Get if the user has joined a study
     *
     * @param context Context required for the {@link PreferenceManager}
     * @return true if the user has joined a study, false if not.
     */
    public static boolean getHasUserJoinedStudy(Context context) {
        /* Key for accessing the preference for first time launch */
        String config = context.getString(R.string.pref_joined_study);

        /* As usual, we use the default SharedPreferences to access the user's preferences */
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);


        /* If a value is stored with the key, we extract it here. If not, use a default. */
        return sp.getBoolean(config, false);
    }


    /**
     * Set if the user has joined a study
     *
     * @param context Context required for the {@link PreferenceManager}
     * @param value   to be introduced into the preferences.
     */
    public static void setHasUserJoinedStudy(Context context, boolean value) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        /* Key for accessing the preference for showing notifications */
        String config = context.getString(R.string.pref_joined_study);


        editor.putBoolean(config, value);
        editor.apply();
    }

    public static String getServerUrl(Context context) {
        /* Key for accessing the preference for first time launch */
        String config = context.getString(R.string.pref_server_url);

        /* As usual, we use the default SharedPreferences to access the user's preferences */
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);


        /* If a value is stored with the key, we extract it here. If not, use a default. */
        return sp.getString(config, "");
    }


    public static void setServerUrl(Context context, String value){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        /* Key for accessing the preference for showing notifications */
        String config = context.getString(R.string.pref_server_url);


        editor.putString(config, value);
        editor.apply();
    }

    public static String getLogin(Context context) {
        String config = context.getString(R.string.pref_login);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        return sp.getString(config, "");
    }

    public static void setLogin(Context context, String value){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        /* Key for accessing the preference for showing notifications */
        String config = context.getString(R.string.pref_login);


        editor.putString(config, value);
        editor.apply();
    }

    public static void setMockedHttp(TestDTO value) {
        mockedHttp = value;
    }

    public static boolean isMockedHttp() {
        return mockedHttp != null;
    }

    public static TestDTO getMockedHttp() {
        return mockedHttp;
    }
}
