package ugr.gbv.myapplication.fragments;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Base64;
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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Locale;

import ugr.gbv.myapplication.R;
import ugr.gbv.myapplication.interfaces.LoadContent;

import static android.app.Activity.RESULT_OK;

public class ImageTask extends Fragment {

    private static final int STT_CODE = 2;
    private Context context;
    private Dialog builder;
    private LoadContent callBack;
    private int selected;
    private int[] imagesId;
    private View mainView;
    private EditText input;

    public ImageTask(LoadContent callBack){
        this.callBack = callBack;
        selected = 0;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.image_task, container, false);

        context = getContext();


        final CardView layout = mainView.findViewById(R.id.cardView);

        final String[] imagesArray = context.getResources().getStringArray(R.array.images);


        imagesId = new int[imagesArray.length];

        input = mainView.findViewById(R.id.image_task_input);
        Button sttButton = mainView.findViewById(R.id.stt_button);

        sttButton.setOnClickListener(v -> callSTT());



        for (int i = 0; i < imagesArray.length; ++i) {
            byte[] decodedString = Base64.decode(imagesArray[i], Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            ImageView imageView = new ImageView(context);
            imageView.setId(View.generateViewId());
            imageView.setImageBitmap(decodedByte);

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


            lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
            if (i > 0) {
                imageView.setVisibility(View.INVISIBLE);
            }



            layout.addView(imageView, lp);
            imagesId[i] = imageView.getId();
        }



        buildDialog();


        Button nextButton = mainView.findViewById(R.id.nextTaskButton);
        nextButton.setOnClickListener(view -> nextTask());

        Button idkButton = mainView.findViewById(R.id.idk_button);
        idkButton.setOnClickListener(view -> nextTask());

        FloatingActionButton helpButton = mainView.findViewById(R.id.helpButton);
        helpButton.setOnClickListener(view -> {
            if(builder != null){
                builder.show();
            }
        });


        return mainView;
    }

    private void nextTask() {
        if(selected == imagesId.length-1) {
            callBack.loadContent();
        }
        else{
            selected++;
            ImageView imageView = mainView.findViewById(imagesId[selected]);
            imageView.setVisibility(View.VISIBLE);
            if(selected > 0){
                imageView = mainView.findViewById(imagesId[selected-1]);
                imageView.setVisibility(View.INVISIBLE);
            }
        }
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == STT_CODE){
            if (resultCode == RESULT_OK && null != data) {
                ArrayList results = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (results != null) {
                    String answer = null;
                    for (Object result : results) {
                        String option = result.toString();

                        if(answer == null || answer.isEmpty())
                            answer = option.replaceAll("\\d","");


                    }

                    if(answer !=null) {
                        input.setText(answer);
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
            startActivityForResult(intent, STT_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(context,
                    "Sorry your device not supported",
                    Toast.LENGTH_SHORT).show();
        }
    }


}

