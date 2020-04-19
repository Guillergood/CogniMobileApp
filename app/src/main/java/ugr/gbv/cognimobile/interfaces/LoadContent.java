package ugr.gbv.cognimobile.interfaces;

import java.util.ArrayList;
import java.util.Locale;

import ugr.gbv.cognimobile.utilities.JsonAnswerWrapper;

public interface LoadContent {
    void loadContent();
    void hideKeyboard();
    JsonAnswerWrapper getJsonAnswerWrapper();

    Locale getLanguage();

    int checkTypos(ArrayList<String> words);
}
