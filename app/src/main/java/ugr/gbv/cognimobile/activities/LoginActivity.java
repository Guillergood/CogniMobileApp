package ugr.gbv.cognimobile.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;

import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.callbacks.LoginCallback;
import ugr.gbv.cognimobile.database.CognimobilePreferences;
import ugr.gbv.cognimobile.database.ContentProvider;
import ugr.gbv.cognimobile.interfaces.LoadDialog;
import ugr.gbv.cognimobile.payload.request.LoginRequest;
import ugr.gbv.cognimobile.payload.response.JwtResponse;
import ugr.gbv.cognimobile.utilities.ErrorHandler;

public class LoginActivity extends AppCompatActivity implements LoginCallback, LoadDialog {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        EditText editTextUsername = findViewById(R.id.editTextUsername);
        EditText editTextPassword = findViewById(R.id.editTextPassword);
        Button loginButton = findViewById(R.id.login_button);
        Button registerButton = findViewById(R.id.register_button);
        Button forgotPasswordButton = findViewById(R.id.forgot_password_button);

        ErrorHandler.setCallback(this);

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

        registerButton.setOnClickListener(view -> {
            goToRegistrationActivity();
        });

        forgotPasswordButton.setOnClickListener(view -> {
            goToForgotPasswordActivity();
        });
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
            if(jwt.getRoles().contains("MODERATOR")) {
                goToExpertActivity();
            }
            else if(jwt.getRoles().contains("USER")){
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
    public void loadDialog(String message) {
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
