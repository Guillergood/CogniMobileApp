package ugr.gbv.myapplication.utilities;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.io.Serializable;
import java.util.Locale;

import ugr.gbv.myapplication.interfaces.TTSHandler;

public class TextToSpeechLocal implements Serializable {

    private static volatile TextToSpeechLocal instanced;
    private static volatile TextToSpeech textToSpeech;
    private static volatile TTSHandler callback;
    private int delayTts = 1000;
    private static int index;

    private TextToSpeechLocal(){
        if (instanced != null){
            throw new RuntimeException("Use .getInstance(Context context) to instanciate TextToSpeechLocal");
        }
    }

    public static TextToSpeechLocal getInstance(Context context, TTSHandler handler) {
        callback = handler;
        return getInstance(context);
    }

    public static TextToSpeechLocal getInstance(Context context) {
        if (instanced == null) {
            synchronized (TextToSpeechLocal.class) {
                if (instanced == null) {
                    instanced = new TextToSpeechLocal();
                    index = 0;
                    textToSpeech=new TextToSpeech(context, status -> {
                        if(status == TextToSpeech.SUCCESS){

                            int result=textToSpeech.setLanguage(Locale.getDefault());

                            if(result == TextToSpeech.LANG_MISSING_DATA ||
                                    result== TextToSpeech.LANG_NOT_SUPPORTED){
                                throw new RuntimeException("Wrong TextToSpeech initialization, result = " + result);

                            }
                            callback.startTTS();
                        }
                    });
                }
            }
        }

        return instanced;
    }

    public void readOutLoud(String phrase){
        textToSpeech.setSpeechRate(0.7f);
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {

            }

            @Override
            // this method will always called from a background thread.
            public void onDone(String utteranceId) {
                if(utteranceId.equals("unique"))
                    callback.TTSEnded();
            }


            @Override
            public void onError(String utteranceId) {

            }
        });

        textToSpeech.speak(phrase,TextToSpeech.QUEUE_ADD,null, "unique");
    }

    public void enumerate(final String[] array){
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

    public void stop(){
        if(textToSpeech.isSpeaking()) {
            textToSpeech.stop();
            clearBuffer();
        }
    }
    public void clear(){
        textToSpeech.shutdown();
    }
    private void clearBuffer(){
        textToSpeech.playSilentUtterance(1, TextToSpeech.QUEUE_FLUSH, null);
    }





}

