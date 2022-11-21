package ugr.gbv.cognimobile.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.content.ContextCompat;
import com.google.android.material.textfield.TextInputLayout;
import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.utilities.DataSender;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText editTextForgotPasswordEmail;
    Button forgotPasswordButton;
    Button goBackButton;
    TextView successText;
    TextInputLayout filledTextFieldForgotPasswordEmail;
    private boolean formHasError = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);
        editTextForgotPasswordEmail = findViewById(R.id.editTextForgotPasswordEmail);
        filledTextFieldForgotPasswordEmail = findViewById(R.id.filledTextFieldForgotPasswordEmail);
        forgotPasswordButton = findViewById(R.id.forgot_password_button);
        successText = findViewById(R.id.successResetPassword);
        goBackButton = findViewById(R.id.go_back_button);
        goBackButton.setOnClickListener(view -> {
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
        });

        forgotPasswordButton.setOnClickListener(v -> {
            if (TextUtils.isEmpty(editTextForgotPasswordEmail.getText().toString())) {
                formHasError = true;
                filledTextFieldForgotPasswordEmail
                        .setBoxStrokeColor(ContextCompat.getColor(getBaseContext(), R.color.highlight));
                filledTextFieldForgotPasswordEmail.setDefaultHintTextColor(ColorStateList.valueOf(
                        ContextCompat.getColor(getBaseContext(), R.color.highlight)));
                Toast.makeText(this, "Email is empty", Toast.LENGTH_LONG).show();
            }

            if(!formHasError){
                sendForgotPassword();
            }
        });

        editTextForgotPasswordEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (TextUtils.isEmpty(charSequence) || Patterns.EMAIL_ADDRESS.matcher(charSequence).matches()) {
                    filledTextFieldForgotPasswordEmail.setBoxStrokeColor(ContextCompat.getColor(getBaseContext(), R.color.gray));
                    filledTextFieldForgotPasswordEmail.setDefaultHintTextColor(ColorStateList.valueOf(
                            ContextCompat.getColor(getBaseContext(), R.color.gray)));
                    filledTextFieldForgotPasswordEmail.setHint("Email");
                } else {
                    filledTextFieldForgotPasswordEmail
                            .setBoxStrokeColor(ContextCompat.getColor(getBaseContext(), R.color.highlight));
                    filledTextFieldForgotPasswordEmail.setDefaultHintTextColor(ColorStateList.valueOf(
                            ContextCompat.getColor(getBaseContext(), R.color.highlight)));
                    filledTextFieldForgotPasswordEmail.setHint("Invalid email");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    void sendForgotPassword(){
        DataSender.getInstance()
                .postToServer("",getBaseContext(), "/api/auth/forgotPassword?email="+
                        editTextForgotPasswordEmail.getText().toString().trim(),null);

        editTextForgotPasswordEmail.setVisibility(View.GONE);
        filledTextFieldForgotPasswordEmail.setVisibility(View.GONE);
        forgotPasswordButton.setVisibility(View.GONE);
        successText.setVisibility(View.VISIBLE);
        goBackButton.setVisibility(View.VISIBLE);

    }
}
