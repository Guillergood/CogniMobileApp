package ugr.gbv.cognimobile.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.SpellCheckerSession;
import android.view.textservice.SuggestionsInfo;
import android.view.textservice.TextInfo;
import android.view.textservice.TextServicesManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.NavUtils;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.database.Provider;
import ugr.gbv.cognimobile.fragments.Task;
import ugr.gbv.cognimobile.interfaces.LoadContent;
import ugr.gbv.cognimobile.utilities.DataSender;
import ugr.gbv.cognimobile.utilities.ErrorHandler;
import ugr.gbv.cognimobile.utilities.JsonAnswerWrapper;
import ugr.gbv.cognimobile.utilities.JsonParserTests;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Test extends AppCompatActivity implements LoadContent, SpellCheckerSession.SpellCheckerSessionListener {

    private static final int MY_DATA_CHECK_CODE = 1050;
    private ArrayList<Task> fragments;
    private int index;
    private int totalScore;
    private View mContentView;
    private JsonAnswerWrapper jsonAnswerWrapper;
    private JsonAnswerWrapper jsonContextEvents;
    private JsonAnswerWrapper jsonContextData;
    private String name;
    private Locale language;

    private int typos;
    private String wordToCheck;
    private boolean doubleBackToExitPressedOnce;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mContentView = findViewById(R.id.fullscreen_content);
        hideNavBar();

        String where = Provider.Cognimobile_Data._ID + " = ?";
        String[] selectionArgs = {Integer.toString(getIntent().getIntExtra("id", 0))};
        String[] projection = new String[]{Provider.Cognimobile_Data.DATA};
        Cursor cursor = getContentResolver().query(Provider.CONTENT_URI_TESTS, projection, where, selectionArgs, Provider.Cognimobile_Data._ID);
        assert cursor != null;
        cursor.moveToFirst();
        String rawJson = cursor.getString(cursor.getColumnIndex(Provider.Cognimobile_Data.DATA));

        cursor.close();

        try {
            fragments = JsonParserTests.getInstance().parseTestToTasks(rawJson,this);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        index = 0;
        totalScore = 0;


        initKeyBoardListener();

        doubleBackToExitPressedOnce = false;


        try {
            JSONObject reader = new JSONObject(rawJson);
            name = reader.getString("name");
            String language = reader.getString("language");
            this.language = new Locale(language);
            jsonAnswerWrapper = new JsonAnswerWrapper(name, language);
            jsonContextEvents = new JsonAnswerWrapper(name, language);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.


    }

    @SuppressLint("InlinedApi")
    private void hideNavBar(){
        mContentView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                //| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                //| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
    }

    private void loadFragment(@NonNull Fragment fragment) {
        getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.fullscreen_content, fragment)
        .commit();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void loadContent() {
        if (index > 1)
            totalScore += fragments.get(index).getScore();
        ++index;
        hideKeyboard(this);
        if (index < fragments.size()) {
            loadFragment(fragments.get(index));
        }
        else{
            try {
                jsonAnswerWrapper.addTotalScore(totalScore);
                DataSender.getInstance().postToServer("insert", "results",jsonAnswerWrapper.getJSONArray(), getApplicationContext());
                DataSender.getInstance().postToServer("insert", "contextEvents", jsonContextEvents.getJSONArray(), getApplicationContext());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            showTestCompletedDialog();
        }
    }

    @Override
    public void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void showKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    @Override
    public JsonAnswerWrapper getJsonAnswerWrapper() {
        return jsonAnswerWrapper;
    }

    @Override
    public JsonAnswerWrapper getJsonContextEvents() {
        return jsonContextEvents;
    }

    @Override
    public JsonAnswerWrapper getJsonContextData() {
        return jsonContextData;
    }


    private void initKeyBoardListener() {
        // Threshold for minimal keyboard height.
        final int MIN_KEYBOARD_HEIGHT_PX = 150;

        // Top-level window decor view.
        final View decorView = getWindow().getDecorView();
        //Register global layout listener.
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            // Retrieve visible rectangle inside window.
            private final Rect windowVisibleDisplayFrame = new Rect();
            private int lastVisibleDecorViewHeight;

            @Override
            public void onGlobalLayout() {

                decorView.getWindowVisibleDisplayFrame(windowVisibleDisplayFrame);
                final int visibleDecorViewHeight = windowVisibleDisplayFrame.height();

                if (index < fragments.size()) {
                    if (lastVisibleDecorViewHeight != 0) {
                        Task actualTask = fragments.get(index);
                        ConstraintLayout constraintLayout = actualTask.getMainLayout();
                        if (lastVisibleDecorViewHeight > visibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX) {
                            if (actualTask.getTaskType() > Task.IMAGE) {
                                ConstraintSet constraintSet = new ConstraintSet();
                                constraintSet.clone(constraintLayout);
                                constraintSet.connect(R.id.additional_task_text, ConstraintSet.START, R.id.textTaskLayout, ConstraintSet.START, (int) getResources().getDimension(R.dimen.margin_medium));
                                constraintSet.connect(R.id.additional_task_text, ConstraintSet.TOP, R.id.textTaskLayout, ConstraintSet.TOP, (int) getResources().getDimension(R.dimen.margin_medium));
                                constraintSet.applyTo(constraintLayout);
                            }
                            actualTask.hideBanner();
                        } else if (lastVisibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX < visibleDecorViewHeight) {
                            if (actualTask.getTaskType() > Task.IMAGE) {

                                ConstraintSet constraintSet = new ConstraintSet();
                                constraintSet.clone(constraintLayout);
                                constraintSet.connect(R.id.additional_task_text, ConstraintSet.START, R.id.textTaskLayout, ConstraintSet.START, (int) getResources().getDimension(R.dimen.margin_medium));
                                constraintSet.connect(R.id.additional_task_text, ConstraintSet.END, R.id.textTaskLayout, ConstraintSet.END, (int) getResources().getDimension(R.dimen.margin_medium));
                                constraintSet.connect(R.id.additional_task_text, ConstraintSet.TOP, R.id.banner, ConstraintSet.BOTTOM, (int) getResources().getDimension(R.dimen.default_margin));
                                constraintSet.applyTo(constraintLayout);
                            }
                            if (actualTask.getTaskType() != Task.FLUENCY || (actualTask.getTaskType() == Task.FLUENCY && !actualTask.hasEnded()))
                                actualTask.displayBanner();


                        }
                    }

                    // Save current decor view height for the next call.
                    lastVisibleDecorViewHeight = visibleDecorViewHeight;
                }
            }

        });
    }

    public String getName() {
        return name;
    }

    @Override
    public Locale getLanguage() {
        return language;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                loadFragment(fragments.get(index));

            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);

                ErrorHandler.getInstance().displayError(this, "TTS had to install the new language");
            }
        }
    }

    @Override
    public void onGetSuggestions(SuggestionsInfo[] results) {

    }

    @Override
    public void onGetSentenceSuggestions(SentenceSuggestionsInfo[] results) {
        for (SentenceSuggestionsInfo result : results) {
            int n = result.getSuggestionsCount();
            for (int i = 0; i < n; i++) {
                String suggestedWord = result.getSuggestionsInfoAt(i).getSuggestionAt(0);
                if (!suggestedWord.equals(wordToCheck)) {
                    typos++;
                }
            }
        }

    }

    @Override
    public int checkTypos(ArrayList<String> input) {
        typos = 0;

        TextServicesManager tsm =
                (TextServicesManager) getSystemService(TEXT_SERVICES_MANAGER_SERVICE);

        SpellCheckerSession session =
                tsm != null ? tsm.newSpellCheckerSession(null, language, this, true) : null;


        if (session != null) {
            for (String word : input) {
                wordToCheck = word;
                session.getSentenceSuggestions(
                        new TextInfo[]{new TextInfo(word)},
                        1
                );
            }

        }

        return typos;

    }

    private void showTestCompletedDialog() {
        Dialog builder = new Dialog(this);
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
            doubleBackToExitPressedOnce = true;
            onBackPressed();
        });

        ImageView speechTail = builder.findViewById(R.id.speechTail);
        speechTail.setVisibility(View.INVISIBLE);

        TextView textView = builder.findViewById(R.id.dialogText);
        textView.setText(R.string.test_completed);
        //textView.setGravity(Gravity.CENTER);


        LottieAnimationView animationView = builder.findViewById(R.id.motion);
        animationView.setAnimation("thumbs-up.json");
        animationView.setImageAssetsFolder("images");
        animationView.playAnimation();
        builder.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        }
        this.doubleBackToExitPressedOnce = true;

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

}
