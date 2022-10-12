package ugr.gbv.cognimobile.activities;

import android.content.res.ColorStateList;
import android.os.Bundle;

import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
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
    TextInputLayout filledTextFieldForgotPasswordEmail;
    private boolean formHasError = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);
        editTextForgotPasswordEmail = findViewById(R.id.editTextForgotPasswordEmail);
        filledTextFieldForgotPasswordEmail = findViewById(R.id.filledTextFieldForgotPasswordEmail);
        forgotPasswordButton = findViewById(R.id.forgot_password_button);

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
                filledTextFieldForgotPasswordEmail.setBoxStrokeColor(ContextCompat.getColor(getBaseContext(), R.color.gray));
                filledTextFieldForgotPasswordEmail.setDefaultHintTextColor(ColorStateList.valueOf(
                        ContextCompat.getColor(getBaseContext(), R.color.gray)));
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
    }
}
