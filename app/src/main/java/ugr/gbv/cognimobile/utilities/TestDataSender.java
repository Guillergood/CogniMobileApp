package ugr.gbv.cognimobile.utilities;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

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

//TODO: Hacer SINGLETON
public class TestDataSender implements Serializable {


    private static volatile TestDataSender instantiated;

    private TestDataSender(){

        if (instantiated != null){
            throw new RuntimeException("Use .getInstance() to instantiate TestDataSender");
        }
    }

    public static TestDataSender getInstance() {
        if (instantiated == null) {
            synchronized (TestDataSender.class) {
                if (instantiated == null) instantiated = new TestDataSender();
            }
        }

        return instantiated;
    }

    public void postToServer(String command, String table, JSONArray data, Context context) {


        HashMap<String, String> params = new HashMap<>();
        params.put("device_id", Aware.getSetting(context, Aware_Preferences.DEVICE_ID));

        Thread task = new Thread(){
            @Override
            public void run() {

                try {

                    String formattedData = formatData(data);

                    params.put("data",formattedData);

                    String urlString = buildURL(table,command, context);


                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.connect();


                    String postInformation = buildPostInformation(params);


                    sendData(conn,postInformation);


                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());

                    conn.disconnect();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };

        task.start();




    }

    private void sendData(HttpURLConnection conn,String postInformation) throws IOException {
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(postInformation);
        wr.flush();
        wr.close();
    }

    private String formatData(JSONArray data) throws JSONException {

        double timestamp = System.currentTimeMillis();


        JSONObject jsonParam = new JSONObject();
        //jsonParam.put("device_id", Aware.getSetting(context, Aware_Preferences.DEVICE_ID));
        jsonParam.put("timestamp", timestamp);
        //TODO array de resultados.
        jsonParam.put("data", data);

        Log.i("JSON",jsonParam.toString());

        JSONArray realData = new JSONArray();

        realData.put(jsonParam);

        Log.i("JSON",realData.toString());
        return realData.toString();
    }

    private String buildPostInformation(HashMap<String,String> params) {
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
