package ugr.gbv.cognimobile.activities;

import android.content.res.ColorStateList;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.content.ContextCompat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.android.material.textfield.TextInputLayout;
import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.interfaces.LoadDialog;
import ugr.gbv.cognimobile.payload.request.SignupRequest;
import ugr.gbv.cognimobile.utilities.DataSender;
import ugr.gbv.cognimobile.utilities.ErrorHandler;

import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity implements LoadDialog {

    private boolean formHasError = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
        EditText editTextFirstName = findViewById(R.id.editTextRegistrationFirstName);
        EditText editTextLastName = findViewById(R.id.editTextRegistrationLastName);
        EditText editTextRegistrationEmail = findViewById(R.id.editTextRegistrationEmail);
        EditText editTextRegistrationUsername = findViewById(R.id.editTextRegistrationUsername);
        EditText editTextPassword = findViewById(R.id.editTextPassword);
        TextInputLayout placeHolderFirstName = findViewById(R.id.filledTextFieldRegistrationFirstName);
        TextInputLayout placeHolderLastName = findViewById(R.id.filledTextFieldRegistrationLastName);
        TextInputLayout placeHolderRegistrationEmail = findViewById(R.id.filledTextFieldRegistrationEmail);
        TextInputLayout placeHolderRegistrationUsername = findViewById(R.id.filledTextRegistrationUsername);
        TextInputLayout placeHolderPassword = findViewById(R.id.filledTextFieldPassword);
        CheckBox checkBox = findViewById(R.id.registerCheckBox);
        TextView textViewTermsAndConditions = findViewById(R.id.termsAndConditionText);
        TextView reminder = findViewById(R.id.expertReminder);
        Button registerButton = findViewById(R.id.register_button);
        Button userButton = findViewById(R.id.userRoleButton);
        Button expertButton = findViewById(R.id.expertRoleButton);

        AtomicReference<String> role = new AtomicReference<>();


        ErrorHandler.setCallback(this);

        userButton.setOnClickListener(view -> {
            expertButton.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.white));
            expertButton.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
            userButton.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
            userButton.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.white));
            reminder.setVisibility(View.GONE);
            role.set("USER");
        });

        expertButton.setOnClickListener(view -> {
            expertButton.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
            expertButton.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.white));
            userButton.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.white));
            userButton.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
            reminder.setVisibility(View.VISIBLE);
            role.set("MODERATOR");
        });

        userButton.performClick();

        editTextFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                placeHolderFirstName.setBoxStrokeColor(ContextCompat.getColor(getBaseContext(), R.color.gray));
                placeHolderFirstName.setDefaultHintTextColor(ColorStateList.valueOf(
                        ContextCompat.getColor(getBaseContext(), R.color.gray)));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        editTextLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                placeHolderLastName.setBoxStrokeColor(ContextCompat.getColor(getBaseContext(), R.color.gray));
                placeHolderLastName.setDefaultHintTextColor(ColorStateList.valueOf(
                        ContextCompat.getColor(getBaseContext(), R.color.gray)));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        editTextRegistrationEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                placeHolderRegistrationEmail.setBoxStrokeColor(ContextCompat.getColor(getBaseContext(), R.color.gray));
                placeHolderRegistrationEmail.setDefaultHintTextColor(ColorStateList.valueOf(
                        ContextCompat.getColor(getBaseContext(), R.color.gray)));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        editTextRegistrationUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                placeHolderRegistrationUsername.setBoxStrokeColor(ContextCompat.getColor(getBaseContext(), R.color.gray));
                placeHolderRegistrationUsername.setDefaultHintTextColor(ColorStateList.valueOf(
                        ContextCompat.getColor(getBaseContext(), R.color.gray)));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                placeHolderPassword.setBoxStrokeColor(ContextCompat.getColor(getBaseContext(), R.color.gray));
                placeHolderPassword.setDefaultHintTextColor(ColorStateList.valueOf(
                        ContextCompat.getColor(getBaseContext(), R.color.gray)));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            checkBox.setButtonTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(getBaseContext(), R.color.gray)));
        });

        textViewTermsAndConditions.setOnClickListener(v -> {
            Toast.makeText(this, "DISPLAY SERVER TERMS AND CONDITIONS", Toast.LENGTH_LONG).show();
        });

        registerButton.setOnClickListener(v -> {
            if(TextUtils.isEmpty(editTextFirstName.getText().toString())){
                formHasError = true;
                placeHolderFirstName
                        .setBoxStrokeColor(ContextCompat.getColor(getBaseContext(), R.color.highlight));
                placeHolderFirstName.setDefaultHintTextColor(ColorStateList.valueOf(
                        ContextCompat.getColor(getBaseContext(), R.color.highlight)));
                Toast.makeText(this,"First name is empty", Toast.LENGTH_LONG).show();
            }
            if(TextUtils.isEmpty(editTextLastName.getText().toString())){
                formHasError = true;
                placeHolderLastName
                        .setBoxStrokeColor(ContextCompat.getColor(getBaseContext(), R.color.highlight));
                placeHolderLastName.setDefaultHintTextColor(ColorStateList.valueOf(
                        ContextCompat.getColor(getBaseContext(), R.color.highlight)));
                Toast.makeText(this,"Last name is empty", Toast.LENGTH_LONG).show();
            }
            if(TextUtils.isEmpty(editTextRegistrationEmail.getText().toString())){
                formHasError = true;
                placeHolderRegistrationEmail
                        .setBoxStrokeColor(ContextCompat.getColor(getBaseContext(), R.color.highlight));
                placeHolderRegistrationEmail.setDefaultHintTextColor(ColorStateList.valueOf(
                        ContextCompat.getColor(getBaseContext(), R.color.highlight)));
                Toast.makeText(this,"Email is empty", Toast.LENGTH_LONG).show();
            }
            if(TextUtils.isEmpty(editTextRegistrationUsername.getText().toString())){
                formHasError = true;
                placeHolderRegistrationUsername
                        .setBoxStrokeColor(ContextCompat.getColor(getBaseContext(), R.color.highlight));
                placeHolderRegistrationUsername.setDefaultHintTextColor(ColorStateList.valueOf(
                        ContextCompat.getColor(getBaseContext(), R.color.highlight)));
                Toast.makeText(this,"Username is empty", Toast.LENGTH_LONG).show();
            }
            if(TextUtils.isEmpty(editTextPassword.getText().toString())){
                formHasError = true;
                placeHolderPassword
                        .setBoxStrokeColor(ContextCompat.getColor(getBaseContext(), R.color.highlight));
                placeHolderPassword.setDefaultHintTextColor(ColorStateList.valueOf(
                        ContextCompat.getColor(getBaseContext(), R.color.highlight)));
                Toast.makeText(this,"Password is empty", Toast.LENGTH_LONG).show();
            }
            else {
                Matcher m = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$")
                        .matcher(editTextPassword.getText().toString());
                if (!m.matches()) {
                    formHasError = true;
                    placeHolderPassword
                            .setBoxStrokeColor(ContextCompat.getColor(getBaseContext(), R.color.highlight));
                    placeHolderPassword.setDefaultHintTextColor(ColorStateList.valueOf(
                            ContextCompat.getColor(getBaseContext(), R.color.highlight)));
                    Toast.makeText(this, "Password Must Contain 8 Characters, One Uppercase, One Lowercase, " +
                            "One Number and One Special Case Character", Toast.LENGTH_LONG).show();
                }
            }
            if(!checkBox.isChecked()){
                formHasError = true;
                checkBox.setButtonTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(getBaseContext(), R.color.highlight)));
                Toast.makeText(this, "The Terms And Conditions must be accepted", Toast.LENGTH_LONG).show();
            }

            if(!formHasError){
                SignupRequest request = new SignupRequest();
                request.setFirstname(editTextFirstName.getText().toString());
                request.setLastname(editTextPassword.getText().toString());
                request.setEmail(editTextRegistrationEmail.getText().toString());
                request.getRoles().add(role.get());
                request.setPassword(editTextPassword.getText().toString());
                try {
                    DataSender.getInstance().postToServer(request, getApplicationContext(), "/api/auth/signup");
                } catch (JsonProcessingException e) {
                    ErrorHandler.displayError("Some error happened during the registration, please try again later.");
                }
            }
        });

    }

    @Override
    public void loadDialog(String message) {
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
            builder.setTitle(RegistrationActivity.this.getString(R.string.error_occurred));
            builder.setMessage(message);
            builder.setCancelable(false);
            builder.setPositiveButton(RegistrationActivity.this.getString(R.string.continue_next_task),
                    (dialog, which) -> dialog.dismiss());
            builder.show();
        });
    }
}
