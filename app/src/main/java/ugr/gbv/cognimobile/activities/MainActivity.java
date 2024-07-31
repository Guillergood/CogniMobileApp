package ugr.gbv.cognimobile.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.callbacks.CredentialsCallback;
import ugr.gbv.cognimobile.database.CognimobilePreferences;
import ugr.gbv.cognimobile.fragments.SettingsFragments;
import ugr.gbv.cognimobile.fragments.StudyFragment;
import ugr.gbv.cognimobile.fragments.TestsFragment;
import ugr.gbv.cognimobile.interfaces.*;
import ugr.gbv.cognimobile.sync.WorkerManager;
import ugr.gbv.cognimobile.utilities.DataSender;
import ugr.gbv.cognimobile.utilities.ErrorHandler;

/**
 * MainActivity.class is the core activity that links every component, allowing the user to
 * navigate between activities
 */
public class MainActivity extends AppCompatActivity
        implements NavigationBarView.OnItemSelectedListener,
        ServerLinkRetrieval,TestClickHandler, LoadDialog, SettingsCallback, CredentialsCallback {

    private static final String TEST_NAME = "name";
    private ActivityResultLauncher<Intent> testFinalization;
    private Fragment actualFragment;
    private Handler handler;

    /**
     * OnCreate method to create the view and instantiate all the elements and put the info,
     *
     * @param savedInstanceState contains the most recent data from the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_actividad_principal);
        initBottomNavBar();
        ErrorHandler.setCallback(this);

        new Handler(Looper.getMainLooper()).postDelayed(() -> runOnUiThread(() -> WorkerManager.getInstance().initiateWorkers(getApplicationContext())), 3000);

        if (CognimobilePreferences.getFirstTimeLaunch(this)) {
            displayTutorialDialog();
            CognimobilePreferences.setFirstTimeLaunch(getApplicationContext(), false);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(getString(R.string.notification_click))) {
                actualFragment = new TestsFragment(this);
            }
        } else {
            actualFragment = new StudyFragment();
        }

        loadFragment();

        testFinalization = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        if (result.getData() != null && result.getData().getExtras() != null) {
                            String name = result.getData().getStringExtra(TEST_NAME);
                            TestsFragment fragment = (TestsFragment) actualFragment;
                            fragment.updateTestDone(name);
                        }
                    }
                });

    }

    @Override
    protected void onResume() {
        ErrorHandler.setCallback(this);
        super.onResume();
    }

    /**
     * initBottomNavBar initializes the bottom navigation bar.
     */
    private void initBottomNavBar() {
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnItemSelectedListener(this);
    }

    /**
     * loadFragment allows to replace fragments from the actual activity.
     */
    private void loadFragment(){
        if(actualFragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, actualFragment)
                    .commit();
        }
    }


    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     * @return true to display the item as the selected item
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_studies) {
            actualFragment = new StudyFragment();
            loadFragment();
        } else if (id == R.id.nav_tests) {
            actualFragment = new TestsFragment(this);
            loadFragment();
        } else if (id == R.id.nav_settings) {
            actualFragment = new SettingsFragments(this);
            loadFragment();
        }
        return true;
    }



    /**
     * This method allows to handle the click from the {@link #onClick(int)}}, and starts the activity
     * Test, with the test information saved on the database.
     *
     * @param id id number of the test to be conducted.
     */
    private void goToTest(int id) {
        Intent intent = new Intent(this, Test.class);
        intent.putExtra("id", id);
        testFinalization.launch(intent);
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

    /**
     * This method allows to go the Tutorial activity.
     */
    private void goToTutorial() {
        Intent intent = new Intent(this, TutorialTest.class);
        startActivity(intent);
    }


    /**
     * This method handles the clicks on the TestFragment.
     */
    @Override
    public void onClick(int id) {
        goToTest(id);
    }

    /**
     * This method reload the UI when the user joins into a study.
     */
    private void reloadUiWhenJoined() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            initiateWorkerManager();
            runOnUiThread(this::reloadFragment);
        });

    }

    /**
     * This method reload the UI fragment when the user joins into a study.
     */
    @Override
    public void reloadFragment() {
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(actualFragment);
        ft.attach(actualFragment);
        ft.commit();
    }

    /**
     * Initiates the periodic tasks using {@link WorkerManager#initiateWorkers(Context)}.
     * It gives time to AWARE sets the information, and then run all the workers.
     */
    private void initiateWorkerManager() {
        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(
                () ->
                        runOnUiThread(() ->
                                WorkerManager.getInstance().initiateWorkers(getApplicationContext()))
                , getResources().getInteger(R.integer.default_time));

    }

    /**
     * Displays the error from {@link ErrorHandler#displayError(String)}}.
     */
    @Override
    public void loadDialog(String message, Object... args) {
        if (args != null && args.length > 0) {
            runOnUiThread(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(MainActivity.this.getString(R.string.error_occurred));
                builder.setMessage(message);
                builder.setCancelable(false);
                builder.setNegativeButton(MainActivity.this.getString(R.string.send_again),
                        (dialog, which) -> dialog.dismiss());
                builder.setPositiveButton(MainActivity.this.getString(R.string.send_again),
                        (dialog, which) -> DataSender.getInstance().postToServer(args[0],
                                getApplicationContext(),
                                (String) args[1],
                                this));
                builder.show();
            });
        }
        else{
            runOnUiThread(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(MainActivity.this.getString(R.string.error_occurred));
                builder.setMessage(message);
                builder.setCancelable(false);
                builder.setPositiveButton(MainActivity.this.getString(R.string.continue_next_task), (dialog, which) -> dialog.dismiss());
                builder.show();
            });
        }
    }

    @Override
    public void doLogout() {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * This method overrides {@link Activity#onStart}, adding the
     * {@link ErrorHandler#setCallback(LoadDialog)} routine to it.
     */
    @Override
    protected void onStart() {
        super.onStart();
        ErrorHandler.setCallback(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void goToChooseQrOrTextActivity() {
        Intent intent = new Intent(this, ServerUrlRetrieval.class);
        startActivity(intent);
    }

    @Override
    public void finishActivity(){
        finish();
    }

    @Override
    public void changeServer() {
        Intent i = new Intent(MainActivity.this, ServerUrlRetrieval.class);
        // set the new task and clear flags
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}

