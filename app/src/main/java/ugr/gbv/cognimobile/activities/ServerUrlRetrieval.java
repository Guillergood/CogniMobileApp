package ugr.gbv.cognimobile.activities;

import static ugr.gbv.cognimobile.qr_reader.ReadQR.INTENT_LINK_LABEL;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
import androidx.core.content.PermissionChecker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.database.CognimobilePreferences;
import ugr.gbv.cognimobile.dto.QrDTO;
import ugr.gbv.cognimobile.interfaces.LoadDialog;
import ugr.gbv.cognimobile.interfaces.ServerLinkRetrieval;
import ugr.gbv.cognimobile.qr_reader.ReadQR;
import ugr.gbv.cognimobile.utilities.ContextDataRetriever;
import ugr.gbv.cognimobile.utilities.CustomObjectMapper;
import ugr.gbv.cognimobile.utilities.ErrorHandler;

public class ServerUrlRetrieval extends AppCompatActivity implements ServerLinkRetrieval, LoadDialog {

    private final int LINK_CODE = 1;
    private final int CAMERA_PERMISSION_CODE = 3;
    private ActivityResultLauncher<Intent> qrTextRetrieval;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_test_link_retrieval);
        LinearLayout sendUrlButton = findViewById(R.id.typeUrlContainer);
        FloatingActionButton sendUrlFabButton = findViewById(R.id.typeUrlButton);
        LinearLayout qrScannerButton = findViewById(R.id.qrScannerContainer);
        FloatingActionButton qrScannerFabButton = findViewById(R.id.qrScannerButton);
        EditText inputUrlText = findViewById(R.id.editTextServerUrl);

        ErrorHandler.setCallback(this);

        sendUrlButton.setOnClickListener(view -> {
            CognimobilePreferences.setServerUrl(this, inputUrlText.getText().toString());
            goToLoginActivity(null);
        });

        sendUrlFabButton.setOnClickListener(view -> {
            CognimobilePreferences.setServerUrl(this, inputUrlText.getText().toString());
            goToLoginActivity(null);
        });

        qrScannerButton.setOnClickListener(view -> {
            goToChooseQrOrTextActivity();
        });

        qrScannerFabButton.setOnClickListener(view -> {
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
    }

    private void goToLoginActivity(QrDTO extraData) {
        Intent intent = new Intent(this, LoginActivity.class);
        if(extraData != null) {
            intent.putExtra("studyName", extraData.getStudy().getName());
        }
        startActivity(intent);
    }

    /**
     * onRequestPermissionsResult method allows to catch information from a requested permission.
     * Using the {@link #requestPermissions(String[], int)} method:
     *
     * @param requestCode  Application specific request code to match with a result
     *                     reported to {@link #onRequestPermissionsResult(int, String[], int[])}.
     *                     Should be >= 0.
     * @param permissions  The requested permissions. Must be non-null and not empty.
     * @param grantResults The requested permissions granted. Must be non-null and not empty.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        //        if (requestCode == CAMERA_PERMISSION_CODE) {
        if (requestCode == CAMERA_PERMISSION_CODE) {

            // Checking whether user granted the permission or not.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                goToQrReadActivity();
            }
            else {
                Toast.makeText(ServerUrlRetrieval.this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    /**
     * This method allows to go the ReadQR activity. Where the user is allowed to get the link
     * from the study. Also checks if the app has the appropriate permissions to do the task.
     */
    @Override
    public void goToChooseQrOrTextActivity() {
        boolean permissionNotGranted = PermissionChecker.checkSelfPermission(this, Manifest.permission.CAMERA) != PermissionChecker.PERMISSION_GRANTED;
        if (permissionNotGranted) {
            ActivityCompat.requestPermissions(ServerUrlRetrieval.this, new String[] { Manifest.permission.CAMERA }, CAMERA_PERMISSION_CODE);
        } else {
            goToQrReadActivity();
        }
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
    public void loadDialog(String message) {
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
