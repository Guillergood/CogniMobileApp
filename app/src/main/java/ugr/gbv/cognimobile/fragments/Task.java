package ugr.gbv.cognimobile.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;

import java.util.Locale;

import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.interfaces.LoadContent;
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


    public Task(){
        loaded = false;
        providedTask = false;
        taskEnded = false;
        index = 0;
        lastIndex = 0;
        length = 0;
        score = 0;
    }

    private void loadNextTask(){
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if(TextToSpeechLocal.isInitialized())
            TextToSpeechLocal.stop();
        callBack.loadContent();
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

        centerButton.setOnClickListener(dialogInterface -> showDialog());
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


    void taskIsEnded(){
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



}
