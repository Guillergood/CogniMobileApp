package ugr.gbv.cognimobile.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import ugr.gbv.cognimobile.database.CognimobilePreferences;
import ugr.gbv.cognimobile.fragments.ExpertTestFragment;
import ugr.gbv.cognimobile.fragments.SettingsFragments;
import ugr.gbv.cognimobile.fragments.StudyFragment;
import ugr.gbv.cognimobile.fragments.TestsFragment;
import ugr.gbv.cognimobile.interfaces.LoadDialog;
import ugr.gbv.cognimobile.interfaces.ServerLinkRetrieval;
import ugr.gbv.cognimobile.interfaces.SettingsCallback;
import ugr.gbv.cognimobile.interfaces.TestClickHandler;
import ugr.gbv.cognimobile.sync.WorkerManager;
import ugr.gbv.cognimobile.utilities.ErrorHandler;

/**
 * MainActivity.class is the core activity that links every component, allowing the user to
 * navigate between activities
 */
public class MainActivity extends AppCompatActivity
        implements NavigationBarView.OnItemSelectedListener,
        ServerLinkRetrieval,TestClickHandler, LoadDialog, SettingsCallback {

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

        WorkerManager.getInstance().initiateWorkers(getApplicationContext());

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
            actualFragment = new StudyFragment(this);
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
                            fragment.deleteTest(name);
                        }
                    }
                });

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
     * hasUserConnectivity method checks the user connectivity
     *
     * @return true when the user has connection, false if not.
     */
    @SuppressLint("MissingPermission")
    private boolean hasUserConnectivity() {
        // Checking internet connectivity
        ConnectivityManager connectivityMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (connectivityMgr != null) {
            activeNetwork = connectivityMgr.getActiveNetworkInfo();
        }
        return activeNetwork != null;
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
    public void loadDialog(String message) {
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setTitle(MainActivity.this.getString(R.string.error_occurred));
            builder.setMessage(message);
            builder.setCancelable(false);
            builder.setPositiveButton(MainActivity.this.getString(R.string.continue_next_task), (dialog, which) -> dialog.dismiss());
            builder.show();
        });
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
}

