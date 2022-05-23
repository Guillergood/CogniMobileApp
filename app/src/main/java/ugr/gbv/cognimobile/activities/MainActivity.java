package ugr.gbv.cognimobile.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.database.CognimobilePreferences;
import ugr.gbv.cognimobile.fragments.SettingsFragments;
import ugr.gbv.cognimobile.fragments.StudyFragment;
import ugr.gbv.cognimobile.fragments.TestsFragment;
import ugr.gbv.cognimobile.interfaces.LoadDialog;
import ugr.gbv.cognimobile.interfaces.ServerLinkRetrieval;
import ugr.gbv.cognimobile.interfaces.TestClickHandler;
import ugr.gbv.cognimobile.sync.WorkerManager;
import ugr.gbv.cognimobile.utilities.ErrorHandler;

import static ugr.gbv.cognimobile.qr_reader.ReadQR.INTENT_LINK_LABEL;

/**
 * MainActivity.class is the core activity that links every component, allowing the user to
 * navigate between activities
 */
public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener,
        NavigationView.OnNavigationItemSelectedListener,
        ServerLinkRetrieval,TestClickHandler, LoadDialog {

    private static final String TEST_NAME = "name";
    private final int LINK_CODE = 1;
    private final int TEST_CODE = 2;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 999;
    private final ArrayList<String> REQUIRED_PERMISSIONS = new ArrayList<>();
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

    }

    /**
     * onRequestPermissionsResult method allows to catch information from a requested permission.
     * Using the {@link #requestPermissions(String[], int)} method:
     *
     * @param requestCode  Application specific request code to match with a result
     *                     reported to {@link #onRequestPermissionsResult(int, String[], int[])}.
     *                     Should be >= 0.
     * @param permissions  The requested permissions. Must be non-null and not empty.
     * @param grantResults The requested permissions granted. Must be non-null and not empty.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        //        if (requestCode == CAMERA_PERMISSION_CODE) {
        if (requestCode == 3) {

            // Checking whether user granted the permission or not.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Showing the toast message
                Toast.makeText(MainActivity.this, "Camera Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(MainActivity.this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
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
     * @param menuItem The selected item
     * @return true to display the item as the selected item
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.nav_studies) {
            actualFragment = new StudyFragment(this);
            loadFragment();
        } else if (id == R.id.nav_tests) {
            actualFragment = new TestsFragment(this);
            loadFragment();
        } else if (id == R.id.nav_settings) {
            actualFragment = new SettingsFragments();
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
     * onActivityResult method allows to catch information from a child activity.
     * Using the {@link #setResult(int)} , {@link #setResult(int, Intent)} methods:
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     *                    <p>
     *                    It catches the link url to be consumed by AWARE.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == LINK_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                String link = data.getStringExtra(INTENT_LINK_LABEL);
                if (link != null) {
                    //TODO JOIN STUDY TO THE NEW SERVER

                    if (hasUserConnectivity()) {
                        CognimobilePreferences.setHasUserJoinedStudy(this, true);
                        reloadUiWhenJoined();
                        Toast.makeText(this, R.string.toast_joining_study, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, R.string.toast_could_not_join_study, Toast.LENGTH_LONG).show();
                    }
                }

            } else {
                Toast.makeText(this, R.string.toast_could_not_join_study, Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == TEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                String name = data.getStringExtra(TEST_NAME);
                TestsFragment fragment = (TestsFragment) actualFragment;
                fragment.deleteTest(name);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
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
        startActivityForResult(intent, TEST_CODE);
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
        Handler handler = new Handler();
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
        handler = new Handler();
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
}

