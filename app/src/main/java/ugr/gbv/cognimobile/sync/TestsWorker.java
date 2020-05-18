package ugr.gbv.cognimobile.sync;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.aware.Aware;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

import ugr.gbv.cognimobile.database.Provider;
import ugr.gbv.cognimobile.utilities.ErrorHandler;
import ugr.gbv.cognimobile.utilities.JsonParserTests;
import ugr.gbv.cognimobile.utilities.NotificationUtils;

public class TestsWorker extends Worker {

    private Context workerContext;

    public TestsWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        workerContext = context;
    }

    @NonNull
    @Override
    public Result doWork() {


        String testURLString = Aware.getSetting(workerContext,Provider.DB_TBL_TESTS);
        String[] arrayLinks = testURLString.split(",");

        int inserted = 0;

        if (arrayLinks.length > 0) {
            for (String link : arrayLinks) {
                String testJson = getHtml(link);
                ContentValues[] data = retrieveDataFromHtml(testJson);
                inserted += bulkInsert(data);
            }
        } else {
            String testJson = getHtml(testURLString);
            if (testJson != null) {
                retrieveDataFromHtml(testJson);
                ContentValues[] data = retrieveDataFromHtml(testJson);
                inserted += bulkInsert(data);
            }

        }

        Log.d("WORKERS", "Tests worker termino :" + System.currentTimeMillis());


        if (inserted > 0) {
            NotificationUtils.getInstance().notifyNewTestsAvailable(inserted, workerContext);
            return Result.success();
        } else {
            return Result.retry();
        }


    }

    private ContentValues[] retrieveDataFromHtml(String link) {

        ContentValues[] testsValues = null;
        try {
            testsValues = JsonParserTests.getInstance().parse(link, workerContext);
        } catch (JSONException e) {
            ErrorHandler.getInstance().displayError(workerContext, e.getMessage());
        }

        return testsValues;

    }

    private int bulkInsert(ContentValues[] data) {
        int result = 0;
        if (data != null) {
            result = workerContext.getContentResolver().bulkInsert(
                    Provider.Cognimobile_Data.CONTENT_URI_TESTS,
                    data
            );
        }

        return result;
    }

    private String getHtml(@NonNull String urlString){
        String html = null;

        URL htmlUrlSource;

        Uri testQueryUri = Uri.parse(urlString).buildUpon()
                .build();
        try{
            htmlUrlSource =  new URL(testQueryUri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
            htmlUrlSource = null;
        }



        try {
            HttpsURLConnection connection = (HttpsURLConnection) htmlUrlSource.openConnection();

            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8), 8);
            StringBuilder str = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                str.append(line);
            }
            in.close();
            html = str.toString();
        } catch (IOException e) {
            ErrorHandler.getInstance().displayError(workerContext, e.getMessage());
        }

        return html;
    }
}
