package ugr.gbv.cognimobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.database.CognimobilePreferences;
import ugr.gbv.cognimobile.fragments.ExpertTestFragment;
import ugr.gbv.cognimobile.fragments.SettingsFragments;
import ugr.gbv.cognimobile.interfaces.LoadDialog;
import ugr.gbv.cognimobile.interfaces.SettingsCallback;
import ugr.gbv.cognimobile.utilities.ErrorHandler;

public class ExpertActivity extends AppCompatActivity implements LoadDialog,
        NavigationBarView.OnItemSelectedListener,
        SettingsCallback {

    private Fragment actualFragment;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expert);
        ErrorHandler.setCallback(this);

        if (CognimobilePreferences.getFirstTimeLaunch(this)) {
            displayTutorialDialog();
            CognimobilePreferences.setFirstTimeLaunch(getApplicationContext(), false);
        }

        initBottomNavBar();

        actualFragment = new ExpertTestFragment();

        loadFragment();
    }


    private void displayTutorialDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.attention));
        builder.setMessage(getText(R.string.tutorial_message));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.go_on), (dialog, which) -> goToTutorial());
        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
            CognimobilePreferences.setFirstTimeLaunch(this, false);
            dialog.dismiss();
        });
        builder.show();

    }

    private void goToTutorial() {
        Intent intent = new Intent(this, TutorialTest.class);
        startActivity(intent);
    }

    private void loadFragment() {
        if (actualFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_expert, actualFragment)
                    .commit();
        }
    }

    @Override
    public void loadDialog(String message) {
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ExpertActivity.this);

            builder.setTitle(ExpertActivity.this.getString(R.string.error_occurred));
            builder.setMessage(message);
            builder.setCancelable(false);
            builder.setPositiveButton(ExpertActivity.this.getString(R.string.continue_next_task), (dialog, which) -> dialog.dismiss());
            builder.show();
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_tests) {
            actualFragment = new ExpertTestFragment();
            loadFragment();
        } else if (id == R.id.nav_settings) {
            actualFragment = new SettingsFragments(this);
            loadFragment();
        }
        return true;
    }

    /**
     * initBottomNavBar initializes the bottom navigation bar.
     */
    private void initBottomNavBar() {
        BottomNavigationView navigation = findViewById(R.id.navigation_expert);
        navigation.setOnItemSelectedListener(this);
    }

    @Override
    public void finishActivity(){
        finish();
    }

}
