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

import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.database.Provider;

public class DataSender implements Serializable {


    private static volatile DataSender instantiated;
    public final static String INSERT = "insert";
    public final static String QUERY = "query";

    private DataSender(){

        if (instantiated != null){
            throw new RuntimeException("Use .getInstance() to instantiate TestDataSender");
        }
    }

    public static DataSender getInstance() {
        if (instantiated == null) {
            synchronized (DataSender.class) {
                if (instantiated == null) instantiated = new DataSender();
            }
        }

        return instantiated;
    }

    public void postToServer(String command, String table, @NonNull JSONArray data, Context context) {


        HashMap<String, Object> params = new HashMap<>();
        params.put("device_id", Aware.getSetting(context, Aware_Preferences.DEVICE_ID));


        Thread thread = new Thread(() -> {
            try {
                switch (command) {
                    case INSERT:
                        String formattedData = formatData(data);
                        params.put("data", formattedData);
                        break;
                /*case QUERY:
                    double timestamp = System.currentTimeMillis();
                    double zero = 0.0;
                    params.put("start","\"0\"");
                    params.put("end", "\""+timestamp+"\"");
                    break;*/
                    default:
                        throw new IllegalStateException("Unexpected value: " + command);
                }

                String urlString = buildURL(table, command, context);


                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.connect();


                String postInformation = buildPostInformation(params);


                sendData(conn, postInformation);


                int code = conn.getResponseCode();


                if (code == 200) {
                    ContentValues contentValues = new ContentValues();
                    long millis = getMillisThirtyDaysAhead();
                    contentValues.put(Provider.Cognimobile_Data.ERASE_TIMESTAMP, millis);
                    contentValues.put(Provider.Cognimobile_Data.DONE, 1);
                    updateValues(Provider.CONTENT_URI_TESTS, contentValues, context, data);
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


                conn.disconnect();
            } catch (IOException | JSONException e) {
                ErrorHandler.getInstance().displayError(context, e.getMessage());
            }
        });

        thread.start();




    }

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

    private long getMillisThirtyDaysAhead() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(c.get(Calendar.MILLISECOND));

        c.add(Calendar.DAY_OF_YEAR, R.integer.thirty);
        return c.getTimeInMillis();
    }

    private long getCurrentMillis() {
        Calendar c = Calendar.getInstance();
        return c.getTimeInMillis();
    }

    private void updateValues(Uri uri, ContentValues values, Context context, JSONArray data) throws JSONException {
        String where = Provider.Cognimobile_Data.NAME + " LIKE ?";
        String[] selectionArgs = {data.getJSONObject(0).getString("name")};

        context.getContentResolver().update(uri, values, where, selectionArgs);

    }

    private void deleteResult(Context context, JSONArray data) throws JSONException {
        String where = Provider.Cognimobile_Data.NAME + " LIKE ?";
        String[] selectionArgs = {data.getJSONObject(0).getString("name")};
        context.getContentResolver().delete(Provider.CONTENT_URI_RESULTS, where, selectionArgs);
    }


    private void sendData(HttpURLConnection conn,String postInformation) throws IOException {
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(postInformation);
        wr.flush();
        wr.close();
    }

    private String formatData(JSONArray data) throws JSONException {

        double timestamp = System.currentTimeMillis() / 1000.0;


        JSONObject jsonParam = new JSONObject();
        //jsonParam.put("device_id", Aware.getSetting(context, Aware_Preferences.DEVICE_ID));
        jsonParam.put("timestamp", timestamp);
        //TODO array de resultados.
        jsonParam.put("data", data);

        JSONArray realData = new JSONArray();

        realData.put(jsonParam);

        return realData.toString();
    }

    private String buildPostInformation(HashMap<String,Object> params) {
        StringBuilder postVariables = new StringBuilder();
        int i = 0;

        for (String key : params.keySet()) {

            if (i != 0){
                postVariables.append("&");
            }
            postVariables.append(key).append("=")
                    .append(params.get(key));
            i++;
        }

        return postVariables.toString();
    }


    private String buildURL(String table, String command, Context context) {
        Cursor studies = Aware.getStudy(context,"");
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
