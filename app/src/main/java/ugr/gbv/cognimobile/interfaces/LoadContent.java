package ugr.gbv.cognimobile.interfaces;

import ugr.gbv.cognimobile.utilities.JsonAnswerWrapper;

public interface LoadContent {
    void loadContent();
    void hideKeyboard();
    JsonAnswerWrapper getJsonAnswerWrapper();
}
