package ugr.gbv.cognimobile.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.service.notification.StatusBarNotification;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.aware.Aware;
import com.aware.ui.PermissionsHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.database.CognimobilePreferences;
import ugr.gbv.cognimobile.fragments.SettingsFragments;
import ugr.gbv.cognimobile.fragments.StudyFragment;
import ugr.gbv.cognimobile.fragments.TestsFragment;
import ugr.gbv.cognimobile.interfaces.LoadDialog;
import ugr.gbv.cognimobile.interfaces.QRCallback;
import ugr.gbv.cognimobile.interfaces.TestClickHandler;
import ugr.gbv.cognimobile.qr_reader.ReadQR;
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
        QRCallback, TestClickHandler, LoadDialog {

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

        REQUIRED_PERMISSIONS.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        REQUIRED_PERMISSIONS.add(Manifest.permission.ACCESS_WIFI_STATE);
        REQUIRED_PERMISSIONS.add(Manifest.permission.CAMERA);
        REQUIRED_PERMISSIONS.add(Manifest.permission.BLUETOOTH);
        REQUIRED_PERMISSIONS.add(Manifest.permission.BLUETOOTH_ADMIN);
        REQUIRED_PERMISSIONS.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        REQUIRED_PERMISSIONS.add(Manifest.permission.ACCESS_FINE_LOCATION);
        REQUIRED_PERMISSIONS.add(Manifest.permission.READ_PHONE_STATE);
        REQUIRED_PERMISSIONS.add(Manifest.permission.GET_ACCOUNTS);
        REQUIRED_PERMISSIONS.add(Manifest.permission.WRITE_SYNC_SETTINGS);
        REQUIRED_PERMISSIONS.add(Manifest.permission.READ_SYNC_SETTINGS);
        REQUIRED_PERMISSIONS.add(Manifest.permission.READ_SYNC_STATS);
        REQUIRED_PERMISSIONS.add(Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            REQUIRED_PERMISSIONS.add(Manifest.permission.FOREGROUND_SERVICE);
        }


        ActivityCompat.requestPermissions(this,
                REQUIRED_PERMISSIONS.toArray(new String[0]),
                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

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

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissions.length > 0 && grantResults.length == permissions.length) {

            Aware.startAWARE(getApplicationContext());

            boolean isRunning = false;
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            assert mNotificationManager != null;
            StatusBarNotification[] notifications = mNotificationManager.getActiveNotifications();
            for (StatusBarNotification notification : notifications) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (notification.getNotification().getChannelId().equals(Aware.AWARE_NOTIFICATION_CHANNEL_GENERAL)) {
                        isRunning = true;
                    }
                } else {
                    isRunning = isMyServiceRunning();
                }
            }


            if (!isRunning) {
                Intent aware = new Intent(this, Aware.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(aware);
                } else {
                    startService(aware);
                }
            }
        }
    }

    /**
     * initBottomNavBar initializes the bottom navigation bar.
     */
    private void initBottomNavBar() {
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
    }

    /**
     * isMyServiceRunning checks if AWARE Framework is actually running or not.
     *
     * @return true if the service is running, false if not.
     */
    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (Aware.class.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
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

        switch (menuItem.getItemId()){
            case R.id.nav_studies:
                actualFragment = new StudyFragment(this);
                loadFragment();
                break;
            case R.id.nav_tests:
                actualFragment = new TestsFragment(this);
                loadFragment();
                break;
            case R.id.nav_settings:
                actualFragment = new SettingsFragments();
                loadFragment();
                break;
        }


        return true;


    }


    /**
     * hasUserConnectivity method checks the user connectivity
     *
     * @return true when the user has connection, false if not.
     */
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
                    Aware.joinStudy(this, link);
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
     * This method allows to go the ReadQR activity. Where the user is allowed to get the link
     * from the study. Also checks if the app has the appropriate permissions to do the task.
     */
    @Override
    public void goToQRActivity() {
        boolean permissionNotGranted = PermissionChecker.checkSelfPermission(this, Manifest.permission.CAMERA) != PermissionChecker.PERMISSION_GRANTED;

        if (permissionNotGranted) {
            ArrayList<String> permission = new ArrayList<>();
            permission.add(Manifest.permission.CAMERA);

            Intent permissions = new Intent(this, PermissionsHandler.class);
            permissions.putExtra(PermissionsHandler.EXTRA_REQUIRED_PERMISSIONS, permission);
            permissions.putExtra(PermissionsHandler.EXTRA_REDIRECT_ACTIVITY, getPackageName() + "/" + getPackageName() + ".qr_reader.ReadQR");
            permissions.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(permissions);
        } else {
            Intent qrCode = new Intent(MainActivity.this, ReadQR.class);
            qrCode.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(qrCode, LINK_CODE);
        }
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
            int count = 0;
            while (count == 0) {
                Cursor studies = Aware.getStudy(getApplicationContext(), "");
                count = studies.getCount();
                studies.close();
            }
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
}

