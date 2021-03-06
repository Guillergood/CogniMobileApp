package ugr.gbv.cognimobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ugr.gbv.cognimobile.R;

import static ugr.gbv.cognimobile.qr_reader.ReadQR.INTENT_LINK_LABEL;

/**
 * Class that allows the user to type the study link
 */
public class WriteTestLink extends AppCompatActivity {

    private EditText editText;

    /**
     * OnCreate method to create the view and instantiate all the elements and put the info,
     *
     * @param savedInstanceState contains the most recent data from the activity.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_written_link);

        editText = findViewById(R.id.writtenLinkEditText);

        FloatingActionButton button = findViewById(R.id.writtenLinkButton);
        button.setOnClickListener(v -> sendBackLink());

    }

    /**
     * Method that puts the data of the link to be sent to the parent activity.
     */
    private void sendBackLink() {
        Intent data = getIntent();
        String link = editText.getText().toString();
        if (!link.isEmpty()) {
            data.putExtra(INTENT_LINK_LABEL, link);
            setResult(RESULT_OK, data);
            finish();
        } else {
            Toast.makeText(this, R.string.provide_link, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
        finish();
    }
}
