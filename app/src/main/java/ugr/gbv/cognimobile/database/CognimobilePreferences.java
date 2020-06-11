package ugr.gbv.cognimobile.database;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import ugr.gbv.cognimobile.R;

public class CognimobilePreferences {

    public static boolean getFirstTimeLaunch(Context context) {
        /* Key for accessing the preference for first time launch */
        String firstTimeLaunchKey = context.getString(R.string.pref_first_time);

        /* As usual, we use the default SharedPreferences to access the user's preferences */
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);


        /* If a value is stored with the key, we extract it here. If not, use a default. */
        return sp.getBoolean(firstTimeLaunchKey, true);
    }


    public static void setFirstTimeLaunch(Context context, boolean value){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        /* Key for accessing the preference for showing notifications */
        String firstTimeLaunchKey = context.getString(R.string.pref_first_time);


        //editor.putBoolean(firstTimeLaunchKey,value);
        editor.putBoolean(firstTimeLaunchKey, value);
        editor.apply();
    }

    //TODO: CAMBIAR PARA LA RELEASE
    public static int getConfig(Context context) {
        /* Key for accessing the preference for first time launch */
        String config = context.getString(R.string.pref_config_value);

        /* As usual, we use the default SharedPreferences to access the user's preferences */
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);


        /* If a value is stored with the key, we extract it here. If not, use a default. */
        return sp.getInt(config, 0);
    }


    public static void setConfig(Context context, int value) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        /* Key for accessing the preference for showing notifications */
        String config = context.getString(R.string.pref_config_value);


        //editor.putBoolean(firstTimeLaunchKey,value);
        editor.putInt(config, value);
        editor.apply();
    }

    public static boolean getNotifications(Context context) {
        /* Key for accessing the preference for first time launch */
        String config = context.getString(R.string.pref_notifications);

        /* As usual, we use the default SharedPreferences to access the user's preferences */
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);


        /* If a value is stored with the key, we extract it here. If not, use a default. */
        return sp.getBoolean(config, true);
    }

    /**
     * @param context
     * @param value
     */

    public static void setNotifications(Context context, boolean value) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        /* Key for accessing the preference for showing notifications */
        String config = context.getString(R.string.pref_notifications);


        editor.putBoolean(config, value);
        editor.apply();
    }

    public static boolean getHasUserJoinedStudy(Context context) {
        /* Key for accessing the preference for first time launch */
        String config = context.getString(R.string.pref_joined_study);

        /* As usual, we use the default SharedPreferences to access the user's preferences */
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);


        /* If a value is stored with the key, we extract it here. If not, use a default. */
        return sp.getBoolean(config, false);
    }


    public static void setHasUserJoinedStudy(Context context, boolean value) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        /* Key for accessing the preference for showing notifications */
        String config = context.getString(R.string.pref_joined_study);


        editor.putBoolean(config, value);
        editor.apply();
    }

}
