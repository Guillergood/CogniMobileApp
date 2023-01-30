package ugr.gbv.cognimobile.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
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
import ugr.gbv.cognimobile.dto.ResultEvent;
import ugr.gbv.cognimobile.dto.ResultTask;
import ugr.gbv.cognimobile.interfaces.LoadContent;
import ugr.gbv.cognimobile.utilities.ContextDataRetriever;
import ugr.gbv.cognimobile.utilities.ErrorHandler;
import ugr.gbv.cognimobile.utilities.TextToSpeechLocal;

/**
 * Parent class to hold all the specific task.
 */
public abstract class Task extends Fragment {

    protected Context context;
    private Dialog builder;
    LoadContent callBack;
    FloatingActionButton centerButton;
    ResultEvent resultEvent = new ResultEvent();
    ResultTask resultTask = new ResultTask();
    TextView bannerText;
    RelativeLayout banner;
    FloatingActionButton submitAnswerButton;
    ConstraintLayout mainLayout;
    public final static int DEFAULT = 0;
    public final static int ONLY_TEXT = 1;


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
    final ArrayList<Long> startWritingTimes;

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
    public final static int ONLY_LANGUAGE = 2;
    Activity parent;
    EditText firstInput;
    Handler handler;

    final ArrayList<String> characterChange;
    final ArrayList<Long> submitAnswerTimes;
    private boolean writing;

    /**
     * Constructor
     */
    public Task() {
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

    /**
     * Load the next task to be completed by the user.
     */
    private void loadNextTask() {
        checkIfUserHasSkippedTask();
        resultEvent.setGenericTimeEndTask(ContextDataRetriever.addTimeStamp());
        resultEvent.setGenericTimeNextTask(ContextDataRetriever.addTimeStamp());
        callBack.getTestEventDTO().getEvents().add(resultEvent);
        callBack.getTestAnswerDTO().getTasks().add(resultTask);
        resultEvent = new ResultEvent();
        resultTask = new ResultTask();

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (TextToSpeechLocal.isInitialized())
            TextToSpeechLocal.stop();
        callBack.loadContent();
    }

    /**
     * Adds the time when the submit button was clicked to the contextual data.
     */
    void addSubmitTime() {
        if (writing) {
            submitAnswerTimes.add(ContextDataRetriever.addTimeStamp());
            writing = false;
        }
    }

    /**
     * Adds the time when the user has started typing to the contextual data.
     */
    void addWritingTime() {
        if (!writing && !clearedByMethod) {
            writing = true;
            startWritingTimes.add(ContextDataRetriever.addTimeStamp());
        }
        clearedByMethod = false;
    }


    /**
     * Checks if the user has skipped a task
     *
     */
    private void checkIfUserHasSkippedTask() {
        boolean skipped = false;
        switch (taskType) {
            case GRAPH:
                skipped = resultEvent.getSpecificATMPoints().size() == 0;
                break;
            case CUBE:
                skipped = resultEvent.getSpecificVSCubePoints().size() == 0;
                break;
            case WATCH:
                skipped = resultEvent.getSpecificVSClockPoints().size() == 0;
                break;
            case IMAGE:
                skipped = TextUtils.isEmpty(resultEvent.getSpecificNamingCharacterChange());
                break;
            case MEMORY:
                skipped = TextUtils.isEmpty(resultEvent.getSpecificMemoryCharacterChange());
                break;
            case ATTENTION_NUMBERS:
                skipped = TextUtils.isEmpty(resultEvent.getSpecificAttentionNumbersItemPosition());
                break;
            case ATTENTION_LETTERS:
                skipped = !taskEnded;
                break;
            case ATTENTION_SUBTRACTION:
                skipped = TextUtils.isEmpty(resultEvent.getSpecificSubtractionCharacterChange());
                break;
            case LANGUAGE:
                skipped = TextUtils.isEmpty(resultEvent.getSpecificSRCharacterChange());
                break;
            case FLUENCY:
                skipped = TextUtils.isEmpty(resultEvent.getSpecificFluencyCharacterChange());
                break;
            case ABSTRACTION:
                skipped = TextUtils.isEmpty(resultEvent.getSpecificAbstractionCharacterChange());
                break;
            case RECALL:
                skipped = TextUtils.isEmpty(resultEvent.getSpecificRecallCharacterChange());
                break;
            case ORIENTATION:
                skipped = TextUtils.isEmpty(resultEvent.getSpecificOrientationCharacterChange());
                break;
        }

        resultEvent.setGenericSkippedTask(skipped);
    }

    /**
     * Sets the next button to its standard behaviour, go to the next task.
     */
    void setNextButtonStandardBehaviour() {

        rightButton.setOnClickListener(view -> {

            if(!taskEnded) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle(getString(R.string.confirmation));
                builder.setMessage(getText(R.string.leave_task));
                builder.setCancelable(false);
                builder.setPositiveButton(getString(R.string.continue_next_task), (dialog, which) -> {
                    try {
                        saveResults();
                    } catch (JSONException e) {
                        ErrorHandler.displayError(e.getMessage());
                    }
                    loadNextTask();
                });
                builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
                builder.show();
            }
            else{
                TextToSpeechLocal.stop();
                try {
                    saveResults();
                } catch (JSONException e) {
                    ErrorHandler.displayError(e.getMessage());
                }
                loadNextTask();
            }
        });
    }


    /**
     * Hides the top of the screen banner
     */
    public void hideBanner() {
        banner.setVisibility(View.GONE);
    }

    /**
     * Shows the top of the screen banner
     */
    public void displayBanner() {
        banner.setVisibility(View.VISIBLE);
    }


    /**
     * Builds the help dialog to be displayed when the user clicks the "help" button.
     */
    void buildDialog() {
        builder = new Dialog(context);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = builder.getWindow();
        if (window != null) {
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
            resultEvent.setGenericTimeHelp(ContextDataRetriever.addTimeStamp());
        });
    }

    /**
     * Displays the help dialog.
     */
    private void showDialog() {
        TextView textView = builder.findViewById(R.id.dialogText);
        textView.setText(bannerText.getText());
        LottieAnimationView animationView = builder.findViewById(R.id.motion);
        animationView.setAnimation(taskType + ".json");
        animationView.setImageAssetsFolder("images");
        animationView.playAnimation();
        builder.show();
    }

    /**
     * Add the typing listener to retrieve all the information from the user.
     */
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


    /**
     * Mark the task as completed or ended.
     */
    void taskIsEnded() {
        taskEnded = true;
        resultEvent.setGenericTimeEndTask(ContextDataRetriever.addTimeStamp());
        showTaskIsEnded();
        setNextButtonStandardBehaviour();
        if (taskType == ATTENTION_LETTERS) {
            TextTask task = (TextTask) this;
            task.getPlayableArea().setClickable(false);
        }
    }


    /**
     * Shows that the task is completed on the top banner.
     */
    private void showTaskIsEnded() {
        bannerText.setText(R.string.task_is_ended);
    }

    /**
     * Handles the action sent from the keyboard
     *
     * @param actionId Flag from IME options {@link EditorInfo#imeOptions}
     * @return returning true will keep the keyboard on.
     */
    boolean handleSubmitKeyboardButton(int actionId) {
        boolean handled = true;
        if (taskType > IMAGE) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                checkIfTaskIsAboutToEnd();
                submitAnswerButton.performClick();
                if(taskType == MEMORY || taskType == RECALL){
                    handled = true;
                }
                else{
                    handled = !taskEnded;
                }
            }
        } else {
            rightButton.callOnClick();
        }
        return handled;
    }

    /**
     * Checks if the tasks is about to end.
     */
    private void checkIfTaskIsAboutToEnd() {
        switch (taskType) {
            case ATTENTION_SUBTRACTION:
                taskEnded = index >= length;
                break;
            case LANGUAGE:
            case ABSTRACTION:
            case ORIENTATION:
                taskEnded = index >= length - 1;
                break;
        }
    }

    /**
     * Gets the task score
     *
     * @return score
     */
    public int getScore() {
        return score;
    }

    /**
     * Gets the task language
     *
     * @return language
     */
    public Locale getLanguage() {
        return callBack.getLanguage();
    }

    /**
     * Gets the task type
     *
     * @return task type
     */
    public int getTaskType() {
        return taskType;
    }

    /**
     * Gets the main layout
     *
     * @return main layout
     */
    public ConstraintLayout getMainLayout() {
        return mainLayout;
    }

    /**
     * Gets if the task has ended
     *
     * @return if the task has ended
     */
    public boolean hasEnded() {
        return taskEnded;
    }

    /**
     * Checks if the help button should be clicked automatically at the beginning of the task.
     * This is configured on the tests JSON.
     */
    void shouldDisplayHelpAtBeginning() {
        if (displayHelpAtBeginning) {
            centerButton.callOnClick();
        } else {
            if (taskType == Task.IMAGE) {
                callBack.showKeyboard(parent);
            }
        }
    }


    /**
     * Saves all the task results
     *
     * @throws JSONException if the json was not able to handle the information.
     */
    abstract void saveResults() throws JSONException;

    /**
     * Sets the score of the task.
     */
    abstract void setScoring();

    /**
     * OnCreate method to create the view and instantiate all the elements and put the info,
     * Sets some contextual information also.
     *
     * @param savedInstanceState contains the most recent data from the activity
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        writing = false;
        resultEvent.setGenericTimeBeforeTask(ContextDataRetriever.addTimeStamp());
        resultEvent.setGenericTimeStartTask(ContextDataRetriever.addTimeStamp());
        super.onCreate(savedInstanceState);
    }
}
