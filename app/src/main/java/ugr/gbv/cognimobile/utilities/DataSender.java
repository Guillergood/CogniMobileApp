package ugr.gbv.cognimobile.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.icu.util.Calendar;

import android.widget.Toast;
import androidx.annotation.NonNull;

import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.database.CognimobilePreferences;
import ugr.gbv.cognimobile.database.ContentProvider;
import ugr.gbv.cognimobile.database.Provider;
import ugr.gbv.cognimobile.payload.response.JwtResponse;

/**
 * Class to send the data to the server
 */
public class DataSender implements Serializable {


    private static volatile DataSender instantiated;
    public final static String INSERT = "insert";
    private boolean retry = true;

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
     * @param data    the data to post
     * @param context parent activity context
     */
    public void postToServer(@NonNull Object data, Context context, String url) throws JsonProcessingException {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                CognimobilePreferences.getServerUrl(context) + url,
                response -> {
                    ErrorHandler.displayError("Datos enviados");
                },
                error -> {
                    //displaying the error in toast if occur
                    if (error.networkResponse.statusCode == 401) {
                        refreshAccessToken(context);
                    } else {
                        ErrorHandler.displayError("Error sending the data.");
                    }
                    Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    return objectMapper.writeValueAsBytes(data);
                } catch (JsonProcessingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of the answers or events.");
                    return null;
                }

            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    JwtResponse jwt = objectMapper.readValue(CognimobilePreferences.getLogin(context), JwtResponse.class);
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", jwt.getType() + " " + jwt.getToken());
                    return headers;
                } catch (JsonProcessingException e) {
                    VolleyLog.wtf("Could not parse the credentials to be used in the getTests call");
                    ErrorHandler.displayError("Something happened when loading the tests into the database");
                }
                return super.getHeaders();
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        //adding the string request to request queue
        requestQueue.add(stringRequest);

//          TODO REFORMULATE
//        HashMap<String, Object> params = new HashMap<>();
//        params.put("device_id", Aware.getSetting(context, Aware_Preferences.DEVICE_ID));
//
//
//        Thread thread = new Thread(() -> {
//            try {
//                if (INSERT.equals(command)) {
//                    String formattedData = formatData(data);
//                    params.put("data", formattedData);
//                } else {
//                    throw new IllegalStateException("Unexpected value: " + command);
//                }
//
//                String urlString = buildURL(table, command, context);
//
//
//                URL url = new URL(urlString);
//                int code = 0;
//                if (urlString.contains("https")) {
//                    code = sendWithHttps(url, params);
//                } else if (urlString.contains("http")) {
//                    code = sendWithHttp(url, params);
//                }
//
//
//                if (!data.getJSONObject(0).getString("name").isEmpty()) {
//                    if (code == 200) {
//                        ContentValues contentValues = new ContentValues();
//                        long millis = getMillisThirtyDaysAhead();
//                        contentValues.put(Provider.Cognimobile_Data.ERASE_TIMESTAMP, millis);
//                        //TODO Erased DONE by the moment
//                        contentValues.put(Provider.Cognimobile_Data.DONE, 0);
//                        updateValues(contentValues, context, data);
//                        contentValues.remove(Provider.Cognimobile_Data.DONE);
//                        //TODO Erased SYNCED by the moment
//                        contentValues.put(Provider.Cognimobile_Data.SYNCED, 0);
//                        deleteResult(context, data);
//                    } else {
//                        if (!isItAlreadyOnTheDatabase(context, data.getJSONObject(0).getString("name"))) {
//                            ContentValues[] contentValues = new ContentValues[1];
//                            ContentValues value = new ContentValues();
//                            value.put(Provider.Cognimobile_Data.NAME, data.getJSONObject(0).getString("name"));
//                            value.put(Provider.Cognimobile_Data.DATA, data.toString());
//                            value.put(Provider.Cognimobile_Data.DEVICE_ID, Aware.getSetting(context, Aware_Preferences.DEVICE_ID));
//
//                            context.getContentResolver().bulkInsert(
//                                    Provider.Cognimobile_Data.CONTENT_URI_RESULTS,
//                                    contentValues
//                            );
//                        }
//
//                    }
//                }
//
//
//            } catch (IOException | JSONException e) {
//                ErrorHandler.displayError(e.getMessage());
//            }
//        });
//
//        thread.start();


    }

    void refreshAccessToken(Context context) {

        JsonObjectRequest refreshTokenRequest = new JsonObjectRequest(Request.Method.POST,
                CognimobilePreferences.getServerUrl(context), null, response -> {
            try {
                String accessToken = response.getString("access_token");
                ObjectMapper objectMapper = new ObjectMapper();
                JwtResponse jwt = objectMapper.readValue(CognimobilePreferences.getLogin(context), JwtResponse.class);
                jwt.setToken(accessToken);
                CognimobilePreferences.setLogin(context,objectMapper.writeValueAsString(jwt));
            } catch (JSONException | JsonProcessingException e) {
                // this will never happen but if so, show error to user.
                ErrorHandler.displayError("Error refreshing the authentication.");
            }
        }, error -> {
            CognimobilePreferences.setLogin(context, "");
        });
        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        //adding the string request to request queue
        requestQueue.add(refreshTokenRequest);
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
//        Cursor studies = Aware.getStudy(context, "");
//        studies.moveToFirst();
//        String urlDb = studies.getString(studies.getColumnIndex(Aware_Provider.Aware_Studies.STUDY_URL));
//        Uri studyUri = Uri.parse(urlDb);
//        Uri.Builder urlBuilder = new Uri.Builder();
//        List<String> paths = studyUri.getPathSegments();
//
//        urlBuilder.scheme(studyUri.getScheme())
//                .authority(studyUri.getAuthority());
//
//        for (String path: paths) {
//            urlBuilder.appendPath(path);
//        }
//
//        urlBuilder.appendPath(table)
//                .appendPath(command);
//
//
//        return urlBuilder.build().toString().replaceAll("%3A",":");
        return "";
    }

}
