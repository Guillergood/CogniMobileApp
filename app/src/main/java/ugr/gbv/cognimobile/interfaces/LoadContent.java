package ugr.gbv.cognimobile.interfaces;

import android.app.Activity;

import java.util.ArrayList;
import java.util.Locale;

import ugr.gbv.cognimobile.utilities.JsonAnswerWrapper;

public interface LoadContent {
    void loadContent();

    void hideKeyboard(Activity activity);

    void showKeyboard(Activity activity);
    JsonAnswerWrapper getJsonAnswerWrapper();

    JsonAnswerWrapper getJsonContextEvents();

    JsonAnswerWrapper getJsonContextData();

    Locale getLanguage();

    int checkTypos(ArrayList<String> words);
}
