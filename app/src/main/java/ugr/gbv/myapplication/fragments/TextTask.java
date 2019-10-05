package ugr.gbv.myapplication.fragments;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.Gravity;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Locale;

import ugr.gbv.myapplication.R;
import ugr.gbv.myapplication.interfaces.LoadContent;
import ugr.gbv.myapplication.interfaces.TTSHandler;
import ugr.gbv.myapplication.utilities.TextToSpeechLocal;
import ugr.gbv.myapplication.utilities.WordListAdapter;

import static android.app.Activity.RESULT_OK;

public class TextTask extends Fragment implements TTSHandler {

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
    private Button sttButton;
    private RecyclerView recyclerView;
    private WordListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private Button nextButton;

    private ArrayList<EditText> variousInputs;
    private final int STT_CODE = 2;




    public TextTask(int taskType,LoadContent callBack){
        this.callBack = callBack;
        this.taskType = taskType;
        answer = new ArrayList<>();
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
        sttButton = mainView.findViewById(R.id.stt_button);
        recyclerView = mainView.findViewById(R.id.words_list);
        recyclerView.setNestedScrollingEnabled(false);


        // use a linear layout manager
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        adapter = new WordListAdapter();
        recyclerView.setAdapter(adapter);


        showUserAdditionalTask();



        countdownText = mainView.findViewById(R.id.countDownText);



        buildDialog();


        nextButton = mainView.findViewById(R.id.nextTaskButton);
        nextButton.setOnClickListener(view -> {

            if (providedTask) {
                TextToSpeechLocal.getInstance(context).stop();
                callBack.loadContent();
            }
            else{

                new CountDownTimer(delayTask, 1000) {

                    public void onTick(long millisUntilFinished) {

                        String display = Long.toString(millisUntilFinished / 1000, 10);
                        countdownText.setText(display);
                        nextButton.setClickable(false);
                    }

                    public void onFinish() {
                        countdownText.setVisibility(View.GONE);
                        nextButton.setClickable(true);
                        switch (taskType) {
                            case MEMORY:
                                //bannerText.setText("This is a memory test. I am going to read a list of words that you will have to remember now and later on. Listen carefully. When I am through, tell me as many words as you can remember. It doesn’t matter in what order you say them. I am going to read the same list for a second time. Try to remember and tell me as many words as you can, including words you said the first time. I will ask you to recall those words again at the end of the test.");
                                memorization("FACE,VELVET,CHURCH,DAISY,RED", 1);
                                break;
                            case ATENTION_NUMBERS:
                                //bannerText.setText("I am going to say some numbers and when I am through, type them to me exactly as I said them");
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
        });

        FloatingActionButton helpButton = mainView.findViewById(R.id.helpButton);
        helpButton.setOnClickListener(view -> {
            if(builder != null){
                builder.show();
            }
        });

        sttButton.setOnClickListener(view -> callSTT());


        return mainView;
    }

    private void orientation() {


    }

    private void recall(String words) {


    }

    private void similarity() {
        array = new String[4];
        array[0] = "train-bicycle";
        array[1] = "transport,speed";
        array[2] = "watch-ruler";
        array[3] = "measurement,numbers";
        addicionalTaskText.setText(array[index]);
        index+=2;
        submitAnswerButton.setOnClickListener(v -> {
            if(index == 5){
                submitAnswerButton.setVisibility(View.GONE);
                addicionalTaskText.setText("You have finalizaed the task");
            }
            else{
                answer.add(addicionalTaskInput.getText().toString());
                addicionalTaskText.setText(array[index]);
                index+=2;
            }
        });

    }

    private void fluencyWithWords(String letter, int numberOfWords) {
        addicionalTaskInput.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        showUserInput();
        addicionalTaskInput.requestFocus();
        if(!addicionalTaskInput.getText().toString().isEmpty())
            adapter.addWord(addicionalTaskInput.getText().toString());
        else
            Toast.makeText(context,"PROVIDE DATA",Toast.LENGTH_LONG).show();
    }

    private void repeatPhrase(String statement) {
        speakPhrase(statement);
    }

    private void serialSubstraction(int startingNumber, final int substration, final int times) {
        index = 0;
        addicionalTaskText.setText(startingNumber + " - " + substration);
        addicionalTaskInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        submitAnswerButton.setOnClickListener(v -> {
            if(index == times){
                submitAnswerButton.setVisibility(View.GONE);
                addicionalTaskText.setText("You have finalizaed the task");
            }
            else{
                answer.add(addicionalTaskInput.getText().toString());
                addicionalTaskText.setText(addicionalTaskInput.getText().toString() + " - " + substration);
                index++;
            }
        });

        showUserInput();
    }

    private void tapLetter(final String target, String words) {
        array = words.split(",");
        index = 0;
        lastIndex = 0;
        layout.setOnClickListener(v -> {
             if(index > 0 && lastIndex != index) {
                answer.add(array[index - 1]);
                Toast.makeText(context, "TAP ON " + array[index - 1], Toast.LENGTH_LONG).show();
                lastIndex = index;
             }

        });

        enumeration();
    }

    private void repeatBackwards(String numbers) {
        array = numbers.split(",");
        index = 0;
        clearLastEnumeration();
        hideInputs();
        placeFirstInput();
        setVariousInputs();
        enumeration();


        addicionalTaskText.setText("Introduce the secuence backwards");
        submitAnswerButton.setOnClickListener(v -> {
            for (EditText editText:variousInputs)
                answer.add(editText.getText().toString());
            Toast.makeText(context, "DONE", Toast.LENGTH_LONG).show();
            submitAnswerButton.setVisibility(View.GONE);
        });
    }

    private void clearLastEnumeration() {
        addicionalTaskInput.getText().clear();
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
        //showUserInput();


        addicionalTaskInput.requestFocus();
        submitAnswerButton.setOnClickListener(v -> {
            for (EditText editText:variousInputs) {
                answer.add(editText.getText().toString());
                editText.getText().clear();
            }
            addicionalTaskInput.requestFocus();
            bannerText.setText("Now I am going to say some more numbers, but when I am through you must repeat them to me in the backwards order.");
            repeatBackwards("7,4,2");
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
            editText.setGravity(Gravity.CENTER_HORIZONTAL);
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

            editText.setOnFocusChangeListener((v, hasFocus) -> {
                if(hasFocus){
                    index = Integer.parseInt(v.getTag().toString());
                }
            });


            set.connect(editText.getId(), ConstraintSet.LEFT, numbersId[i - 1], ConstraintSet.RIGHT, 8);
            set.connect(editText.getId(), ConstraintSet.TOP, numbersId[i - 1], ConstraintSet.TOP, 0);
            set.constrainHeight(editText.getId(), dimens);
            set.constrainWidth(editText.getId(), dimens);
            set.setTranslationX(editText.getId(),positionX);
            set.setVisibility(editText.getId(),ConstraintSet.INVISIBLE);
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

        addicionalTaskInput.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                index = Integer.parseInt(v.getTag().toString());
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
            case MEMORY:
                bannerText.setText("This is a memory test. I am going to read a list of words that you will have to remember now and later on. Listen carefully. When I am through, tell me as many words as you can remember. It doesn’t matter in what order you say them. I am going to read the same list for a second time. Try to remember and tell me as many words as you can, including words you said the first time. I will ask you to recall those words again at the end of the test.");
                break;
            case ATENTION_NUMBERS:
                bannerText.setText("I am going to say some numbers and when I am through, type them to me exactly as I said them");
                break;
            case ATENTION_LETTERS:
                bannerText.setText("I am going to read a sequence of letters. Every time I say the letter A, tap your hand once. If I say a different letter, do not tap");
                break;
            case ATENTION_SUBSTRACTION:
                bannerText.setText("Now, I will ask you to count by subtracting seven from 100, and then, keep subtracting seven from your answer until I tell you to stop ");
                break;
            case LANGUAGE:
                //I only know that John is the one to help today.
                bannerText.setText("I am going to read you a sentence. Repeat it after me, exactly as I say it. ");
                break;
            case FLUENCY:
                bannerText.setText("Tell me as many words as you can think of that begin with a certain letter of the alphabet that I will tell you in a moment. You can say any kind of word you want, except for proper nouns (like Bob or Boston), numbers, or words that begin with the same sound but have a different suffix, for example, love, lover, loving. I will tell you to stop after one minute");
                break;
            case ABSTRACTION:
                bannerText.setText("I am going to give you two words and I want you to introduce the similarity of them. ");
                break;
            case RECALL:
                bannerText.setText("I read some words to you earlier, which I asked you to remember. Tell me as many of those words as you can remember");
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
        addicionalTaskInput.requestFocus();
        submitAnswerButton.setVisibility(View.VISIBLE);
        sttButton.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        if(variousInputs != null && variousInputs.size() > 0){
            for (EditText editText:variousInputs){
                editText.setVisibility(View.VISIBLE);
            }
        }
    }

    private void hideInputs() {
        addicionalTaskText.setVisibility(View.INVISIBLE);
        addicionalTaskInput.setVisibility(View.INVISIBLE);
        submitAnswerButton.setVisibility(View.INVISIBLE);
        sttButton.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        if(variousInputs != null && variousInputs.size() > 0){
            for (EditText editText:variousInputs){
                editText.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void memorization(String words, int times) {
        array = words.split(",");
        index = 0;
        enumeration();
        addicionalTaskInput.requestFocus();
        if(times > 0) {

        }
    }


    private void enumeration() {
        TextToSpeechLocal.getInstance(context,this).enumerate(array);
    }

    private void speakPhrase(String phrase){
        TextToSpeechLocal.getInstance(context, this).readOutLoud(phrase);
    }



    private void buildDialog(){
        builder = new Dialog(context);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = builder.getWindow();
        if(window != null) {
            window.setBackgroundDrawable(
                    new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        builder.setOnDismissListener(dialogInterface -> {
            //nothing;
        });


        TextView content = new TextView(context);
        content.setText(getResources().getText(R.string.app_name));
        content.setBackgroundColor(getResources().getColor(R.color.white,context.getTheme()));
        builder.addContentView(content, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        );


    }

    @Override
    public void startTTS() {
        Toast.makeText(context,"AHORA",Toast.LENGTH_LONG).show();
    }

    @Override
    public void TTSEnded() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> showUserInput());

    }

    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public void onStart() {
        TextToSpeechLocal.getInstance(context,this);
        super.onStart();
    }

    @Override
    public void onPause() {
        TextToSpeechLocal.getInstance(context,this).stop();
        super.onPause();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == STT_CODE){
            if (resultCode == RESULT_OK && null != data) {
                ArrayList results = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                Object result = results.get(0);
                    if(variousInputs != null && variousInputs.size() > 0){
                        String[] tokens = result.toString().split("");
                        for(int i = 0, k = 0; k < tokens.length && i < variousInputs.size(); ++i, ++k){
                            if(tokens[k].isEmpty() || tokens[k].equals("")){
                                ++k;
                                if(k < tokens.length)
                                    variousInputs.get(i).setText(tokens[k]);
                            }
                            else{
                                variousInputs.get(i).setText(tokens[k]);
                            }
                        }
                    }
                    else {
                        addicionalTaskInput.setText(result.toString());
                    }

            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    public void callSTT() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,1000);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Need to speak");
        try {
            startActivityForResult(intent, STT_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(context,
                    "Sorry your device not supported",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
