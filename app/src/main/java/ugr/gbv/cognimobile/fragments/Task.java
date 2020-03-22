package ugr.gbv.cognimobile.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.interfaces.LoadContent;
import ugr.gbv.cognimobile.utilities.TextToSpeechLocal;

public abstract class Task extends Fragment {
    protected Context context;
    Dialog builder;
    LoadContent callBack;
    FloatingActionButton centerButton;
    TextView bannerText;
    int taskType;
    boolean loaded;
    boolean providedTask;

    FloatingActionButton leftButton;
    FloatingActionButton rightButton;

    public static final int TUTORIAL = -1;
    public static final int GRAPH = 0;
    public static final int CUBE = 1;
    public static final int WATCH = 2;
    public static final int IMAGE = 3;
    public static final int MEMORY = 4;
    public static final int ATENTION_NUMBERS = 5;
    public static final int ATENTION_LETTERS = 6;
    public static final int ATENTION_SUBSTRACTION = 7;
    public static final int LANGUAGE = 8;
    public static final int FLUENCY = 9;
    public static final int ABSTRACTION = 10;
    public static final int RECALL = 11;
    public static final int ORIENTATION = 12;



    public Task(){
        loaded = false;
        providedTask = false;
    }

    void loadNextTask(){
        callBack.loadContent();
        if(TextToSpeechLocal.isInitialized())
            TextToSpeechLocal.getInstance(context).stop();
    }

    void setNextButtonStandardBehaviour() {

        rightButton.setOnClickListener(view -> {


            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle(getString(R.string.alert));
            builder.setMessage(getText(R.string.leave_task));
            builder.setCancelable(false);
            builder.setPositiveButton(getString(R.string.leave), (dialog, which) -> loadNextTask());
            builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
            builder.show();


        });
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
}
