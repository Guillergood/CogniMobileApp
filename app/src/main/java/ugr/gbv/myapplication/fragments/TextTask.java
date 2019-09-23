package ugr.gbv.myapplication.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
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
    private boolean providedTask = false;
    private String[] array;
    private ArrayList<String> answer;
    private int index = 0;
    private int lastIndex = 0;
    private View mainView;
    private int taskType;
    private RelativeLayout layout;
    private TextView countdownText;
    private TextView addicionalTaskText;
    private EditText addicionalTaskInput;
    private Button submitAnswerButton;
    private ConstraintLayout mainLayout;
    private TextView bannerText;

    private ArrayList<EditText> variousInputs;

    private boolean alredyPressLastLetter;



    public TextTask(int taskType,LoadContent callBack){
        this.callBack = callBack;
        this.taskType = taskType;
        answer = new ArrayList<>();
        alredyPressLastLetter = false;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.text_task, container, false);

        context = getContext();


        layout = mainView.findViewById(R.id.textSpace);
        mainLayout = mainView.findViewById(R.id.textTaskLayout);
        addicionalTaskText = mainView.findViewById(R.id.additional_task_text);
        addicionalTaskInput = mainView.findViewById(R.id.additional_task_input);
        submitAnswerButton = mainView.findViewById(R.id.submit_button);
        bannerText = mainView.findViewById(R.id.banner_text);


        tts=new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){

                    int result=tts.setLanguage(Locale.UK);

                    if(result!=TextToSpeech.LANG_MISSING_DATA &&
                            result!=TextToSpeech.LANG_NOT_SUPPORTED){


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
                                    bannerText.setText("This is a memory test. I am going to read a list of words that you will have to remember now and later on. Listen carefully. When I am through, tell me as many words as you can remember. It doesn’t matter in what order you say them. I am going to read the same list for a second time. Try to remember and tell me as many words as you can, including words you said the first time. I will ask you to recall those words again at the end of the test.");
                                    memorization("FACE,VELVET,CHURCH,DAISY,RED");
                                    break;
                                case ATENTION_NUMBERS:
                                    bannerText.setText("I am going to say some numbers and when I am through, type them to me exactly as I said them");
                                    repeat("2,1,8,5,4");
                                    break;
                                case ATENTION_LETTERS:
                                    
                                    tapLetter("A", "F,B,A,C,M,N,A,A,J,K,L,B,A,F,A,K,D,E,A,A,A,J,A,M,O,F,A,A,B");
                                    break;
                                case ATENTION_SUBSTRACTION:
                                    serialSubstraction(100, 7, 5);
                                    break;
                                case LANGUAGE:
                                    //I only know that John is the one to help today.
                                    repeatPhrase("The cat always hid under the couch when dogs were in the room.");
                                    break;
                                case FLUENCY:
                                    bannerText.setText("Tell me as many words as you can think of that begin with a certain letter of the alphabet that I will tell you in a moment. You can say any kind of word you want, except for proper nouns (like Bob or Boston), numbers, or words that begin with the same sound but have a different suffix, for example, love, lover, loving. I will tell you to stop after one minute");
                                    fluencyWithWords("F", 11);
                                    break;
                                case ABSTRACTION:
                                    similarity();
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

    private void similarity() {
        showUserInput();
        showUserAdditionalTask();
        array = new String[4];
        array[0] = "train-bicycle";
        array[1] = "transport,speed";
        array[2] = "watch-ruler";
        array[3] = "measurement,numbers";
        addicionalTaskText.setText("Introduce the similarity of: " + array[index]);
        index+=2;
        submitAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index == 5){
                    submitAnswerButton.setVisibility(View.GONE);
                    addicionalTaskText.setText("You have finalizaed the task");
                }
                else{
                    answer.add(addicionalTaskInput.getText().toString());
                    addicionalTaskText.setText("Introduce the similarity of: " + array[index]);
                    index+=2;
                }
            }
        });

    }

    private void fluencyWithWords(String letter, int numberOfWords) {
        addicionalTaskInput.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        showUserInput();
        showUserAdditionalTask();

    }

    private void repeatPhrase(String statement) {
        speakPhrase(statement);
        showUserInput();
        showUserAdditionalTask();
    }

    private void serialSubstraction(int startingNumber, final int substration, final int times) {
        index = 0;
        showUserInput();
        addicionalTaskText.setText("Introduce the number of the substraction: " + startingNumber + " - " + substration);
        addicionalTaskInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        submitAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index == times){
                    submitAnswerButton.setVisibility(View.GONE);
                    addicionalTaskText.setText("You have finalizaed the task");
                }
                else{
                    answer.add(addicionalTaskInput.getText().toString());
                    addicionalTaskText.setText("Introduce the number of the substraction: " + addicionalTaskInput.getText().toString() + " - " + substration);
                    index++;
                }
            }
        });
    }

    private void tapLetter(final String target, String words) {
        array = words.split(",");
        index = 0;
        lastIndex = 0;
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if(index > 0 && lastIndex != index) {
                    answer.add(array[index - 1]);
                    Toast.makeText(context, "TAP ON " + array[index - 1], Toast.LENGTH_LONG).show();
                    lastIndex = index;
                 }

            }
        });

        enumeration();
    }

    private void repeatBackwards(String numbers) {
        array = numbers.split(",");
        index = 0;
        clearLastEnumeration();
        placeFirstInput();
        setVariousInputs();
        enumeration();
        showUserInput();
        showUserAdditionalTask();
        addicionalTaskText.setText("Introduce the secuence backwards");
        submitAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (EditText editText:variousInputs)
                    answer.add(editText.getText().toString());
                Toast.makeText(context, "DONE", Toast.LENGTH_LONG).show();
                submitAnswerButton.setVisibility(View.GONE);
            }
        });
    }

    private void clearLastEnumeration() {
        for(int i = 1; i < variousInputs.size(); ++i){
            mainLayout.removeView(variousInputs.get(i));
        }
        variousInputs.clear();
    }

    private void repeat(String numbers) {
        array = numbers.split(",");
        index = 0;
        placeFirstInput();
        changeInputFilterAndType();
        setVariousInputs();
        enumeration();
        showUserInput();
        showUserAdditionalTask();
        addicionalTaskInput.requestFocus();
        submitAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (EditText editText:variousInputs) {
                    answer.add(editText.getText().toString());
                    editText.getText().clear();
                }
                addicionalTaskInput.requestFocus();
                bannerText.setText("Now I am going to say some more numbers, but when I am through you must repeat them to me in the backwards order.");
                repeatBackwards("7,4,2");
            }
        });

    }

    private void setVariousInputs() {
        int[] numbersId = new int[array.length];
        numbersId[0] = addicionalTaskInput.getId();
        variousInputs = new ArrayList<>();
        variousInputs.add(addicionalTaskInput);

        ConstraintSet set = new ConstraintSet();
        set.clone(mainLayout);

        int dimens = getResources().getDimensionPixelSize(R.dimen.input_dimen);
        int portion = mainLayout.getWidth()/array.length;
        int positionX = portion-mainLayout.getWidth()/2;


        for(int i = 1; i < array.length; ++i){

            EditText editText = new EditText(context);
            editText.setId(View.generateViewId());
            editText.setTag(Integer.toString(i));
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setKeyListener(DigitsKeyListener.getInstance("123456789"));
            InputFilter[] inputArray = new InputFilter[1];
            inputArray[0] = new InputFilter.LengthFilter(1);
            editText.setFilters(inputArray);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(count > 0) {
                        if (variousInputs.size() > index+1) {
                            variousInputs.get(index+1).requestFocus();
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                        index = Integer.parseInt(v.getTag().toString());
                    }
                }
            });


            set.connect(editText.getId(), ConstraintSet.LEFT, numbersId[i - 1], ConstraintSet.RIGHT, 8);
            set.connect(editText.getId(), ConstraintSet.TOP, numbersId[i - 1], ConstraintSet.TOP, 0);
            set.constrainHeight(editText.getId(), dimens);
            set.constrainWidth(editText.getId(), dimens);
            set.setTranslationX(editText.getId(),positionX);
            mainLayout.addView(editText);
            set.applyTo(mainLayout);

            numbersId[i] = editText.getId();
            variousInputs.add(editText);
        }
    }

    private void placeFirstInput(){
        ConstraintSet set = new ConstraintSet();
        set.clone(mainLayout);

        int dimens = getResources().getDimensionPixelSize(R.dimen.input_dimen);
        set.constrainHeight(addicionalTaskInput.getId(), dimens);
        set.constrainWidth(addicionalTaskInput.getId(), dimens);

        int portion = mainLayout.getWidth()/array.length;
        int positionX = portion-mainLayout.getWidth()/2;
        set.setTranslationX(addicionalTaskInput.getId(),positionX);
        set.applyTo(mainLayout);

        addicionalTaskInput.setScrollContainer(true);
        addicionalTaskInput.setTag("0");

        addicionalTaskInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count > 0) {
                    index=1;
                    if (variousInputs.size() > index) {
                        variousInputs.get(index).requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        addicionalTaskInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    index = Integer.parseInt(v.getTag().toString());
                }
            }
        });

    }

    private void changeInputFilterAndType(){
        addicionalTaskInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        addicionalTaskInput.setKeyListener(DigitsKeyListener.getInstance("123456789"));
        InputFilter[] inputArray = new InputFilter[1];
        inputArray[0] = new InputFilter.LengthFilter(1);
        addicionalTaskInput.setFilters(inputArray);
    }

    private void showUserAdditionalTask() {
        switch (taskType){
            case ATENTION_NUMBERS:
                addicionalTaskText.setText("Introduce the secuence");
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
