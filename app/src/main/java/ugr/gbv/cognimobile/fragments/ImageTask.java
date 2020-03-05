package ugr.gbv.cognimobile.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.Locale;

import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.interfaces.LoadContent;

import static android.app.Activity.RESULT_OK;

public class ImageTask extends Task {

    private static final int STT_CODE = 2;
    private int selected;
    private int[] imagesId;
    private View mainView;
    private EditText input;

    public ImageTask(LoadContent callBack){
        this.callBack = callBack;
        selected = 0;
        taskType = Task.IMAGE;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.image_task, container, false);

        context = getContext();

        bannerText = mainView.findViewById(R.id.banner_text);


        final CardView layout = mainView.findViewById(R.id.cardView);

        final String[] imagesArray = context.getResources().getStringArray(R.array.images);


        imagesId = new int[imagesArray.length];

        input = mainView.findViewById(R.id.image_task_input);

        input.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                nextTask();
                handled = true;
            }
            return handled;
        });



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

        helpButton = mainView.findViewById(R.id.helpButton);

        buildDialog();


        Button nextButton = mainView.findViewById(R.id.nextTaskButton);
        nextButton.setOnClickListener(view -> nextTask());

        Button idkButton = mainView.findViewById(R.id.idk_button);
        idkButton.setOnClickListener(view -> nextTask());





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
                clearInputs();
            }
        }
    }

    private void clearInputs() {
        input.getText().clear();
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

