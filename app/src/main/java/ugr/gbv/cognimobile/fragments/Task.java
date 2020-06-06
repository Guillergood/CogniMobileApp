package ugr.gbv.cognimobile.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Locale;

import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.interfaces.LoadContent;
import ugr.gbv.cognimobile.utilities.ContextDataRetriever;
import ugr.gbv.cognimobile.utilities.ErrorHandler;
import ugr.gbv.cognimobile.utilities.TextToSpeechLocal;

public abstract class Task extends Fragment {

    protected Context context;
    private Dialog builder;
    LoadContent callBack;
    FloatingActionButton centerButton;
    TextView bannerText;
    RelativeLayout banner;
    FloatingActionButton submitAnswerButton;
    ConstraintLayout mainLayout;
    final static int DEFAULT = 0;
    final static int ONLY_TEXT = 1;


    int taskType;
    int index;
    int lastIndex;
    int length;
    int score;
    boolean loaded;
    boolean providedTask;
    boolean taskEnded;
    boolean displayHelpAtBeginning;
    boolean clearedByMethod;
    ArrayList<Long> startWritingTimes;

    FloatingActionButton leftButton;
    FloatingActionButton rightButton;

    public static final int GRAPH = 0;
    public static final int CUBE = 1;
    public static final int WATCH = 2;
    public static final int IMAGE = 3;
    public static final int MEMORY = 4;
    public static final int ATTENTION_NUMBERS = 5;
    public static final int ATTENTION_LETTERS = 6;
    public static final int ATTENTION_SUBTRACTION = 7;
    public static final int LANGUAGE = 8;
    public static final int FLUENCY = 9;
    public static final int ABSTRACTION = 10;
    public static final int RECALL = 11;
    public static final int ORIENTATION = 12;
    final static int ONLY_LANGUAGE = 2;
    Activity parent;
    EditText firstInput;
    Handler handler;

    ArrayList<String> characterChange;
    ArrayList<Long> submitAnswerTimes;
    private boolean writing;

    public Task(){
        loaded = false;
        providedTask = false;
        taskEnded = false;
        index = 0;
        lastIndex = 0;
        length = 0;
        score = 0;
        characterChange = new ArrayList<>();
        startWritingTimes = new ArrayList<>();
        submitAnswerTimes = new ArrayList<>();
        clearedByMethod = false;
    }

    private void loadNextTask(){
        try {
            checkIfUserHasSkippedTask();
            if (taskType < Task.MEMORY) {
                callBack.getJsonContextEvents().addField(ContextDataRetriever.GenericTimeEndTask, ContextDataRetriever.addTimeStamp());
            }
            callBack.getJsonContextEvents().addField(ContextDataRetriever.GenericTimeNextTask, ContextDataRetriever.addTimeStamp());
            callBack.getJsonContextEvents().addTaskField();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if(TextToSpeechLocal.isInitialized())
            TextToSpeechLocal.stop();
        callBack.loadContent();
    }

    void addSubmitTime() {
        if (writing) {
            submitAnswerTimes.add(ContextDataRetriever.addTimeStamp());
            writing = false;
        }
    }

    void addWritingTime() {
        if (!writing && !clearedByMethod) {
            writing = true;
            startWritingTimes.add(ContextDataRetriever.addTimeStamp());
        }
        clearedByMethod = false;
    }


    private void checkIfUserHasSkippedTask() throws JSONException {
        boolean skipped = false;
        switch (taskType) {
            case GRAPH:
                skipped = callBack.getJsonContextEvents().getFieldArray(ContextDataRetriever.SpecificATMPoints).length() == 0;
                break;
            case CUBE:
                skipped = callBack.getJsonContextEvents().getFieldArray(ContextDataRetriever.SpecificVSCubePoints).length() == 0;
                break;
            case WATCH:
                skipped = callBack.getJsonContextEvents().getFieldArray(ContextDataRetriever.SpecificVSClockPoints).length() == 0;
                break;
            case IMAGE:
                skipped = callBack.getJsonContextEvents().getFieldString(ContextDataRetriever.SpecificNamingCharacterChange).isEmpty();
                break;
            case MEMORY:
                skipped = callBack.getJsonContextEvents().getFieldString(ContextDataRetriever.SpecificMemoryCharacterChange).isEmpty();
                break;
            case ATTENTION_NUMBERS:
                skipped = callBack.getJsonContextEvents().getFieldString(ContextDataRetriever.SpecificAttentionNumbersItemPosition).isEmpty();
                break;
            case ATTENTION_LETTERS:
                skipped = !taskEnded;
                break;
            case ATTENTION_SUBTRACTION:
                skipped = callBack.getJsonContextEvents().getFieldString(ContextDataRetriever.SpecificSubtractionCharacterChange).isEmpty();
                break;
            case LANGUAGE:
                skipped = callBack.getJsonContextEvents().getFieldString(ContextDataRetriever.SpecificSRCharacterChange).isEmpty();
                break;
            case FLUENCY:
                skipped = callBack.getJsonContextEvents().getFieldString(ContextDataRetriever.SpecificFluencyCharacterChange).isEmpty();
                break;
            case ABSTRACTION:
                skipped = callBack.getJsonContextEvents().getFieldString(ContextDataRetriever.SpecificAbstractionCharacterChange).isEmpty();
                break;
            case RECALL:
                skipped = callBack.getJsonContextEvents().getFieldString(ContextDataRetriever.SpecificRecallCharacterChange).isEmpty();
                break;
            case ORIENTATION:
                skipped = callBack.getJsonContextEvents().getFieldString(ContextDataRetriever.SpecificOrientationCharacterChange).isEmpty();
                break;
        }

        try {
            callBack.getJsonContextEvents().addField(ContextDataRetriever.GenericSkippedTask, skipped);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void setNextButtonStandardBehaviour() {

        rightButton.setOnClickListener(view -> {


            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle(getString(R.string.confirmation));
            builder.setMessage(getText(R.string.leave_task));
            builder.setCancelable(false);
            builder.setPositiveButton(getString(R.string.continue_next_task), (dialog, which) -> {
                try {
                    saveResults();
                } catch (JSONException e) {
                    ErrorHandler.getInstance().displayError(context, e.getMessage());
                }
                loadNextTask();
            });
            builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
            builder.show();


        });
    }


    public void hideBanner(){
        banner.setVisibility(View.GONE);
    }

    public void displayBanner(){
        banner.setVisibility(View.VISIBLE);
    }


    void buildDialog(){
        builder = new Dialog(context);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = builder.getWindow();
        if(window != null) {
            window.setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));
        }
        builder.setContentView(R.layout.task_dialog);

        ConstraintLayout dialog = builder.findViewById(R.id.dialog);

        dialog.setOnClickListener(v -> {
            builder.dismiss();
            if (taskType == Task.IMAGE) {
                callBack.showKeyboard(parent);
            }
        });

        centerButton.setOnClickListener(dialogInterface -> {
            showDialog();
            try {
                callBack.getJsonContextEvents().addField(ContextDataRetriever.GenericTimeHelp, ContextDataRetriever.addTimeStamp());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void showDialog(){
        TextView textView = builder.findViewById(R.id.dialogText);
        textView.setText(bannerText.getText());
        LottieAnimationView animationView = builder.findViewById(R.id.motion);
        animationView.setAnimation(taskType +".json");
        animationView.setImageAssetsFolder("images");
        animationView.playAnimation();
        builder.show();
    }

    void addTextWatcherToInput() {
        firstInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                addWritingTime();
                characterChange.add(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    void taskIsEnded(){
        taskEnded = true;
        try {
            callBack.getJsonContextEvents().addField(ContextDataRetriever.GenericTimeEndTask, ContextDataRetriever.addTimeStamp());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        showTaskIsEnded();
        setNextButtonStandardBehaviour();
        if(taskType == ATTENTION_LETTERS){
            TextTask task = (TextTask) this;
            task.getPlayableArea().setClickable(false);
        }
    }


    private void showTaskIsEnded() {
        bannerText.setText(R.string.task_is_ended);
    }

    boolean handleSubmitKeyboardButton(int actionId) {
        // returning true will keep the keyboard on.
        boolean handled = true;
        if(taskType > IMAGE) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                checkIfTaskIsAboutToEnd();
                submitAnswerButton.performClick();
                handled = !taskEnded;
            }
        }
        else{
            rightButton.callOnClick();
        }
        return handled;
    }

    private void checkIfTaskIsAboutToEnd() {
        switch (taskType){
            case ATTENTION_SUBTRACTION:
                taskEnded = index >= length;
                break;
            case LANGUAGE:
            case ABSTRACTION:
            case ORIENTATION:
                taskEnded = index >= length-1;
                break;
        }
    }

    public int getScore() {
        return score;
    }

    public Locale getLanguage() {
        return callBack.getLanguage();
    }

    public LoadContent getCallBack() {
        return callBack;
    }

    public int getTaskType() {
        return taskType;
    }

    public ConstraintLayout getMainLayout() {
        return mainLayout;
    }

    public boolean hasEnded() {
        return taskEnded;
    }

    void shouldDisplayHelpAtBeginning() {
        if (displayHelpAtBeginning) {
            centerButton.callOnClick();
        } else {
            if (taskType == Task.IMAGE) {
                callBack.showKeyboard(parent);
            }
        }
    }


    abstract void saveResults() throws JSONException;
    abstract void setScoring();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        writing = false;

        if (taskType > Task.IMAGE) {
            try {
                callBack.getJsonContextEvents().addField(ContextDataRetriever.GenericTimeBeforeTask, ContextDataRetriever.addTimeStamp());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                callBack.getJsonContextEvents().addField(ContextDataRetriever.GenericTimeStartTask, ContextDataRetriever.addTimeStamp());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        super.onCreate(savedInstanceState);
    }
}
