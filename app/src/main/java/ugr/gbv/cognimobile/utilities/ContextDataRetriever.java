package ugr.gbv.cognimobile.utilities;

import android.view.View;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Class to collect contextual information about the user.
 */
public class ContextDataRetriever {

    /**
     * Unwrap the data from an Integer Arraylist
     *
     * @param arrayList to unwrap
     * @return a string to put inside the json
     */
    public static String retrieveInformationFromIntegerArrayList(ArrayList<Integer> arrayList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < arrayList.size(); ++i) {
            stringBuilder.append(arrayList.get(i).toString());
            stringBuilder.append(",");
        }
        if (stringBuilder.length() > 0)
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    /**
     * Unwrap the data from a Long Arraylist
     *
     * @param arrayList to unwrap
     * @return a string to put inside the json
     */
    public static String retrieveInformationFromLongArrayList(ArrayList<Long> arrayList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < arrayList.size(); ++i) {
            stringBuilder.append(arrayList.get(i).toString());
            stringBuilder.append(",");
        }
        if (stringBuilder.length() > 0)
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    /**
     * Unwrap the data from a String Arraylist
     *
     * @param arrayList to unwrap
     * @return a string to put inside the json
     */
    public static String retrieveInformationFromStringArrayList(ArrayList<String> arrayList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < arrayList.size(); ++i) {
            stringBuilder.append(arrayList.get(i));
            stringBuilder.append(",");
        }
        if (stringBuilder.length() > 0)
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    /**
     * Unwrap the data from a Button Arraylist
     *
     * @param arrayList to unwrap
     * @return a string to put inside the json
     */
    public static String retrieveInformationFromButtonArrayList(ArrayList<Button> arrayList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i < arrayList.size(); ++i) {
            stringBuilder.append(ContextDataRetriever.getDistanceBetweenViews(arrayList.get(i - 1), arrayList.get(i)));
            stringBuilder.append(",");
        }
        if (stringBuilder.length() > 0)
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }


    /**
     * Retrieves the current time in milliseconds.
     *
     * @return the current time in milliseconds
     */
    public static long addTimeStamp() {
        return System.currentTimeMillis();
    }

    public static String getCurrentDateString(Locale language) {
        final Date date = new Date();
        final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS zzz";
        final SimpleDateFormat sdf = new SimpleDateFormat(ISO_FORMAT, language);
        final TimeZone utc = TimeZone.getTimeZone("UTC");
        sdf.setTimeZone(utc);
        return sdf.format(date);
    }

    /**
     * Calculates the distance in dp from two views.
     *
     * @param firstView  first view
     * @param secondView second view
     * @return the distance between them in dp.
     */
    private static int getDistanceBetweenViews(View firstView, View secondView) {
        int[] firstPosition = new int[2];
        int[] secondPosition = new int[2];

        firstView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        firstView.getLocationOnScreen(firstPosition);
        secondView.getLocationOnScreen(secondPosition);

        int b = firstView.getMeasuredHeight() + firstPosition[1];
        int t = secondPosition[1];
        return Math.abs(b - t);
    }

    public static String fixUrl(String url) {
        String newUrl = url;
        String concatenation = "backend";
        if(newUrl.contains("localhost")){
            newUrl = newUrl.replace("localhost","192.168.1.38");
        }
        if(!newUrl.contains(concatenation)){
            if(newUrl.endsWith("/")) {
                newUrl = newUrl + concatenation;
            }
            else{
                newUrl = newUrl + "/" + concatenation;
            }
        }
        return newUrl;
    }
}
