package ugr.gbv.cognimobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.callbacks.LoginCallback;
import ugr.gbv.cognimobile.database.CognimobilePreferences;
import ugr.gbv.cognimobile.database.ContentProvider;
import ugr.gbv.cognimobile.dto.StudyEnrollRequest;
import ugr.gbv.cognimobile.interfaces.LoadDialog;
import ugr.gbv.cognimobile.payload.request.LoginRequest;
import ugr.gbv.cognimobile.payload.response.JwtResponse;
import ugr.gbv.cognimobile.utilities.DataSender;
import ugr.gbv.cognimobile.utilities.ErrorHandler;

public class LoginActivity extends AppCompatActivity implements LoginCallback, LoadDialog {

    private String studyName;


    @Override
    protected void onSaveInstanceState(@NonNull final Bundle outState) {
        outState.putString("studyName", studyName);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        EditText editTextUsername = findViewById(R.id.editTextUsername);
        EditText editTextPassword = findViewById(R.id.editTextPassword);
        Button loginButton = findViewById(R.id.login_button);
        Button registerButton = findViewById(R.id.register_button);
        Button forgotPasswordButton = findViewById(R.id.forgot_password_button);
        ImageButton backButton = findViewById(R.id.backButton);
        Button backButtonText = findViewById(R.id.backButtonText);

        if(savedInstanceState != null) {
            studyName = savedInstanceState.getString("studyName");
        }
        if(getIntent().hasExtra("studyName")){
            studyName = getIntent().getStringExtra("studyName");
        }

        ErrorHandler.setCallback(this);

        backButton.setOnClickListener( view -> goBack());

        backButtonText.setOnClickListener( view -> goBack());

        loginButton.setOnClickListener(view -> {
            if(TextUtils.isEmpty(editTextUsername.getText().toString())){
                Toast.makeText(this,"Username is empty", Toast.LENGTH_LONG).show();
            }
            else if(TextUtils.isEmpty(editTextPassword.getText().toString())){
                Toast.makeText(this,"Password is empty", Toast.LENGTH_LONG).show();
            }

            ContentProvider.getInstance().doLogin(this,
                    new LoginRequest(editTextUsername.getText().toString(),
                            editTextPassword.getText().toString()),this);
        });

        registerButton.setOnClickListener(view -> goToRegistrationActivity());

        forgotPasswordButton.setOnClickListener(view -> goToForgotPasswordActivity());
    }

    private void goBack() {
        Intent i = new Intent(LoginActivity.this, ServerUrlRetrieval.class);
        // set the new task and clear flags
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    private void goToForgotPasswordActivity() {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);

    }

    private void goToRegistrationActivity() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);

    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void loginStored() {
        ObjectMapper objectMapper = new ObjectMapper();
        JwtResponse jwt;
        try {
            jwt = objectMapper.readValue(CognimobilePreferences.getLogin(this), JwtResponse.class);
            if(jwt.getRoles().contains("EXPERT")) {
                goToExpertActivity();
            }
            else if(jwt.getRoles().contains("USER")){
                if(!TextUtils.isEmpty(studyName)){
                    StudyEnrollRequest studyEnrollRequest = new StudyEnrollRequest();
                    studyEnrollRequest.setStudyName(studyName);
                    DataSender.getInstance().enrollInStudy(studyEnrollRequest,getBaseContext());
                }
                goToMainActivity();
            }
            else{
                ErrorHandler.displayError("With this user you are not allowed to use the app. Sorry.");
            }
        } catch (JsonProcessingException e) {
            ErrorHandler.displayError("Some error happened when trying to login, please try again.");
        }
    }

    private void goToExpertActivity() {
        Intent intent = new Intent(this, ExpertActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void loadDialog(String message, Object... args) {
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

            builder.setTitle(LoginActivity.this.getString(R.string.error_occurred));
            builder.setMessage(message);
            builder.setCancelable(false);
            builder.setPositiveButton(LoginActivity.this.getString(R.string.continue_next_task), (dialog, which) -> dialog.dismiss());
            builder.show();
        });
    }
}
