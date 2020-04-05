package ugr.gbv.cognimobile.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.interfaces.LoadContent;
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

    int taskType;
    int index = 0;
    int lastIndex = 0;
    int length = 0;
    boolean loaded;
    boolean providedTask;
    boolean taskEnded;

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



    public Task(){
        loaded = false;
        providedTask = false;
        taskEnded = false;
    }

    private void loadNextTask(){
        callBack.loadContent();
        if(TextToSpeechLocal.isInitialized())
            TextToSpeechLocal.getInstance(context).stop();
    }

    void setNextButtonStandardBehaviour() {

        rightButton.setOnClickListener(view -> {


            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle(getString(R.string.confirmation));
            builder.setMessage(getText(R.string.leave_task));
            builder.setCancelable(false);
            builder.setPositiveButton(getString(R.string.continue_next_task), (dialog, which) -> {
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

        dialog.setOnClickListener(v -> builder.dismiss());

        centerButton.setOnClickListener(dialogInterface -> showDialog());
    }

    private void showDialog(){
        TextView textView = builder.findViewById(R.id.dialogText);
        textView.setText(bannerText.getText());
        builder.show();
        LottieAnimationView animationView = builder.findViewById(R.id.motion);
        animationView.setAnimation(taskType +".json");
        animationView.setImageAssetsFolder("images");
        animationView.playAnimation();

    }


    void taskIsEnded(){
        showTaskIsEnded();
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




    public int getTaskType() {
        return taskType;
    }

    public ConstraintLayout getMainLayout() {
        return mainLayout;
    }

    public boolean hasEnded() {
        return taskEnded;
    }
}
