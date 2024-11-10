package ugr.gbv.cognimobile.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
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

import com.google.android.material.textfield.TextInputLayout;
import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.callbacks.CredentialsCallback;
import ugr.gbv.cognimobile.database.CognimobilePreferences;
import ugr.gbv.cognimobile.interfaces.LoadDialog;
import ugr.gbv.cognimobile.payload.request.SignupRequest;
import ugr.gbv.cognimobile.utilities.DataSender;
import ugr.gbv.cognimobile.utilities.ErrorHandler;

import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity implements LoadDialog, CredentialsCallback {


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
		TextView rightTextPassword = findViewById(R.id.rightTextPassword);
		Button registerButton = findViewById(R.id.register_button);
		Button userButton = findViewById(R.id.userRoleButton);
		Button expertButton = findViewById(R.id.expertRoleButton);

		AtomicReference<String> role = new AtomicReference<>();

		ErrorHandler.setLoadDialogCallback(this);

		userButton.setOnClickListener(view -> {
			expertButton.setBackgroundColor(ContextCompat.getColor(getBaseContext(),
					R.color.white));
			expertButton.setTextColor(ContextCompat.getColor(getBaseContext(),
					R.color.colorPrimary));
			userButton.setBackgroundColor(ContextCompat.getColor(getBaseContext(),
					R.color.colorPrimary));
			userButton.setTextColor(ContextCompat.getColor(getBaseContext(),
					R.color.white));
			reminder.setVisibility(View.GONE);
			role.set("USER");
		});

		expertButton.setOnClickListener(view -> {
			expertButton.setBackgroundColor(ContextCompat.getColor(getBaseContext(),
					R.color.colorPrimary));
			expertButton.setTextColor(ContextCompat.getColor(getBaseContext(),
					R.color.white));
			userButton.setBackgroundColor(ContextCompat.getColor(getBaseContext(),
					R.color.white));
			userButton.setTextColor(ContextCompat.getColor(getBaseContext(),
					R.color.colorPrimary));
			reminder.setVisibility(View.VISIBLE);
			role.set("EXPERT");
		});

		userButton.performClick();

		TextWatcher textWatcher = new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				placeHolderFirstName.setBoxStrokeColor(ContextCompat.getColor(getBaseContext(),
						R.color.gray));
				placeHolderFirstName.setDefaultHintTextColor(ColorStateList.valueOf(ContextCompat.getColor(getBaseContext(),
						R.color.gray)));
			}

			@Override
			public void afterTextChanged(Editable editable) {

			}
		};

		editTextFirstName.addTextChangedListener(textWatcher);
		editTextLastName.addTextChangedListener(textWatcher);
		editTextRegistrationEmail.addTextChangedListener(textWatcher);
		editTextRegistrationUsername.addTextChangedListener(textWatcher);
		editTextPassword.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				Matcher m = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$")
						.matcher(charSequence.toString());
				if (!m.matches()) {
					placeHolderPassword.setBoxStrokeColor(ContextCompat.getColor(getBaseContext(),
							R.color.highlight));
					placeHolderPassword.setDefaultHintTextColor(ColorStateList.valueOf(ContextCompat.getColor(getBaseContext(),
							R.color.highlight)));
					rightTextPassword.setVisibility(View.VISIBLE);
				} else {
					placeHolderPassword.setBoxStrokeColor(ContextCompat.getColor(getBaseContext(),
							R.color.gray));
					placeHolderPassword.setDefaultHintTextColor(ColorStateList.valueOf(ContextCompat.getColor(getBaseContext(),
							R.color.gray)));
					rightTextPassword.setVisibility(View.GONE);
				}
			}

			@Override
			public void afterTextChanged(Editable editable) {

			}
		});

		checkBox.setOnCheckedChangeListener((compoundButton, b) -> checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(getBaseContext(),
                R.color.gray))));

		textViewTermsAndConditions.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(CognimobilePreferences.getServerUrl(this).replace("/backend",
                        "/terms")))));

		registerButton.setOnClickListener(v -> {
			StringBuilder builder = new StringBuilder();
			if (TextUtils.isEmpty(editTextFirstName.getText().toString())) {
				placeHolderFirstName.setBoxStrokeColor(ContextCompat.getColor(getBaseContext(),
						R.color.highlight));
				placeHolderFirstName.setDefaultHintTextColor(ColorStateList.valueOf(ContextCompat.getColor(getBaseContext(),
						R.color.highlight)));
				builder.append("First name is empty").append('\n');
			}
			if (TextUtils.isEmpty(editTextLastName.getText().toString())) {
				placeHolderLastName.setBoxStrokeColor(ContextCompat.getColor(getBaseContext(),
						R.color.highlight));
				placeHolderLastName.setDefaultHintTextColor(ColorStateList.valueOf(ContextCompat.getColor(getBaseContext(),
						R.color.highlight)));
				builder.append("Last name is empty").append('\n');
			}
			if (TextUtils.isEmpty(editTextRegistrationEmail.getText().toString())) {
				placeHolderRegistrationEmail.setBoxStrokeColor(ContextCompat.getColor(getBaseContext(),
						R.color.highlight));
				placeHolderRegistrationEmail.setDefaultHintTextColor(ColorStateList.valueOf(ContextCompat.getColor(getBaseContext(),
						R.color.highlight)));
				builder.append("Email is empty").append('\n');
			}
			if (TextUtils.isEmpty(editTextRegistrationUsername.getText().toString())) {
				placeHolderRegistrationUsername.setBoxStrokeColor(ContextCompat.getColor(getBaseContext(),
						R.color.highlight));
				placeHolderRegistrationUsername.setDefaultHintTextColor(ColorStateList.valueOf(ContextCompat.getColor(getBaseContext(),
						R.color.highlight)));
				builder.append("Username is empty").append('\n');
			}
			if (TextUtils.isEmpty(editTextPassword.getText().toString())) {
				placeHolderPassword.setBoxStrokeColor(ContextCompat.getColor(getBaseContext(),
						R.color.highlight));
				placeHolderPassword.setDefaultHintTextColor(ColorStateList.valueOf(ContextCompat.getColor(getBaseContext(),
						R.color.highlight)));
				builder.append("Password is empty").append('\n');
			} else {
				Matcher m = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$")
						.matcher(editTextPassword.getText().toString());
				if (!m.matches()) {
					placeHolderPassword.setBoxStrokeColor(ContextCompat.getColor(getBaseContext(),
							R.color.highlight));
					placeHolderPassword.setDefaultHintTextColor(ColorStateList.valueOf(ContextCompat.getColor(getBaseContext(),
							R.color.highlight)));
					builder.append("Password Must Contain 8 Characters And Less Than 20, One Uppercase, One Lowercase, One Number and One Special Case Character").append('\n');
				}
			}
			if (!checkBox.isChecked()) {
				checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(getBaseContext(),
						R.color.highlight)));
				builder.append("The Terms And Conditions must be accepted").append('\n');
			}

			if (builder.length() == 0) {
				SignupRequest request = new SignupRequest();
				request.setFirstname(editTextFirstName.getText().toString());
				request.setLastname(editTextLastName.getText().toString());
				request.setUsername(editTextRegistrationUsername.getText().toString());
				request.setEmail(editTextRegistrationEmail.getText().toString());
				request.getRoles().add(role.get());
				request.setPassword(editTextPassword.getText().toString());
				DataSender.getInstance().postToServer(request,
						getApplicationContext(),
						"/api/auth/signup",
						this);
				onBackPressed();
			}
			else {
				loadDialog(builder.toString());
			}
		});

	}

	@Override
	public void loadDialog(String message, Object... args) {
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

	@Override
	public void doLogout() {

	}
}
