package ugr.gbv.cognimobile.sync;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import ugr.gbv.cognimobile.database.ContentProvider;
import ugr.gbv.cognimobile.database.Provider;
import ugr.gbv.cognimobile.utilities.ErrorHandler;
import ugr.gbv.cognimobile.utilities.JsonParserTests;
import ugr.gbv.cognimobile.utilities.NotificationUtils;

/**
 * Class to retrieve the tests from the server
 */
public class TestsWorker extends Worker {

    private final Context workerContext;

    /**
     * Constructor
     *
     * @param context from the parent activity.
     * @param params  worker parameters
     */
    public TestsWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        workerContext = context;
    }

    /**
     * Overrides the {@link Worker#doWork()} method
     *
     * @return The {@link androidx.work.ListenableWorker.Result} of the computation; note that
     * dependent work will not execute if you use
     * {@link androidx.work.ListenableWorker.Result#failure()} or
     * {@link androidx.work.ListenableWorker.Result#failure(Data)}
     */
    @NonNull
    @Override
    public Result doWork() {
        //TODO Reformular
        ContentProvider.getInstance().getTests(workerContext);
        return Result.success();
    }

    /**
     * Retrieves the data from the link's html
     *
     * @param link to retrieve the data.
     * @return Tests to be inserted in the database.
     */
    private ContentValues[] retrieveDataFromHtml(String link) {

        ContentValues[] testsValues = null;
        try {
            testsValues = JsonParserTests.getInstance().parse(link, workerContext);
        } catch (JSONException e) {
            ErrorHandler.displayError(e.getMessage());
        }

        return testsValues;

    }

    /**
     * Bulk insertion of the tests.
     *
     * @param data to be inserted
     * @return rows affected.
     */
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

    /**
     * Gets the html from the URL
     *
     * @param urlString to retrieve the URL html.
     * @return html as a string to be parsed.
     */
    private String getHtml(@NonNull String urlString) {
        String html = null;

        URL htmlUrlSource;

        Uri testQueryUri = Uri.parse(urlString).buildUpon()
                .build();
        try {
            htmlUrlSource = new URL(testQueryUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            htmlUrlSource = null;
        }

        if (urlString.contains("https")) {
            html = getFromHTTPS(Objects.requireNonNull(htmlUrlSource));
        } else if (urlString.contains("http")) {
            html = getFromHTTP(Objects.requireNonNull(htmlUrlSource));
        }

        return html;
    }

    /**
     * Gets the html from a HTTPS URL
     *
     * @param htmlUrlSource to retrieve the HTTPS URL html.
     * @return html as a string to be parsed.
     */
    private String getFromHTTPS(URL htmlUrlSource) {
        String html = null;
        try {
            HttpsURLConnection connection = (HttpsURLConnection) htmlUrlSource.openConnection();

            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8), 8);
            StringBuilder str = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                str.append(line.replaceAll("&quot;", "\""));
            }
            in.close();
            html = str.toString();
        } catch (IOException e) {
            ErrorHandler.displayError(e.getMessage());
        }
        return html;
    }

    /**
     * Gets the html from a HTTP URL
     *
     * @param htmlUrlSource to retrieve the HTTP URL html.
     * @return html as a string to be parsed.
     */
    private String getFromHTTP(URL htmlUrlSource) {
        String html = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) htmlUrlSource.openConnection();

            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8), 8);
            StringBuilder str = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                str.append(line.replaceAll("&quot;", "\""));
            }
            in.close();
            html = str.toString();
        } catch (IOException e) {
            ErrorHandler.displayError(e.getMessage());
        }
        return html;
    }

    /**
     * Gets the tests urls.
     *
     * @param urlString to retrieve the tests links.
     * @return urls where the tests data are stored.
     */
    private String[] getTestsLinks(@NonNull String urlString) {

        ArrayList<String> urls = null;

        URL htmlUrlSource;

        Uri testQueryUri = Uri.parse(urlString).buildUpon()
                .build();
        try {
            htmlUrlSource = new URL(testQueryUri.toString());
        } catch (MalformedURLException e) {
            ErrorHandler.displayError(e.getMessage() + " " + urlString);
            htmlUrlSource = null;
        }


        if (urlString.contains("https")) {
            urls = getLinksFromHTTPS(Objects.requireNonNull(htmlUrlSource));
        } else if (urlString.contains("http")) {
            urls = getLinksFromHTTP(Objects.requireNonNull(htmlUrlSource));
        }


        return Objects.requireNonNull(urls).toArray(new String[0]);
    }

    /**
     * Gets the tests urls from a HTTP url.
     *
     * @param htmlUrlSource to retrieve the tests links.
     * @return urls where the tests data are stored.
     */
    private ArrayList<String> getLinksFromHTTP(URL htmlUrlSource) {
        ArrayList<String> urls = new ArrayList<>();
        try {
            HttpURLConnection connection = (HttpURLConnection) htmlUrlSource.openConnection();

            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8), 8);
            String line;
            while ((line = reader.readLine()) != null) {
                urls.add(line.replaceAll("<[^>]*>", "").trim());
            }
            in.close();
        } catch (IOException e) {
            ErrorHandler.displayError(e.getMessage());
        }
        return urls;
    }

    /**
     * Gets the tests urls from a HTTPS url.
     *
     * @param htmlUrlSource to retrieve the tests links.
     * @return urls where the tests data are stored.
     */
    private ArrayList<String> getLinksFromHTTPS(URL htmlUrlSource) {
        ArrayList<String> urls = new ArrayList<>();
        try {
            HttpsURLConnection connection = (HttpsURLConnection) htmlUrlSource.openConnection();

            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8), 8);
            String line;
            while ((line = reader.readLine()) != null) {
                urls.add(line.replaceAll("<[^>]*>", "").trim());
            }
            in.close();
        } catch (IOException e) {
            ErrorHandler.displayError(e.getMessage());
        }
        return urls;
    }
}
