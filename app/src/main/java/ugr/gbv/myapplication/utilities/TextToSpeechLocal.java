package ugr.gbv.myapplication.utilities;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.io.Serializable;
import java.util.Locale;

public class TextToSpeechLocal implements Serializable {

    private static volatile TextToSpeechLocal instanced;
    private static volatile TextToSpeech textToSpeech;
    private int delayTts = 1000;
    private int index;

    private TextToSpeechLocal(){
        if (instanced != null){
            throw new RuntimeException("Use .getInstance(Context context) to instanciate TextToSpeechLocal");
        }
    }

    public static TextToSpeechLocal getInstance(Context context) {
        if (instanced == null) {
            synchronized (TextToSpeechLocal.class) {
                if (instanced == null) {
                    instanced = new TextToSpeechLocal();
                    textToSpeech=new TextToSpeech(context, new android.speech.tts.TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if(status == android.speech.tts.TextToSpeech.SUCCESS){

                                int result=textToSpeech.setLanguage(Locale.getDefault());

                                if(result == android.speech.tts.TextToSpeech.LANG_MISSING_DATA ||
                                        result== android.speech.tts.TextToSpeech.LANG_NOT_SUPPORTED){
                                    throw new RuntimeException("Wrong TextToSpeech initialization, result = " + result);

                                }

                            }
                        }
                    });
                }
            }
        }

        return instanced;
    }

    public void readOutLoud(String phrase){
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {

            }

            @Override
            public void onDone(String utteranceId) {

            }

            @Override
            public void onError(String utteranceId) {

            }
        });

        textToSpeech.speak(phrase,TextToSpeech.QUEUE_FLUSH,null, null);

    }

    public void enumerate(final String[] array){
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                index = 0;
            }

            @Override
            // this method will always called from a background thread.
            public void onDone(String utteranceId) {
                index++;
                textToSpeech.playSilentUtterance(delayTts, TextToSpeech.QUEUE_ADD, null);
                if(index < array.length) {
                    textToSpeech.speak(array[index], TextToSpeech.QUEUE_ADD, null, Integer.toString(index));
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
        }
    }
    public void clear(){
        textToSpeech.shutdown();
    }


}

