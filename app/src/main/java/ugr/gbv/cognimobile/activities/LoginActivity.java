package ugr.gbv.cognimobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ugr.gbv.cognimobile.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        EditText editTextUsername = findViewById(R.id.editTextUsername);
        EditText editTextPassword = findViewById(R.id.editTextPassword);
        Button loginButton = findViewById(R.id.login_button);
        Button registerButton = findViewById(R.id.register_button);
        Button forgotPasswordButton = findViewById(R.id.forgot_password_button);

        loginButton.setOnClickListener(view -> {
            if(TextUtils.isEmpty(editTextUsername.getText().toString())){
                Toast.makeText(this,"Username is empty", Toast.LENGTH_LONG).show();
            }
            else if(TextUtils.isEmpty(editTextPassword.getText().toString())){
                Toast.makeText(this,"Password is empty", Toast.LENGTH_LONG).show();
            }
            //TODO CALL TO THE SERVER
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
}
