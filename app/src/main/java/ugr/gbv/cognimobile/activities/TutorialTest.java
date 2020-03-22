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
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class TutorialTest extends AppCompatActivity {

    Dialog builder;
    TextView textView;
    FloatingActionButton nextButton;


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

    private void displayWelcomeDialog() {


        textView.setText(R.string.tutorial_welcome_meessage);


        nextButton.setOnClickListener(v -> {
            builder.dismiss();
            showTopArea();
        });

        builder.show();
    }

    private void showTopArea() {

        textView.setText(R.string.tutorial_top_message);

        nextButton.setOnClickListener(v -> {

            builder.dismiss();
            showCenterArea();

        });

        builder.show();

    }

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
