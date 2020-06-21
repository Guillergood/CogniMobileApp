package ugr.gbv.cognimobile.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.providers.Aware_Provider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.database.Provider;

/**
 * Class to send the data to the server
 */
public class DataSender implements Serializable {


    private static volatile DataSender instantiated;
    public final static String INSERT = "insert";

    /**
     * Private constructor "singleton" pattern
     */
    private DataSender() {

        if (instantiated != null) {
            throw new RuntimeException("Use .instantiate() to instantiate TestDataSender");
        }
    }

    /**
     * Get instance of the singleton instance
     *
     * @return the unique instance of the class
     */
    public static DataSender getInstance() {
        if (instantiated == null) {
            synchronized (DataSender.class) {
                if (instantiated == null) instantiated = new DataSender();
            }
        }

        return instantiated;
    }

    /**
     * Post a command in the server, using a RESTful API
     *
     * @param command using any of the CRUD ones
     * @param table   the name of the table to commit the command
     * @param data    the data to post
     * @param context parent activity context
     */
    public void postToServer(String command, String table, @NonNull JSONArray data, Context context) {


        HashMap<String, Object> params = new HashMap<>();
        params.put("device_id", Aware.getSetting(context, Aware_Preferences.DEVICE_ID));


        Thread thread = new Thread(() -> {
            try {
                if (INSERT.equals(command)) {
                    String formattedData = formatData(data);
                    params.put("data", formattedData);
                } else {
                    throw new IllegalStateException("Unexpected value: " + command);
                }

                String urlString = buildURL(table, command, context);


                URL url = new URL(urlString);
                int code = 0;
                if (urlString.contains("https")) {
                    code = sendWithHttps(url, params);
                } else if (urlString.contains("http")) {
                    code = sendWithHttp(url, params);
                }


                if (!data.getJSONObject(0).getString("name").isEmpty()) {
                    if (code == 200) {
                        ContentValues contentValues = new ContentValues();
                        long millis = getMillisThirtyDaysAhead();
                        contentValues.put(Provider.Cognimobile_Data.ERASE_TIMESTAMP, millis);
                        contentValues.put(Provider.Cognimobile_Data.DONE, 1);
                        updateValues(contentValues, context, data);
                        contentValues.remove(Provider.Cognimobile_Data.DONE);
                        contentValues.put(Provider.Cognimobile_Data.SYNCED, 1);
                        deleteResult(context, data);
                    } else {
                        if (!isItAlreadyOnTheDatabase(context, data.getJSONObject(0).getString("name"))) {
                            ContentValues[] contentValues = new ContentValues[1];
                            ContentValues value = new ContentValues();
                            value.put(Provider.Cognimobile_Data.NAME, data.getJSONObject(0).getString("name"));
                            value.put(Provider.Cognimobile_Data.DATA, data.toString());
                            value.put(Provider.Cognimobile_Data.DEVICE_ID, Aware.getSetting(context, Aware_Preferences.DEVICE_ID));

                            context.getContentResolver().bulkInsert(
                                    Provider.Cognimobile_Data.CONTENT_URI_RESULTS,
                                    contentValues
                            );
                        }

                    }
                }


            } catch (IOException | JSONException e) {
                ErrorHandler.displayError(e.getMessage());
            }
        });

        thread.start();


    }

    /**
     * Send data with http url
     *
     * @param url    url where the data will be sent
     * @param params parameters to send
     * @return http connection
     * @throws IOException in case that there is no connection established.
     */
    private int sendWithHttp(URL url, HashMap<String, Object> params) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.connect();


        String postInformation = buildPostInformation(params);


        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(postInformation);
        wr.flush();
        wr.close();

        int value = conn.getResponseCode();
        conn.disconnect();
        return value;
    }

    /**
     * Send data with https url
     *
     * @param url    url where the data will be sent
     * @param params parameters to send
     * @return http connection
     * @throws IOException in case that there is no connection established.
     */
    private int sendWithHttps(URL url, HashMap<String, Object> params) throws IOException {
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.connect();


        String postInformation = buildPostInformation(params);


        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(postInformation);
        wr.flush();
        wr.close();

        return conn.getResponseCode();
    }

    /**
     * Checks if the test is in the local database
     *
     * @param context Parent context
     * @param name    of the test to check
     * @return true if it is, false if not.
     */
    private boolean isItAlreadyOnTheDatabase(Context context, String name) {

        boolean value = false;

        String[] projection = new String[]{Provider.Cognimobile_Data.NAME};
        String whereClause = Provider.Cognimobile_Data.NAME + " LIKE ? ";

        String[] whereArgs = new String[]{
                name,
        };
        Cursor cursor = context.getContentResolver().query(Provider.CONTENT_URI_TESTS, projection, whereClause, whereArgs, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            value = true;
        }
        return value;
    }

    /**
     * Get thirty days ahead of the current time in milliseconds
     *
     * @return thirty days ahead of the current time in milliseconds
     */
    private long getMillisThirtyDaysAhead() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(c.get(Calendar.MILLISECOND));

        c.add(Calendar.DAY_OF_YEAR, R.integer.thirty);
        return c.getTimeInMillis();
    }


    /**
     * Update values to mark those tests that has been sent successfully to the server.
     *
     * @param values  to update
     * @param context from the parent activity
     * @param data    to get the name of the test
     * @throws JSONException in case that the json is invalid.
     */
    private void updateValues(ContentValues values, Context context, JSONArray data) throws JSONException {
        String where = Provider.Cognimobile_Data.NAME + " LIKE ?";
        String[] selectionArgs = {data.getJSONObject(0).getString("name")};

        context.getContentResolver().update(Provider.CONTENT_URI_TESTS, values, where, selectionArgs);

    }

    /**
     * Delete results that has been sent successfully to the server.
     *
     * @param context from the parent activity
     * @param data    to get the name of the test
     * @throws JSONException in case that the json is invalid.
     */
    private void deleteResult(Context context, JSONArray data) throws JSONException {
        String where = Provider.Cognimobile_Data.NAME + " LIKE ?";
        String[] selectionArgs = {data.getJSONObject(0).getString("name")};
        context.getContentResolver().delete(Provider.CONTENT_URI_RESULTS, where, selectionArgs);
    }


    /**
     * Formats the json data field from AWARE database.
     *
     * @param data to put in the server database.
     * @throws JSONException in case that the json is invalid.
     */
    private String formatData(JSONArray data) throws JSONException {

        double timestamp = System.currentTimeMillis() / 1000.0;


        JSONObject jsonParam = new JSONObject();
        jsonParam.put("timestamp", timestamp);
        jsonParam.put("data", data);

        JSONArray realData = new JSONArray();

        realData.put(jsonParam);

        return realData.toString();
    }

    /**
     * Append parameters to the url where the information will be sent
     *
     * @param params to put inside the url
     * @return the complete url with parameters
     */
    private String buildPostInformation(HashMap<String, Object> params) {
        StringBuilder postVariables = new StringBuilder();
        int i = 0;

        for (String key : params.keySet()) {

            if (i != 0) {
                postVariables.append("&");
            }
            postVariables.append(key).append("=")
                    .append(params.get(key));
            i++;
        }

        return postVariables.toString();
    }


    /**
     * Build the url where the information will be sent
     *
     * @return the complete url with parameters
     */
    private String buildURL(String table, String command, Context context) {
        Cursor studies = Aware.getStudy(context, "");
        studies.moveToFirst();
        String urlDb = studies.getString(studies.getColumnIndex(Aware_Provider.Aware_Studies.STUDY_URL));
        Uri studyUri = Uri.parse(urlDb);
        Uri.Builder urlBuilder = new Uri.Builder();
        List<String> paths = studyUri.getPathSegments();

        urlBuilder.scheme(studyUri.getScheme())
                .authority(studyUri.getAuthority());

        for (String path: paths) {
            urlBuilder.appendPath(path);
        }

        urlBuilder.appendPath(table)
                .appendPath(command);


        return urlBuilder.build().toString().replaceAll("%3A",":");
    }

}
