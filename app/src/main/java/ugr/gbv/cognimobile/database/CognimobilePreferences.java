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

        editor.putBoolean(firstTimeLaunchKey,value);
        editor.apply();
    }

}
