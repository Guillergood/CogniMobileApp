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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import org.json.JSONException;

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
        banner = mainView.findViewById(R.id.banner);
        mainLayout = mainView.findViewById(R.id.imageTaskLayout);


        final CardView layout = mainView.findViewById(R.id.cardView);

        final String[] imagesArray = context.getResources().getStringArray(R.array.images);


        imagesId = new int[imagesArray.length];

        input = mainView.findViewById(R.id.image_task_input);

        input.setOnEditorActionListener((v, actionId, event) -> handleSubmitKeyboardButton(actionId));



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

        centerButton = mainView.findViewById(R.id.centerButton);

        buildDialog();


        rightButton = mainView.findViewById(R.id.rightButton);
        rightButton.setOnClickListener(view -> nextTask());
        providedTask = true;


        return mainView;
    }

    private void nextTask() {

        ImageView actualImage = mainView.findViewById(imagesId[selected]);
        actualImage.setVisibility(View.INVISIBLE);
        selected++;
        ImageView nextImage = mainView.findViewById(imagesId[selected]);
        nextImage.setVisibility(View.VISIBLE);
        clearInputs();
        if (selected >= imagesId.length - 1) {
            setNextButtonStandardBehaviour();
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


    @Override
    void saveResults() throws JSONException {

    }

    @Override
    void setScoring() {

    }
}

