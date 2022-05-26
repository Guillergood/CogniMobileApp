package ugr.gbv.cognimobile.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;

import java.util.ArrayList;

import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.database.CognimobilePreferences;
import ugr.gbv.cognimobile.interfaces.LoadContent;
import ugr.gbv.cognimobile.utilities.ContextDataRetriever;
import ugr.gbv.cognimobile.utilities.ImageConverse;
import ugr.gbv.cognimobile.utilities.TextToSpeechLocal;

import static android.app.Activity.RESULT_OK;

/**
 * Class to display the task type "Image":
 * {@link Task#IMAGE}
 */
public class ImageTask extends Task {

    private static final int STT_CODE = 2;
    private int selected;
    private int[] imagesId;
    private View mainView;
    private String[] expectedAnswers;
    private final ArrayList<String> answers;
    private final Bundle bundle;
    private LinearLayout sttButtonContainer;

    /**
     * Constructor
     *
     * @param callBack callback to pass the parent the events
     * @param bundle   bundle of information to be filled into the task
     */
    public ImageTask(LoadContent callBack, Bundle bundle) {
        this.callBack = callBack;
        selected = 0;
        taskType = Task.IMAGE;
        this.bundle = bundle;
        answers = new ArrayList<>();
    }

    /**
     * Overrides {@link androidx.fragment.app.Fragment#onViewCreated(View, Bundle)}
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Handler handler = new Handler();
        handler.post(this::shouldDisplayHelpAtBeginning);
    }

    /**
     * Overrides {@link androidx.fragment.app.Fragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * Also sets all the necessary elements for the task to be displayed and be completed.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.image_task, container, false);

        context = getContext();
        parent = getActivity();

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

        sttButtonContainer = mainLayout.findViewById(R.id.sttButtonContainer);

        FloatingActionButton sttButton = mainView.findViewById(R.id.sttButton);

        sttButton.setOnClickListener(view -> callSTT());

        firstInput = mainView.findViewById(R.id.image_task_input);

        firstInput.setFocusableInTouchMode(true);
        firstInput.requestFocus();
        firstInput.setOnEditorActionListener((v, actionId, event) -> handleSubmitKeyboardButton(actionId));


        for (int i = 0; i < imagesArray.length; ++i) {
            String[] possibleSplit = imagesArray[i].split("base64,");
            Bitmap decodedByte;
            if (possibleSplit.length > 1){
                decodedByte = ImageConverse.getInstance().decodeFromBase64(possibleSplit[1]);
            }
            else {
                decodedByte = ImageConverse.getInstance().decodeFromBase64(imagesArray[i]);
            }
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

        switch (CognimobilePreferences.getConfig(context)) {
            case DEFAULT:
            case ONLY_TEXT:
                hideMicro();
                break;
            case ONLY_LANGUAGE:
                cantEdit();
                break;
        }

        if (imagesArray.length <= 1) {
            setNextButtonStandardBehaviour();
        }

        addTextWatcherToInput();


        return mainView;
    }

    /**
     * Goes to the next task to complete, it can be:
     * - More images to recognize.
     * - Next task.
     */
    private void nextTask() {

        ImageView actualImage = mainView.findViewById(imagesId[selected]);
        actualImage.setVisibility(View.INVISIBLE);

        setScoring();

        addSubmitTime();

        selected++;
        ImageView nextImage = mainView.findViewById(imagesId[selected]);
        nextImage.setVisibility(View.VISIBLE);


        clearInputs();
        if (selected >= imagesId.length - 1) {
            setNextButtonStandardBehaviour();
            taskEnded = true;
        }

    }

    /**
     * Clears the text in the inputs
     */
    private void clearInputs() {
        clearedByMethod = true;
        firstInput.getText().clear();
    }


    /**
     * Call the Text-to-Speech function, {@link TextToSpeechLocal}.
     */
    private void callSTT() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1000);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, getLanguage());
        try {
            startActivityForResult(intent, TextToSpeechLocal.STT_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(context,
                    "Sorry your device not supported",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Hides the microphone button.
     */
    private void hideMicro() {
        sttButtonContainer.setVisibility(View.GONE);
    }

    private void cantEdit() {
        firstInput.setEnabled(false);
        firstInput.setKeyListener(null);
    }


    /**
     * onActivityResult method allows to catch information from a another activity.
     * This method retrieves the text from the Speech-to-Text function.
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     *                    <p>
     *                    It catches the link url to be consumed by AWARE.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == STT_CODE) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> results = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (results != null) {
                    String answer = null;
                    for (Object result : results) {
                        String option = result.toString();

                        if (answer == null || answer.isEmpty())
                            answer = option.replaceAll("\\d", "");


                    }
                    if(answer !=null) {
                        firstInput.setText(answer);
                    }

                }
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Overrides {@link Task#saveResults()}
     */
    @Override
    void saveResults() throws JSONException {
        setScoring();
        addSubmitTime();
        callBack.getJsonAnswerWrapper().addArrayList("answer_sequence", answers);
        callBack.getJsonAnswerWrapper().addStringArray("expected_answers", expectedAnswers);
        callBack.getJsonAnswerWrapper().addField("task_type", taskType);
        callBack.getJsonAnswerWrapper().addField("score", score);
        callBack.getJsonContextEvents().addField(ContextDataRetriever.SpecificNamingCharacterChange, ContextDataRetriever.retrieveInformationFromStringArrayList(characterChange));
        callBack.getJsonContextEvents().addField(ContextDataRetriever.SpecificNamingStartWriting, ContextDataRetriever.retrieveInformationFromLongArrayList(startWritingTimes));
        callBack.getJsonContextEvents().addField(ContextDataRetriever.SpecificNamingSubmitAnswer, ContextDataRetriever.retrieveInformationFromLongArrayList(submitAnswerTimes));
    }

    /**
     * Overrides {@link Task#setScoring()}
     */
    @Override
    void setScoring() {
        if (selected < imagesId.length) {
            if (firstInput.getText().toString().isEmpty()) {
                answers.add("");
            } else {
                answers.add(firstInput.getText().toString());
                String[] possibleAnswers = expectedAnswers[selected].split(",");

                boolean goOn = true;

                for (int i = 0; i < possibleAnswers.length && goOn; ++i) {
                    if (possibleAnswers[i].toLowerCase().equals(firstInput.getText().toString().toLowerCase())) {
                        score++;
                        goOn = false;
                    }
                }

            }

        }
    }
}

