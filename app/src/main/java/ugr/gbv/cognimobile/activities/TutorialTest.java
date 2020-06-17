package ugr.gbv.cognimobile.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ugr.gbv.cognimobile.R;

/**
 * Class that will allow to display the instructions about how to use the app to the user.
 */
public class TutorialTest extends AppCompatActivity {

    Dialog builder;
    TextView textView;
    FloatingActionButton nextButton;


    /**
     * OnCreate method to create the view and instantiate all the elements and put the info,
     *
     * @param savedInstanceState contains the most recent data from the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tutorial_task);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        builder = new Dialog(this);

        builder.setContentView(R.layout.tutorial_dialog);
        builder.setCancelable(false);

        textView = builder.findViewById(R.id.dialogText);
        nextButton = builder.findViewById(R.id.tutorial_next_button);


        displayWelcomeDialog();


    }

    /**
     * Displays the first message to the user.
     */
    private void displayWelcomeDialog() {
        textView.setText(R.string.tutorial_welcome_meessage);


        nextButton.setOnClickListener(v -> {
            builder.dismiss();
            showTopArea();
        });

        builder.show();
    }

    /**
     * Shows the message that describes the top area.
     */
    private void showTopArea() {

        textView.setText(R.string.tutorial_top_message);

        nextButton.setOnClickListener(v -> {

            builder.dismiss();
            showCenterArea();

        });

        builder.show();

    }

    /**
     * Shows the message that describes the center area.
     */
    private void showCenterArea() {

        CardView centerArea = findViewById(R.id.cardView);
        centerArea.setVisibility(View.VISIBLE);

        textView.setText(R.string.tutorial_center_message);


        nextButton.setOnClickListener(v -> {

            builder.dismiss();
            showBottomArea();

        });

        builder.show();

    }

    /**
     * Shows the message that describes the bottom area.
     */
    private void showBottomArea() {

        CardView bottomArea = findViewById(R.id.cardViewBottom);
        bottomArea.setVisibility(View.VISIBLE);


        textView.setText(R.string.tutorial_bottom_message);

        FloatingActionButton rightButton = findViewById(R.id.rightButton);

        nextButton.setOnClickListener(v -> {

            builder.dismiss();
            rightButton.setOnClickListener(rb -> {
                builder.dismiss();
                finish();
            });

        });


        builder.show();

    }


}
