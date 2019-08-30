package ugr.gbv.myapplication.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

import ugr.gbv.myapplication.R;
import ugr.gbv.myapplication.interfaces.LoadContent;

public class TextTask extends Fragment {

    private Context context;
    private Dialog builder;
    private LoadContent callBack;
    private ImageView selected;


    public static final int MEMORY = 5;
    public static final int ATENTION_NUMBERS = 6;
    public static final int ATENTION_LETTERS = 7;
    public static final int ATENTION_SUBSTRACTION = 8;
    public static final int LANGUAGE = 9;
    public static final int FLUENCY = 10;
    public static final int ABSTRACTION = 11;
    public static final int RECALL = 12;
    public static final int ORIENTATION = 13;

    private static int MAX_SPEECH = 500;

    private TextToSpeech tts;
    private int delayTts = 1000;
    private int delayTask = 5000;
    private boolean initialized = false;
    private boolean providedTask = false;
    private String[] array;
    private int index = 0;
    private View mainView;
    private int taskType;
    private RelativeLayout layout;
    private TextView countdownText;
    private TextView addicionalTaskText;
    private EditText addicionalTaskInput;
    private Button submitAnswerButton;



    public TextTask(int taskType,LoadContent callBack){
        this.callBack = callBack;
        this.taskType = taskType;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.text_task, container, false);

        context = getContext();


        layout = mainView.findViewById(R.id.textSpace);
        addicionalTaskText = mainView.findViewById(R.id.additional_task_text);
        addicionalTaskInput = mainView.findViewById(R.id.additional_task_input);
        submitAnswerButton = mainView.findViewById(R.id.submit_button);


        tts=new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){

                    int result=tts.setLanguage(Locale.UK);

                    if(result!=TextToSpeech.LANG_MISSING_DATA &&
                            result!=TextToSpeech.LANG_NOT_SUPPORTED){

                        initialized = true;

                    }

                }
            }
        });

        countdownText = mainView.findViewById(R.id.countDownText);



        buildDialog();


        Button nextButton = mainView.findViewById(R.id.nextTaskButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (providedTask) {
                    if(tts.isSpeaking()){
                        tts.stop();
                    }
                    callBack.loadContent();
                }
                else{

                    new CountDownTimer(delayTask, 1000) {


                        public void onTick(long millisUntilFinished) {

                            String display = Long.toString(millisUntilFinished / 1000, 10);
                            countdownText.setText(display);
                        }

                        public void onFinish() {
                            countdownText.setVisibility(View.GONE);
                            switch (taskType) {
                                case MEMORY:
                                    memorization("FACE,VELVET,CHURCH,DAISY,RED");
                                    break;
                                case ATENTION_NUMBERS:
                                    repeat("2,1,8,5,4");
                                    repeatBackwards("7,4,2");
                                    break;
                                case ATENTION_LETTERS:
                                    tapLetter("A", "F,B,A,C,M,N,A,A,J,K,L,B,A,F,A,K,D,E,A,A,A,J,A,M,O,F,A,A,B");
                                    break;
                                case ATENTION_SUBSTRACTION:
                                    serialSubstraction(100, 7, 5);
                                    break;
                                case LANGUAGE:
                                    repeatPhrase("I only know that John is the one to help today.");
                                    repeatPhrase("The cat always hid under the couch when dogs were in the room.");
                                    break;
                                case FLUENCY:
                                    fluencyWithWords("F", 11);
                                    break;
                                case ABSTRACTION:
                                    similarity("train-bicycle,watch-ruler", "transport,speed");
                                    similarity("watch-ruler", "measurement,numbers");
                                    break;
                                case RECALL:
                                    recall("FACE,VELVET,CHURCH,DAISY,RED");
                                    break;
                                case ORIENTATION:
                                    orientation();
                                    break;
                                default:
                                    throw new RuntimeException("INVALID TASKTYPE");
                            }

                        }
                    }.start();
                    providedTask = true;
                }
            }
        });

        FloatingActionButton helpButton = mainView.findViewById(R.id.helpButton);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(builder != null){
                    builder.show();
                }
            }
        });


        return mainView;
    }

    private void orientation() {
        showUserInput();
        showUserAdditionalTask();
    }

    private void recall(String words) {
        showUserInput();
        showUserAdditionalTask();
    }

    private void similarity(String words, String acceptedAnswer) {
        showUserInput();
        showUserAdditionalTask();
    }

    private void fluencyWithWords(String letter, int numberOfWords) {
        showUserInput();
        showUserAdditionalTask();
    }

    private void repeatPhrase(String statement) {
        speakPhrase(statement);
        showUserInput();
        showUserAdditionalTask();
    }

    private void serialSubstraction(int startingNumber, int substration, int times) {
        showUserInput();
        addicionalTaskText.setText("Introduce the number of the substraction: " + startingNumber + " - " + substration);
        addicionalTaskInput.setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    private void tapLetter(final String target, String words) {
        array = words.split(",");
        index = 0;
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index > 1) {
                    if(target.equals(array[index - 1]))
                        Toast.makeText(context, "TAP ON " + array[index - 1] + " = " + target, Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(context, "TAP ON " + array[index - 1] + " != " + target, Toast.LENGTH_LONG).show();
                }
            }
        });

        enumeration();
    }

    private void repeatBackwards(String numbers) {
        array = numbers.split(",");
        Collections.reverse(Arrays.asList(array));
        index = 0;
        enumeration();
        showUserInput();
        showUserAdditionalTask();
    }

    private void repeat(String numbers) {
        array = numbers.split(",");
        index = 0;
        enumeration();
        showUserInput();
        showUserAdditionalTask();
    }

    private void showUserAdditionalTask() {
        switch (taskType){
            case ATENTION_NUMBERS:
                addicionalTaskText.setText("Introduce the secuence");
                addicionalTaskInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case ATENTION_SUBSTRACTION:

                break;
            case LANGUAGE:
                addicionalTaskText.setText("Repeat the sentence");
                break;
            case FLUENCY:
                addicionalTaskText.setText("Introduce words starting with: F");
                break;
            case ABSTRACTION:
                addicionalTaskText.setText("Introduce the similarity of:");
                break;
            case RECALL:
                addicionalTaskText.setText("Introduce the secuence of the words that you remind:");
                break;
            case ORIENTATION:
                break;
            default:
                throw new RuntimeException("INVALID TASKTYPE");
        }
    }

    private void showUserInput() {
        addicionalTaskText.setVisibility(View.VISIBLE);
        addicionalTaskInput.setVisibility(View.VISIBLE);
        submitAnswerButton.setVisibility(View.VISIBLE);
    }

    private void memorization(String words) {
        array = words.split(",");
        index = 0;
        enumeration();
    }





    private void enumeration() {

        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {

            }

            @Override
            // this method will always called from a background thread.
            public void onDone(String utteranceId) {
                index++;
                tts.playSilentUtterance(delayTts, TextToSpeech.QUEUE_ADD, null);
                if(index < array.length) {
                    tts.speak(array[index], TextToSpeech.QUEUE_ADD, null, Integer.toString(index));
                }
                else if(index == array.length){
                    tts.speak("END", TextToSpeech.QUEUE_ADD, null,null);
                }

            }

            @Override
            public void onError(String utteranceId) {

            }
        });

        tts.speak(array[index],TextToSpeech.QUEUE_FLUSH,null, Integer.toString(index));

    }

    private void speakPhrase(String phrase){
        tts.speak(phrase,TextToSpeech.QUEUE_FLUSH,null, Integer.toString(index));
    }



    private void buildDialog(){
        builder = new Dialog(context);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = builder.getWindow();
        if(window != null) {
            window.setBackgroundDrawable(
                    new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //nothing;
            }
        });


        TextView content = new TextView(context);
        content.setText(getResources().getText(R.string.app_name));
        content.setBackgroundColor(getResources().getColor(R.color.white,context.getTheme()));
        builder.addContentView(content, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        );


    }
}
