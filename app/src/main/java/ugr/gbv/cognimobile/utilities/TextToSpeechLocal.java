package ugr.gbv.cognimobile.utilities;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.util.Locale;

import ugr.gbv.cognimobile.interfaces.TTSHandler;

public class TextToSpeechLocal {

    private static TextToSpeech textToSpeech;
    private static int currentStatus = TextToSpeech.ERROR;
    private static TTSHandler callback;
    private static boolean shuttedDown;
    private static int delayTts = 1000;
    private static int index;
    public static final int STT_CODE = 2;
    private static Locale language;


    public static void getInstance(Context context, TTSHandler handler, Locale pLanguage) {
        callback = handler;
        language = pLanguage;
        getInstance(context);
    }

    public static void getInstance(Context context) {
        if (textToSpeech == null) {
            index = 0;
            shuttedDown = false;
            initializeTts(context);
        }
        else if(shuttedDown){
            initializeTts(context);
        }

    }

    private static synchronized void initializeTts(Context context){
        textToSpeech=new TextToSpeech(context, status -> {
            currentStatus = status;
            if(status == TextToSpeech.SUCCESS){

                int result = textToSpeech.setLanguage(language);

                if (result != TextToSpeech.LANG_MISSING_DATA &&
                        result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    shuttedDown = false;
                    if (callback != null)
                        callback.startTTS();
                }

                if (callback != null) {
                    callback.TTSisInitialized();
                }

            } else {
                initializeTts(context);
            }
        });
    }

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
        textToSpeech.speak(phrase,TextToSpeech.QUEUE_ADD,null, "onePhrase");
    }

    public static void enumerate(final String[] array) {
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {

            }

            @Override
            // this method will always called from a background thread.
            public void onDone(String utteranceId) {
                index++;
                textToSpeech.playSilentUtterance(delayTts, TextToSpeech.QUEUE_ADD, null);
                if(index < array.length) {
                    callback.setIndex(index);
                    textToSpeech.speak(array[index], TextToSpeech.QUEUE_ADD, null, Integer.toString(index));
                    callback.registerTimeStamp();
                }
                else{
                    index = 0;
                    callback.TTSEnded();
                }

            }

            @Override
            public void onError(String utteranceId) {

            }
        });

        textToSpeech.speak(array[index],TextToSpeech.QUEUE_ADD,null, Integer.toString(index));
    }

    public static void stop() {
        if(textToSpeech != null) {
            if (textToSpeech.isSpeaking()) {
                textToSpeech.stop();
                clearBuffer();
            }
        }
    }

    public static void clear() {
        if(textToSpeech != null) {
            clearBuffer();
            shuttedDown = true;
            textToSpeech.shutdown();
        }
    }

    private static void clearBuffer() {
        if(textToSpeech != null) {
            textToSpeech.playSilentUtterance(1, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public static synchronized boolean isInitialized(){
        return textToSpeech != null && currentStatus != TextToSpeech.ERROR;
    }





}

