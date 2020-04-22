package ugr.gbv.cognimobile.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import org.json.JSONException;

import java.util.ArrayList;

import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.interfaces.LoadContent;
import ugr.gbv.cognimobile.utilities.ImageConversor;

import static android.app.Activity.RESULT_OK;

public class ImageTask extends Task {

    private static final int STT_CODE = 2;
    private int selected;
    private int[] imagesId;
    private View mainView;
    private EditText input;
    private String[] expectedAnswers;
    private ArrayList<String> answers;
    private Bundle bundle;

    public ImageTask(LoadContent callBack,Bundle bundle){
        this.callBack = callBack;
        selected = 0;
        taskType = Task.IMAGE;
        this.bundle = bundle;
        answers = new ArrayList<>();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Handler handler = new Handler();
        handler.post(this::shouldDisplayHelpAtBeginning);
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


        final String[] imagesArray = bundle.getStringArray("images");

        if (imagesArray == null) {
            throw new RuntimeException("IMAGES NULL");
        }

        expectedAnswers = bundle.getStringArray("answer");


        imagesId = new int[imagesArray.length];

        input = mainView.findViewById(R.id.image_task_input);

        input.setOnEditorActionListener((v, actionId, event) -> handleSubmitKeyboardButton(actionId));



        for (int i = 0; i < imagesArray.length; ++i) {
            Bitmap decodedByte = ImageConversor.getInstance().decodeFromBase64(imagesArray[i]);
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


        displayHelpAtBeginning = bundle.getBoolean("display_help");


        return mainView;
    }

    private void nextTask() {

        ImageView actualImage = mainView.findViewById(imagesId[selected]);
        actualImage.setVisibility(View.INVISIBLE);

        setScoring();

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
        setScoring();
        callBack.getJsonAnswerWrapper().addArrayList("answer_sequence", answers);
        callBack.getJsonAnswerWrapper().addField("score",score);
        callBack.getJsonAnswerWrapper().addTaskField();
    }

    @Override
    void setScoring() {
        if(selected < imagesId.length) {
            if (input.getText().toString().isEmpty()) {
                answers.add("");
            } else {
                answers.add(input.getText().toString());
                if (expectedAnswers[selected].toLowerCase().contains(input.getText().toString().toLowerCase())) {
                    score++;
                }
            }

        }
    }
}

