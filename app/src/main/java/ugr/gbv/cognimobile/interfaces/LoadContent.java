package ugr.gbv.cognimobile.interfaces;

import android.app.Activity;
import android.view.textservice.SentenceSuggestionsInfo;

import java.util.List;
import java.util.Locale;

import ugr.gbv.cognimobile.dto.TestAnswerDTO;
import ugr.gbv.cognimobile.dto.TestEventDTO;

/**
 * Interface that gives the activity to load content.
 */
public interface LoadContent {
    /**
     * This method allows to replace fragments from the actual activity and continue with
     * the next one from the fragments list. When the list is over, it tries to send the necessary
     * data to the server.
     */
    void loadContent();

    /**
     * This method allows to hide the keyboard.
     *
     * @param activity Actual activity to hide the keyboard from.
     */
    void hideKeyboard(Activity activity);

    /**
     * This method allows to display the keyboard.
     *
     * @param activity Actual activity to hide the keyboard from.
     */
    void showKeyboard(Activity activity);

    /**
     * Getter for the json that contains all the answers typed by the user who has done the test
     *
     * @return the json that contains all the answers
     */
    TestAnswerDTO getTestAnswerDTO();

    /**
     * Getter for the json that contains all the extra information about the user who has done the test
     *
     * @return the json that contains the extra information
     */
    TestEventDTO getTestEventDTO();

    /**
     * Getter for the language of the test.
     *
     * @return the language of the test
     */
    Locale getLanguage();

    /**
     * Method that checks if the user has submitted correct words.
     * Uses {@link ugr.gbv.cognimobile.activities.Test#onGetSentenceSuggestions(SentenceSuggestionsInfo[])
     * to check every word.
     * <p>
     * @param input Array of the user words.
     */
    int checkTypos(List<String> words);
}
