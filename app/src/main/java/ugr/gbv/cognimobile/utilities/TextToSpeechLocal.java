package ugr.gbv.cognimobile.utilities;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.util.Locale;

import ugr.gbv.cognimobile.interfaces.TTSHandler;
/**
 * Class to manage the Text-to-Speech functionality
 */
public class TextToSpeechLocal {

    private static TextToSpeech textToSpeech;
    private static int currentStatus = TextToSpeech.ERROR;
    private static TTSHandler callback;
    private static boolean shuttedDown;
    private static final int delayTts = 1000;
    private static int index;
    public static final int STT_CODE = 2;
    private static Locale language;


    /**
     * One of the methods to instantiate the class
     *
     * @param context   from the parent activity
     * @param handler   callback to interact with the parent activity
     * @param pLanguage language that the Text-to-Speech functionality should speak
     */
    public static void instantiate(Context context, TTSHandler handler, Locale pLanguage) {
        callback = handler;
        language = pLanguage;
        instantiate(context);
    }

    /**
     * One of the methods to instantiate the class
     *
     * @param context from the parent activity
     */
    public static void instantiate(Context context) {
        if (textToSpeech == null) {
            index = 0;
            shuttedDown = false;
            initializeTts(context);
        } else if (shuttedDown) {
            initializeTts(context);
        }

    }

    /**
     * One of the methods to instantiate the Text-to-Speech functionality.
     *
     * @param context from the parent activity
     */
    private static synchronized void initializeTts(Context context) {
        textToSpeech = new TextToSpeech(context, status -> {
            currentStatus = status;
            if (status == TextToSpeech.SUCCESS) {

                int result = textToSpeech.setLanguage(language);

                if (result != TextToSpeech.LANG_MISSING_DATA &&
                        result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    shuttedDown = false;
                    if (callback != null)
                        callback.startTTS();
                }
                else if(result == TextToSpeech.LANG_MISSING_DATA){
                    ErrorHandler.displayError("The TextToSpeech component could not load the test language: "
                            + language.getLanguage() + " Please download it in the mobile settings.");
                }
                else if(result == TextToSpeech.LANG_NOT_SUPPORTED){
                    ErrorHandler.displayError("The TextToSpeech component could not load the test language: "
                            + language.getLanguage() + " It is not supported.");
                }
                else if(result == TextToSpeech.ERROR_NETWORK){
                    ErrorHandler.displayError("The TextToSpeech component could not load the test language: due to a network issue.");
                }

                if (callback != null) {
                    callback.TTSisInitialized();
                }

            } else {
                ErrorHandler.displayError("The TextToSpeech component could not be iniciated.");
            }
        });
    }

    /**
     * The Text-to-Speech functionality reads the phrase.
     *
     * @param phrase to be spoken.
     */
    public static void readOutLoud(String phrase) {
        textToSpeech.setSpeechRate(0.7f);
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {

            }

            @Override
            // this method will always called from a background thread.
            public void onDone(String utteranceId) {
                callback.TTSEnded();
            }


            @Override
            public void onError(String utteranceId) {

            }
        });
        // utteranceId MUST NOT BE null, otherwise callback is not called.(line 79)
        textToSpeech.speak(phrase, TextToSpeech.QUEUE_ADD, null, "onePhrase");
    }

    /**
     * The Text-to-Speech functionality reads an array.
     *
     * @param array to be spoken.
     */
    public static void enumerate(final String[] array) {
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {

            }

            @Override
            // this method will always called from a background thread.
            public void onDone(String utteranceId) {
                if(array != null && array.length > 0){
                    index++;
                    textToSpeech.playSilentUtterance(delayTts,
                            TextToSpeech.QUEUE_ADD,
                            null);
                    if (index < array.length) {
                        callback.setIndex(index);
                        textToSpeech.speak(array[index],
                                TextToSpeech.QUEUE_ADD,
                                null,
                                Integer.toString(index));
                        callback.registerTimeStamp();
                    } else {
                        index = 0;
                        callback.TTSEnded();
                    }
                }
                else {
                    index = 0;
                    stop();
                    callback.TTSEnded();
                }
            }

            @Override
            public void onError(String utteranceId) {
                stop();
                callback.TTSEnded();
            }
        });
        if(array != null && array.length > 0) {
            textToSpeech.speak(array[index],
                    TextToSpeech.QUEUE_ADD,
                    null,
                    Integer.toString(index));
        }
        else {
            index = 0;
            stop();
            callback.TTSEnded();
        }
    }

    /**
     * Stops the Text-to-Speech functionality.
     */
    public static void stop() {
        if (textToSpeech != null) {
            if (textToSpeech.isSpeaking()) {
                textToSpeech.stop();
                clearBuffer();
            }
        }
    }

    /**
     * Clears the Text-to-Speech functionality.
     */
    public static void clear() {
        if (textToSpeech != null) {
            clearBuffer();
            shuttedDown = true;
            textToSpeech.shutdown();
        }
    }

    /**
     * Clears the buffer of Text-to-Speech functionality.
     */
    private static void clearBuffer() {
        if (textToSpeech != null) {
            textToSpeech.playSilentUtterance(1, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    /**
     * Checks if the Text-to-Speech functionality is initialized.
     *
     * @return if the Text-to-Speech functionality is initialized.
     */
    public static synchronized boolean isInitialized() {
        return textToSpeech != null && currentStatus != TextToSpeech.ERROR;
    }


}

