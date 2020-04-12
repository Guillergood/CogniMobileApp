package ugr.gbv.cognimobile.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
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
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Locale;

import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.interfaces.LoadContent;
import ugr.gbv.cognimobile.interfaces.TTSHandler;
import ugr.gbv.cognimobile.interfaces.TextTaskCallback;
import ugr.gbv.cognimobile.utilities.TextToSpeechLocal;
import ugr.gbv.cognimobile.adapters.WordListAdapter;

import static android.app.Activity.RESULT_OK;

public class TextTask extends Task implements TTSHandler, TextTaskCallback {

    private String[] array;
    private ArrayList<String> answers;
    private ArrayList<Object> expectedAnswers;



    //UI
    private View mainView;
    private RelativeLayout playableArea;
    private TextView countdownText;
    private TextView additionalTaskText;
    private EditText additionalTaskInput;
    private LinearLayout submitAnswerContainer;
    private RecyclerView recyclerView;
    private WordListAdapter adapter;
    private LinearLayout sttButtonContainer;
    private ExtendedFloatingActionButton startButton;
    private ArrayList<EditText> variousInputs;
    private Bundle bundle;

    //FLAGS
    private int timesCompleted;
    private boolean onlyNumbersInputAccepted;
    private boolean firstDone;


    public TextTask(int taskType, LoadContent callBack, Bundle bundle){
        this.callBack = callBack;
        this.taskType = taskType;
        answers = new ArrayList<>();

        timesCompleted = 0;
        firstDone = false;
        this.bundle = bundle;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.text_task, container, false);

        context = getContext();

        onlyNumbersInputAccepted = false;

        // Assignation of variables
        playableArea = mainView.findViewById(R.id.textSpace);
        mainLayout = mainView.findViewById(R.id.textTaskLayout);
        additionalTaskText = mainView.findViewById(R.id.additional_task_text);
        additionalTaskInput = mainView.findViewById(R.id.additional_task_input);
        submitAnswerButton = mainView.findViewById(R.id.submitButton);
        bannerText = mainView.findViewById(R.id.banner_text);
        FloatingActionButton sttButton = mainView.findViewById(R.id.sttButton);
        startButton = mainLayout.findViewById(R.id.startButton);
        sttButtonContainer = mainLayout.findViewById(R.id.sttButtonContainer);
        submitAnswerContainer = mainLayout.findViewById(R.id.submitButtonContainer);
        rightButton = mainView.findViewById(R.id.rightButton);
        banner = mainView.findViewById(R.id.banner);
        countdownText = mainView.findViewById(R.id.countDownText);
        centerButton = mainView.findViewById(R.id.centerButton);

        // Assignation of recyclerview
        recyclerView = mainView.findViewById(R.id.words_list);
        recyclerView.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new WordListAdapter(this);
        recyclerView.setAdapter(adapter);





        showUserAdditionalTask();
        buildDialog();
        setNextButtonStandardBehaviour();



        startButton.setOnClickListener(v -> startTask());
        sttButton.setOnClickListener(view -> callSTT());


        providedTask = true;

        return mainView;
    }



    private void setNextButtonLoopTask(){
        rightButton.setOnClickListener(v -> {
            hideInputs();
            clearInputs();
            showCountdownAgain();
            clearRecyclerView();
            startTask();
        });
    }



    private void startTask(){
        if(taskType != RECALL && taskType != ATTENTION_SUBTRACTION && taskType != ABSTRACTION && taskType != ORIENTATION) {
            new CountDownTimer(context.getResources().getInteger(R.integer.default_time), context.getResources().getInteger(R.integer.one_seg_millis)) {

                public void onTick(long millisUntilFinished) {
                    String display = Long.toString(millisUntilFinished / 1000, 10);
                    countdownText.setText(display);
                    rightButton.setClickable(false);
                }

                public void onFinish() {
                    countdownText.setVisibility(View.GONE);
                    rightButton.setClickable(true);
                    switch (taskType) {
                        case MEMORY:
                            //bannerText.setText("This is a memory test. I am going to read a list of words that you will have to remember now and later on. Listen carefully. When I am through, tell me as many words as you can remember. It doesn’t matter in what order you say them. I am going to read the same list for a second time. Try to remember and tell me as many words as you can, including words you said the first time. I will ask you to recall those words again at the end of the test.");
                            //String words = "FACE,VELVET,CHURCH,DAISY,RED";
                            String words = "GEZICHT,FLUWEEL,KERK,MADELIEF,ROOD";
                            memorization(words, 2);
                            break;
                        case ATTENTION_NUMBERS:
                            //bannerText.setText("I am going to say some numbers and when I am through, type them to me exactly as I said them");
                            if(firstDone){
                                repeatBackwards("7,4,2");
                            }
                            else{
                                repeat("2,1,8,5,4");
                            }
                            onlyNumbersInputAccepted = true;
                            break;
                        case ATTENTION_LETTERS:
                            tapLetter("A", "F,B,A,C,M,N,A,A,J,K,L,B,A,F,A,K,D,E,A,A,A,J,A,M,O,F,A,A,B");
                            break;
                        case LANGUAGE:
                            //String[] phrases = {"The cat always hid under the couch when dogs were in the room.", "I only know that John is the one to help today."};
                            String[] phrases = {"Ik weet alleen dat Jan vandaag geholpen zou worden.", "De kat verstopte zich altijd onder de bank als er honden in de kamer waren."};
                            repeatPhrase(phrases);
                            break;
                        case FLUENCY:
                            bannerText.setText(R.string.fluency_instructions);
                            fluency("F", 11);
                            break;

                        default:
                            throw new RuntimeException("INVALID TASKTYPE");
                    }

                }
            }.start();
        }
        else{
            switch (taskType){
                case RECALL:
                    recall("FACE,VELVET,CHURCH,DAISY,RED");
                    break;
                case ORIENTATION:
                    orientation();
                    break;
                case ATTENTION_SUBTRACTION:
                    subtractions(100, 7, 5);
                    onlyNumbersInputAccepted = true;
                    break;
                case ABSTRACTION:
                    abstraction();
                    break;
                default:
                    throw new RuntimeException("INVALID TASKTYPE");
            }
        }

        startButton.setVisibility(View.GONE);
        providedTask = true;
        timesCompleted++;
    }


    //-------------------TASKS-------------------//

    private void memorization(String words, int times) {
        array = words.split(",");
        index = 0;
        length = times;
        enumeration();
        setNextButtonLoopTask();
        additionalTaskInput.requestFocus();
        additionalTaskInput.setOnEditorActionListener((v, actionId, event) -> handleSubmitKeyboardButton(actionId));
        enableWordList();
        if(timesCompleted == times){
            setNextButtonStandardBehaviour();
        }

    }

    private void repeat(String numbers) {
        array = numbers.split(",");
        index = 0;
        placeFirstInput();
        changeInputFilterAndType();
        setVariousInputs();
        enumeration();
        additionalTaskInput.requestFocus();
        submitAnswerButton.setOnClickListener(v -> {
            clearInputs();
            additionalTaskInput.requestFocus();
            bannerText.setText(R.string.backwards_instructions);
            hideInputs();
            firstDone = true;
            try {
                saveResults();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            startButton.setVisibility(View.VISIBLE);
            startButton.setOnClickListener(v1 -> {
                showCountdownAgain();
                startTask();
            });
        });
    }

    private void repeatBackwards(String numbers) {
        array = numbers.split(",");
        index = 0;
        clearInputs();
        hideInputs();
        placeFirstInput();
        setVariousInputs();
        enumeration();
        submitAnswerButton.setOnClickListener(v -> {
            taskEnded = true;
            hideInputs();
            clearInputs();
            taskIsEnded();
        });
    }


    private void tapLetter(final String target, String words) {
        array = words.split(",");
        index = 0;
        lastIndex = 0;
        playableArea.setClickable(true);
        playableArea.setFocusable(true);
        playableArea.setOnClickListener(v -> {
            if(index > 0 && lastIndex != index) {
                answers.add(array[index - 1]);
                Toast.makeText(context, "CLICK!", Toast.LENGTH_SHORT).show();
                lastIndex = index;
            }

        });

        enumeration();
    }

    private void subtractions(int startingNumber, final int substration, final int times) {
        index = 0;
        String displaySubtraction = startingNumber + " - " + substration;
        additionalTaskText.setText(displaySubtraction);
        additionalTaskInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        additionalTaskInput.setOnEditorActionListener((v, actionId, event) -> handleSubmitKeyboardButton(actionId));
        submitAnswerButton.setOnClickListener(v -> {
            if(index == length){
                submitAnswerButton.setVisibility(View.GONE);
                hideInputs();
                taskIsEnded();
                checkSubtractions(startingNumber, substration);

            }
            else{
                String displayAnotherSubtraction = additionalTaskInput.getText().toString() + " - " + substration;
                answers.add(additionalTaskInput.getText().toString());
                additionalTaskText.setText(displayAnotherSubtraction);
                index++;
            }
            clearInputs();
        });
        length = times;

        showUserInput();
        hideMicro();
    }

    private void repeatPhrase(String[] phrases) {
        additionalTaskInput.setOnEditorActionListener((v, actionId, event) -> handleSubmitKeyboardButton(actionId));
        submitAnswerButton.setOnClickListener(v -> {
            clearInputs();
            ++index;
            hideInputs();
            if(index >= length){
                setNextButtonStandardBehaviour();
                taskIsEnded();
            }
            else{
                setNextButtonLoopTask();
                speakPhrase(phrases[index]);
            }

        });
        length = phrases.length;

        speakPhrase(phrases[index]);
    }

    private void fluency(String letter, int numberOfWords) {
        additionalTaskInput.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        showUserInput();
        additionalTaskInput.requestFocus();
        additionalTaskInput.setOnEditorActionListener((v, actionId, event) -> handleSubmitKeyboardButton(actionId));
        enableWordList();
        countDownTask(getResources().getInteger(R.integer.one_minute_millis));
    }

    private void abstraction() {
        array = new String[4];
        array[0] = "trein-fiets";
        array[1] = "transport,speed";
        array[2] = "horloge-liniaal";
        array[3] = "measurement,numbers";
        additionalTaskText.setText(array[index]);
        index+=2;
        showUserInput();
        additionalTaskInput.setOnEditorActionListener((v, actionId, event) -> handleSubmitKeyboardButton(actionId));
        submitAnswerButton.setOnClickListener(v -> {
            if(index >= length){
                hideInputs();
                taskIsEnded();
            }
            else{
                answers.add(additionalTaskInput.getText().toString());
                clearInputs();
                additionalTaskText.setText(array[index]);
                index+=2;
            }
        });

        length = array.length;

    }

    private void recall(String words) {
        showUserInput();
        enableWordList();
        additionalTaskInput.setOnEditorActionListener((v, actionId, event) -> handleSubmitKeyboardButton(actionId));
    }


    private void orientation() {
        /*String[] questions = {"Tell me the year we are now.",
                "Tell me the exact date we are now.",
                "Tell which city you are now."};*/
        String[] questions = {"Vertel me het jaar dat we nu zijn.",
                "Vertel me de exacte datum waarop we nu zijn.",
                "Vertel in welke stad u zich nu bevindt."};

        showUserInput();

        submitAnswerButton.setOnClickListener(v -> {
            clearInputs();
            ++index;
            if(index >= length){
                setNextButtonStandardBehaviour();
                hideInputs();
                taskIsEnded();
            }
            else{
                setNextButtonLoopTask();
                additionalTaskText.setText(questions[index]);
            }
        });

        length = questions.length;
        additionalTaskText.setText(questions[index]);
        additionalTaskInput.setOnEditorActionListener((v, actionId, event) -> handleSubmitKeyboardButton(actionId));
    }

    //-------------------END TASKS-------------------//

    //-------------------UI-------------------//

    private void countDownTask(int millis) {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            taskEnded = true;
            hideInputs();
            hideBanner();
        }, millis);
    }

    private void hideMicro() {
        sttButtonContainer.setVisibility(View.GONE);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(mainLayout);
        constraintSet.connect(R.id.submitButtonContainer,ConstraintSet.RIGHT,R.id.textTaskLayout,ConstraintSet.RIGHT,8);
        constraintSet.connect(R.id.submitButtonContainer,ConstraintSet.LEFT,R.id.textTaskLayout,ConstraintSet.LEFT,8);
        constraintSet.applyTo(mainLayout);
        rearrangeSubmitAnswerContainer();
    }

    private void clearInputs() {
        additionalTaskInput.getText().clear();
        if(variousInputs != null) {
            for (int i = 1; i < variousInputs.size(); ++i) {
                mainLayout.removeView(variousInputs.get(i));
            }
            variousInputs.clear();
        }
    }

    private void setVariousInputs() {
        int[] numbersId = new int[array.length];
        numbersId[0] = additionalTaskInput.getId();
        variousInputs = new ArrayList<>();
        variousInputs.add(additionalTaskInput);

        ConstraintSet set = new ConstraintSet();
        set.clone(mainLayout);

        int dimens = getResources().getDimensionPixelSize(R.dimen.input_dimen);
        int portion = mainLayout.getWidth()/array.length;
        int positionX = portion-mainLayout.getWidth()/2;


        for(int i = 1; i < array.length; ++i){
            EditText editText = new EditText(context);
            editText.setId(View.generateViewId());
            editText.setTag(Integer.toString(i));
            editText.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
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

            if(i == array.length-1){
                editText.setOnEditorActionListener((v, actionId, event) -> handleSubmitKeyboardButton(actionId));
            }


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
        set.constrainHeight(additionalTaskInput.getId(), dimens);
        set.constrainWidth(additionalTaskInput.getId(), dimens);

        int portion = mainLayout.getWidth()/array.length;
        int positionX = portion-mainLayout.getWidth()/2;
        set.setTranslationX(additionalTaskInput.getId(),positionX);
        set.applyTo(mainLayout);

        additionalTaskInput.setScrollContainer(true);
        additionalTaskInput.setTag("0");

        additionalTaskInput.addTextChangedListener(new TextWatcher() {
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

        additionalTaskInput.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                index = Integer.parseInt(v.getTag().toString());
            }
        });

    }

    private void changeInputFilterAndType(){
        additionalTaskInput.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        additionalTaskInput.setKeyListener(DigitsKeyListener.getInstance("123456789"));
        InputFilter[] inputArray = new InputFilter[1];
        inputArray[0] = new InputFilter.LengthFilter(1);
        additionalTaskInput.setFilters(inputArray);
    }

    private void showUserAdditionalTask() {
        switch (taskType){
            case MEMORY:
                bannerText.setText(R.string.memory_instructions);
                break;
            case ATTENTION_NUMBERS:
                bannerText.setText(R.string.numbers_instructions);
                break;
            case ATTENTION_LETTERS:
                bannerText.setText(R.string.letters_instructions);
                break;
            case ATTENTION_SUBTRACTION:
                bannerText.setText(R.string.substraction_instructions);
                break;
            case LANGUAGE:
                bannerText.setText(R.string.language_instructions);
                break;
            case FLUENCY:
                bannerText.setText(R.string.fluency_instructions);
                break;
            case ABSTRACTION:
                bannerText.setText(R.string.abstraction_instruction);
                break;
            case RECALL:
                bannerText.setText(R.string.recall_instructions);
                break;
            case ORIENTATION:
                bannerText.setText(R.string.orientation_instructions);
                break;
            default:
                throw new RuntimeException("INVALID TASKTYPE");
        }
    }

    private void showUserInput() {
        additionalTaskText.setVisibility(View.VISIBLE);
        additionalTaskInput.setVisibility(View.VISIBLE);
        additionalTaskInput.requestFocus();
        submitAnswerContainer.setVisibility(View.VISIBLE);
        sttButtonContainer.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        if(variousInputs != null && variousInputs.size() > 0){
            for (EditText editText:variousInputs){
                editText.setVisibility(View.VISIBLE);
            }
        }
    }

    private void hideInputs() {
        additionalTaskText.setVisibility(View.INVISIBLE);
        additionalTaskInput.setVisibility(View.INVISIBLE);
        submitAnswerContainer.setVisibility(View.INVISIBLE);
        sttButtonContainer.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        if(variousInputs != null && variousInputs.size() > 0){
            for (EditText editText:variousInputs){
                editText.setVisibility(View.INVISIBLE);
            }
        }
        callBack.hideKeyboard();

    }

    private void showCountdownAgain(){
        countdownText.setVisibility(View.VISIBLE);
    }

    private void clearRecyclerView() {
        adapter.removeAllWords();
    }

    private void enableWordList(){
        TextView submitButtonLabel = mainView.findViewById(R.id.submitButtonLabel);
        submitButtonLabel.setText(R.string.add_word);
        submitAnswerButton.setOnClickListener(v -> {
            if(!additionalTaskInput.getText().toString().isEmpty()) {
                adapter.addWord(additionalTaskInput.getText().toString());
                recyclerView.scrollToPosition(0);
            }
            else
                Toast.makeText(context,"PROVIDE DATA",Toast.LENGTH_LONG).show();

            clearInputs();
        });
    }

    private void changeSubmitButton() {
        submitAnswerButton.setImageResource(R.drawable.ic_check_black_24dp);
        TextView submitButtonLabel = mainView.findViewById(R.id.submitButtonLabel);
        submitButtonLabel.setText(R.string.save);
    }

    private void restoreSubmitButton() {
        submitAnswerButton.setImageResource(R.drawable.add_word_24dp);
        TextView submitButtonLabel = mainView.findViewById(R.id.submitButtonLabel);
        submitButtonLabel.setText(R.string.add_word);
    }

    private void showEditElements() {

        if(additionalTaskInput.getVisibility() != View.VISIBLE){
            additionalTaskInput.setVisibility(View.VISIBLE);
            submitAnswerContainer.setVisibility(View.VISIBLE);
            rearrangeSubmitAnswerContainer();
        }
        else{
            additionalTaskInput.setVisibility(View.GONE);
            submitAnswerContainer.setVisibility(View.GONE);
        }


    }

    private void rearrangeSubmitAnswerContainer(){
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(mainLayout);
        constraintSet.connect(submitAnswerContainer.getId(), ConstraintSet.START, mainLayout.getId(), ConstraintSet.START, (int) getResources().getDimension(R.dimen.default_margin));
        constraintSet.connect(submitAnswerContainer.getId(), ConstraintSet.END, mainLayout.getId(), ConstraintSet.END, (int) getResources().getDimension(R.dimen.default_margin));
        constraintSet.connect(submitAnswerContainer.getId(), ConstraintSet.TOP, additionalTaskInput.getId(), ConstraintSet.BOTTOM, (int) getResources().getDimension(R.dimen.margin_medium));
        constraintSet.applyTo(mainLayout);
    }


    //-------------------END UI-------------------//

    //-------------------TTS-------------------//

    @Override
    public void startTTS() {
        startButton.setClickable(true);
    }

    @Override
    public void TTSEnded() {
        final Handler handler = new Handler(Looper.getMainLooper());
        if(taskType != ATTENTION_LETTERS) {
            handler.post(this::showUserInput);
            if (taskType == ATTENTION_NUMBERS || taskType == ATTENTION_SUBTRACTION) {
                handler.post(this::hideMicro);
            }
        }
        else{
            taskEnded = true;
            handler.postDelayed(this::taskIsEnded,context.getResources().getInteger(R.integer.default_time));
        }

    }

    private void enumeration() {
        TextToSpeechLocal.getInstance(context,this).enumerate(array);
    }

    private void speakPhrase(String phrase){
        TextToSpeechLocal.getInstance(context, this).readOutLoud(phrase);
    }

    @Override
    public void onPause() {
        TextToSpeechLocal.getInstance(context,this).stop();
        super.onPause();
    }

    @Override
    public void onStart() {
        TextToSpeechLocal.getInstance(context,this);
        super.onStart();
    }

    @Override
    public void onStop() {
        TextToSpeechLocal.getInstance(context).stop();
        TextToSpeechLocal.getInstance(context).clear();
        super.onStop();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == TextToSpeechLocal.STT_CODE){
            if (resultCode == RESULT_OK && null != data) {
                ArrayList results = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (results != null) {
                    String answer = null;
                    for (Object result : results) {
                        String option = result.toString();
                        if(onlyNumbersInputAccepted){
                            if(answer == null || answer.isEmpty())
                                answer = option.replaceAll("\\D","");
                        }
                        else{
                            if(answer == null || answer.isEmpty())
                                answer = option.replaceAll("\\d","");
                        }

                    }

                    if(answer !=null) {
                        if (variousInputs != null && variousInputs.size() > 0) {
                            if(index > variousInputs.size() ) index = 0;
                            String[] tokens = answer.split("(?!^)");
                            for (int k = 0; k < tokens.length && index < variousInputs.size(); ++index, ++k) {
                                if (tokens[k].isEmpty() || tokens[k].equals("")) {
                                    ++k;
                                } else {
                                    variousInputs.get(index).setText(tokens[k]);
                                }
                            }
                        } else {
                            additionalTaskInput.setText(answer);
                        }
                    }

                }
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    private void callSTT() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,1000);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        try {
            startActivityForResult(intent, TextToSpeechLocal.STT_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(context,
                    "Sorry your device not supported",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //-------------------END TTS-------------------//

    //-------------------GETTERS-------------------//

    RelativeLayout getPlayableArea() {
        return playableArea;
    }

    public ConstraintLayout getMainLayout() {
        return mainLayout;
    }

    //-------------------END GETTERS-------------------//

    //-------------------INTERFACES-------------------//

    @Override
    void saveResults() throws JSONException {

        switch (taskType) {
            case ATTENTION_NUMBERS:
                if(firstDone){
                    if(variousInputs != null && variousInputs.size() > 0){
                        for (EditText editText:variousInputs){
                            answers.add(editText.getText().toString());
                        }
                    }
                    callBack.getJsonAnswerWrapper().addArray("answer_backwards", answers);
                }
                else{
                    if(variousInputs != null && variousInputs.size() > 0){
                        for (EditText editText:variousInputs){
                            answers.add(editText.getText().toString());
                        }

                        callBack.getJsonAnswerWrapper().addArray("answer", answers);
                    }
                }
                setScoring();
                break;
            case ATTENTION_LETTERS:
                tapLetter("A", "F,B,A,C,M,N,A,A,J,K,L,B,A,F,A,K,D,E,A,A,A,J,A,M,O,F,A,A,B");
                break;
            case LANGUAGE:
                //String[] phrases = {"The cat always hid under the couch when dogs were in the room.", "I only know that John is the one to help today."};
                String[] phrases = {"Ik weet alleen dat Jan vandaag geholpen zou worden.", "De kat verstopte zich altijd onder de bank als er honden in de kamer waren."};
                repeatPhrase(phrases);
                break;
            case FLUENCY:
                bannerText.setText(R.string.fluency_instructions);
                fluency("F", 11);
                break;

            default:
                throw new RuntimeException("INVALID TASKTYPE");
        }

    }

    @Override
    void setScoring() {
        switch (taskType) {

            case ATTENTION_NUMBERS:
                checkAnswerArray();
                break;

            case ATTENTION_LETTERS:
                checkLettersPressed();
                break;

            case LANGUAGE:
                checkPhrases();
                break;

            case FLUENCY:
                checkFluency();
                break;

            case ABSTRACTION:
                checkAbstraction();
                break;

            case RECALL:
            case ORIENTATION:
                checkRecall();
                break;

        }

        addScoreToJson();

    }

    @Override
    public void editWord(String word) {
        additionalTaskInput.setText(word);
        if(taskType == Task.FLUENCY)
            showEditElements();
        changeSubmitButton();
        submitAnswerButton.setOnClickListener(v -> {
            adapter.editWord(word, additionalTaskInput.getText().toString());
            clearInputs();
            if(taskType == Task.FLUENCY)
                showEditElements();
            else {
                enableWordList();
                restoreSubmitButton();
            }

        });
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    //-------------------END INTERFACES-------------------//
    //-------------------CHECKERS-------------------------//

    private void checkRecall() {
        int errors = 0;

        /*
             Allocate 1 point for each word recalled freely without any cues.
         */

        for(int i = 0; i < answers.size(); ++i) {
            String expectedAnswer = (String) expectedAnswers.get(i);
            if (expectedAnswer.equals(answers.get(i))) {
                ++errors;
            }
        }


        score = answers.size() - errors;
    }

    private void checkAbstraction() {
        int errors = 0;

        /*
            Only the last two item pairs are scored. Give 1 point to each item pair correctly
            answered.

         */

        for(int i = 1; i < answers.size(); ++i) {
            String expectedAnswer = (String) expectedAnswers.get(i);
            if (expectedAnswer.contains(answers.get(i))) {
                ++errors;
            }
        }


        score = answers.size() - errors;
    }

    private void checkFluency() {
        answers = adapter.getAllWords();
        //TODO check answer with a dictionary

        score = 1;

    }

    private void checkPhrases() {
        int errors = 0;

        for(int i = 0; i < answers.size(); ++i) {
            if (!answers.get(i).equalsIgnoreCase((String) expectedAnswers.get(i))) {
                ++errors;
            }
        }
        /*
            Allocate 1 point for each sentence correctly repeated. Repetition must be exact.
         */

        score = answers.size() - errors;
    }

    private void addScoreToJson() {
        try {
            callBack.getJsonAnswerWrapper().addField("score",score);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkAnswerArray() {

        boolean goOn = true;

        for(int i = 0; i < answers.size() && goOn; ++i) {
            if (!answers.get(i).equalsIgnoreCase((String)expectedAnswers.get(i))) {
                goOn = false;
            }
        }
        //if onePoint = true -> 1, otherwise -> 0
        score = goOn ? 1:0;
    }

    private void checkLettersPressed(){
        boolean goOn = true;
        int errors = 0;

        for(int i = 1; i < answers.size() && goOn; ++i) {
            if (!answers.get(i-1).equalsIgnoreCase(answers.get(i))) {
                errors++;
                if(errors > 1){
                    goOn = false;
                }
            }
        }
        //if onePoint = true -> 1, otherwise -> 0
        score = goOn ? 1:0;
    }

    private void checkSubtractions(int firstMinuend, int subtrahend){

        int errors = 0;
        int expectedResult = firstMinuend - subtrahend;


        for(int i = 0; i < answers.size(); ++i) {
            int result = Integer.parseInt(answers.get(i));
            if(expectedResult != result){
                ++errors;
            }
            expectedResult = result - subtrahend;

        }

        /*
            Give no (0) points for no correct subtractions,
            1 point for one correction subtraction,
            2 points for two-to-three correct subtractions,
            and 3 points if the participant successfully makes four or five correct subtraction.
         */

        switch (errors){
            case 0:
            case 1:
                score = 3;
                break;
            case 2:
            case 3:
                score = 2;
                break;
            case 4:
                score = 1;
                break;
            default:
                score = 0;
                break;
        }

    }

    //-------------------END CHECKERS-------------------------//

}
