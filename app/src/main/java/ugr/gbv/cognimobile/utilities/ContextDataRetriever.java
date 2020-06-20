package ugr.gbv.cognimobile.utilities;

import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Class to collect contextual information about the user.
 */
public class ContextDataRetriever {

    //EVENTS
    public static final String GenericTimeHelp = "GenericTimeHelp";
    public static final String GenericTimeBeforeTask = "GenericTimeBeforeTask";
    public static final String GenericTimeStartTask = "GenericTimeStartTask";
    public static final String GenericTimeEndTask = "GenericTimeEndTask";
    public static final String GenericTimeNextTask = "GenericTimeNextTask";
    public static final String GenericSkippedTask = "GenericSkippedTask";

    public static final String SpecificATMTimeBetweenClicks = "SpecificATMTimeBetweenClicks";
    public static final String SpecificATMAlreadyClickedButton = "SpecificATMAlreadyClickedButton";
    public static final String SpecificATMPoints = "SpecificATMPoints";
    public static final String SpecificATMDistanceBetweenCircles = "SpecificATMDistanceBetweenCircles";

    public static final String SpecificVSCubeStartDraw = "SpecificVSCubeStartDraw";
    public static final String SpecificVSCubeEndDraw = "SpecificVSCubeEndDraw";
    public static final String SpecificVSCubePoints = "SpecificVSCubePoints";

    public static final String SpecificVSClockStartDraw = "SpecificVSClockStartDraw";
    public static final String SpecificVSClockEndDraw = "SpecificVSClockEndDraw";
    public static final String SpecificVSClockPoints = "SpecificVSClockPoints";

    public static final String SpecificNamingCharacterChange = "SpecificNamingCharacterChange";
    public static final String SpecificNamingStartWriting = "SpecificNamingStartWriting";
    public static final String SpecificNamingSubmitAnswer = "SpecificNamingSubmitAnswer";

    public static final String SpecificMemoryCharacterChange = "SpecificMemoryCharacterChange";
    public static final String SpecificMemoryScrollingList = "SpecificMemoryScrollingList";
    public static final String SpecificMemorySettlingList = "SpecificMemorySettlingList";
    public static final String SpecificMemoryStartWriting = "SpecificMemoryStartWriting";
    public static final String SpecificMemorySubmitAnswer = "SpecificMemorySubmitAnswer";

    public static final String SpecificAttentionNumbersItemPosition = "SpecificAttentionNumbersItemPosition";
    public static final String SpecificAttentionNumbersStartWriting = "SpecificAttentionNumbersStartWriting";
    public static final String SpecificAttentionNumbersItemPositionBackwards = "SpecificAttentionNumbersItemPositionBackwards";
    public static final String SpecificAttentionNumbersSubmitAnswer = "SpecificAttentionNumbersSubmitAnswer";

    public static final String SpecificAttentionLettersTimeToAnswer = "SpecificAttentionLettersTimeToAnswer";
    public static final String SpecificAttentionLettersSoundTimes = "SpecificAttentionLettersSoundTimes";

    public static final String SpecificSubtractionCharacterChange = "SpecificSubtractionCharacterChange";
    public static final String SpecificSubtractionStartWriting = "SpecificSubtractionStartWriting";
    public static final String SpecificSubtractionSubmitAnswer = "SpecificSubtractionSubmitAnswer";

    public static final String SpecificSRCharacterChange = "SpecificSRCharacterChange";
    public static final String SpecificSRStartWriting = "SpecificSRStartWriting";
    public static final String SpecificSRSubmitAnswer = "SpecificSRSubmitAnswer";


    public static final String SpecificFluencyCharacterChange = "SpecificFluencyCharacterChange";
    public static final String SpecificFluencyScrollingList = "SpecificFluencyScrollingList";
    public static final String SpecificFluencySettlingList = "SpecificFluencySettlingList";
    public static final String SpecificFluencyStartWriting = "SpecificFluencyStartWriting";
    public static final String SpecificFluencySubmitAnswer = "SpecificFluencySubmitAnswer";

    public static final String SpecificAbstractionCharacterChange = "SpecificAbstractionCharacterChange";
    public static final String SpecificAbstractionStartWriting = "SpecificAbstractionStartWriting";
    public static final String SpecificAbstractionSubmitAnswer = "SpecificAbstractionSubmitAnswer";

    public static final String SpecificRecallCharacterChange = "SpecificRecallCharacterChange";
    public static final String SpecificRecallScrollingList = "SpecificRecallScrollingList";
    public static final String SpecificRecallSettlingList = "SpecificRecallSettlingList";
    public static final String SpecificRecallStartWriting = "SpecificRecallStartWriting";
    public static final String SpecificRecallNumbersOfWords = "SpecificRecallNumbersOfWords";
    public static final String SpecificRecallNumbersOfCorrectWords = "SpecificRecallNumbersOfCorrectWords";
    public static final String SpecificRecallSubmitAnswer = "SpecificRecallSubmitAnswer";

    public static final String SpecificOrientationCharacterChange = "SpecificOrientationCharacterChange";
    public static final String SpecificOrientationStartWriting = "SpecificOrientationStartWriting";
    public static final String SpecificOrientationSubmitAnswer = "SpecificOrientationSubmitAnswer";

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
}
