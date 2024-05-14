package ugr.gbv.cognimobile.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.database.CognimobilePreferences;
import ugr.gbv.cognimobile.dto.QrDTO;
import ugr.gbv.cognimobile.interfaces.LoadDialog;
import ugr.gbv.cognimobile.interfaces.ServerLinkRetrieval;
import ugr.gbv.cognimobile.qr_reader.ReadQR;
import ugr.gbv.cognimobile.utilities.ContextDataRetriever;
import ugr.gbv.cognimobile.utilities.ErrorHandler;


import static ugr.gbv.cognimobile.qr_reader.ReadQR.INTENT_LINK_LABEL;

public class ServerUrlRetrieval extends AppCompatActivity implements ServerLinkRetrieval, LoadDialog {

    private final int LINK_CODE = 1;
    private ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private ActivityResultLauncher<Intent> qrTextRetrieval;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_test_link_retrieval);
        initializeComponents();
        setupPermissionRequestLauncher();
    }

    private void initializeComponents() {
        LinearLayout sendUrlButton = findViewById(R.id.typeUrlContainer);
        FloatingActionButton sendUrlFabButton = findViewById(R.id.typeUrlButton);
        LinearLayout qrScannerButton = findViewById(R.id.qrScannerContainer);
        FloatingActionButton qrScannerFabButton = findViewById(R.id.qrScannerButton);
        EditText inputUrlText = findViewById(R.id.editTextServerUrl);
        TextInputLayout placeHolderInput = findViewById(R.id.filledTextFieldServerUrl);

        inputUrlText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {
                if(Patterns.WEB_URL.matcher(charSequence).matches()) {
                    placeHolderInput.setBoxStrokeColor(ContextCompat.getColor(getBaseContext(),
                            R.color.gray));
                    placeHolderInput.setDefaultHintTextColor(ColorStateList.valueOf(ContextCompat.getColor(getBaseContext(),
                            R.color.gray)));
                    placeHolderInput.setHint("Url");
                    sendUrlButton.setClickable(true);
                    sendUrlFabButton.setClickable(true);
                }
                else{
                    placeHolderInput.setBoxStrokeColor(ContextCompat.getColor(getBaseContext(),
                            R.color.vivid_red));
                    placeHolderInput.setDefaultHintTextColor(ColorStateList.valueOf(ContextCompat.getColor(getBaseContext(),
                            R.color.vivid_red)));
                    placeHolderInput.setHint("Invalid url");
                    sendUrlButton.setClickable(false);
                    sendUrlFabButton.setClickable(false);
                }

            }

            @Override
            public void afterTextChanged(final Editable editable) {

            }
        });

        ErrorHandler.setCallback(this);

        sendUrlButton.setOnClickListener(view -> {
            CognimobilePreferences.setServerUrl(this, inputUrlText.getText().toString());
            goToLoginActivity(null);
        });

        sendUrlFabButton.setOnClickListener(view -> {
            CognimobilePreferences.setServerUrl(this, inputUrlText.getText().toString());
            goToLoginActivity(null);
        });

        sendUrlButton.setClickable(false);
        sendUrlFabButton.setClickable(false);

        qrScannerButton.setOnClickListener(view -> {
            goToChooseQrOrTextActivity();
        });

        qrTextRetrieval = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        if (result.getData() != null && result.getData().getExtras() != null) {
                            ObjectMapper mapper = new ObjectMapper();
                            QrDTO data;
                            try {
                                data = mapper.readValue(result.getData().getExtras()
                                        .getString(INTENT_LINK_LABEL), QrDTO.class);
                                CognimobilePreferences.setServerUrl(this,
                                        ContextDataRetriever.fixUrl(data.getUrl()));
                                goToLoginActivity(data);
                            } catch (JsonProcessingException e) {
                                ErrorHandler.displayError("Some error happened processing the QR Code");
                            }
                        }
                    }
                });

        qrScannerFabButton.setOnClickListener(view -> {
            goToChooseQrOrTextActivity();
        });
    }

    private void setupPermissionRequestLauncher() {
        requestCameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        goToQrReadActivity();
                    } else {
                        Toast.makeText(ServerUrlRetrieval.this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void goToLoginActivity(QrDTO extraData) {
        Intent intent = new Intent(this, LoginActivity.class);
        if(extraData != null && extraData.getStudy() != null) {
            intent.putExtra("studyName", extraData.getStudy().getName());
        }
        startActivity(intent);
    }


    /**
     * This method allows to go the ReadQR activity. Where the user is allowed to get the link
     * from the study. Also checks if the app has the appropriate permissions to do the task.
     */
    @Override
    public void goToChooseQrOrTextActivity() {
        requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
    }

    private void goToQrReadActivity() {
        Intent qrCode = new Intent(ServerUrlRetrieval.this, ReadQR.class);
        qrCode.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        qrTextRetrieval.launch(qrCode);
    }

    /**
     * onActivityResult method allows to catch information from a child activity.
     * Using the {@link #setResult(int)} , {@link #setResult(int, Intent)} methods:
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == LINK_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                String link = data.getStringExtra(INTENT_LINK_LABEL);
                if (link != null) {
                    //TODO JOIN STUDY TO THE NEW SERVER


                }

            } else {
                Toast.makeText(this, R.string.toast_could_not_join_study, Toast.LENGTH_LONG).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void loadDialog(String message, Object... args) {
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ServerUrlRetrieval.this);

            builder.setTitle(ServerUrlRetrieval.this.getString(R.string.error_occurred));
            builder.setMessage(message);
            builder.setCancelable(false);
            builder.setPositiveButton(ServerUrlRetrieval.this.getString(R.string.continue_next_task), (dialog, which) -> dialog.dismiss());
            builder.show();
        });
    }
}
