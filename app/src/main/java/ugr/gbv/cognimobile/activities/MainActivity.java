package ugr.gbv.cognimobile.activities;

import android.Manifest;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.ContentValues;
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
import com.aware.Aware_Preferences;
import com.aware.ui.PermissionsHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.UUID;

import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.database.CognimobilePreferences;
import ugr.gbv.cognimobile.database.Provider;
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

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener,
        NavigationView.OnNavigationItemSelectedListener,
        QRCallback, TestClickHandler, LoadDialog {

    private final int LINK_CODE = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 999;
    private ArrayList<String> REQUIRED_PERMISSIONS = new ArrayList<>();
    private Fragment actualFragment;

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


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if(permissions.length > 0 && grantResults.length == permissions.length){

            Aware.startAWARE(getApplicationContext());

            boolean isRunning = false;
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            assert mNotificationManager != null;
            StatusBarNotification[] notifications = mNotificationManager.getActiveNotifications();
            for (StatusBarNotification notification : notifications) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (notification.getNotification().getChannelId().equals(Aware.AWARE_NOTIFICATION_CHANNEL_GENERAL) ) {
                        isRunning = true;
                    }
                }
                else{
                    isRunning = isMyServiceRunning();
                }
            }


            if(!isRunning) {
                Intent aware = new Intent(this, Aware.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(aware);
                } else {
                    startService(aware);
                }
            }
        }
    }

    private void initBottomNavBar() {
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if(manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (Aware.class.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Devuelve "true" si se ha podido hacer la transsicion de un fragment a otro.
     * En otro caso "false"
     *
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
     * Manejando para ir cambiando de fragmento, en funcion de qué botón se pinche de la barra de
     * navegación inferior.
     *
     *
     * @param menuItem Elemento que se ha pinchado
     * @return booleano valor de la función loadFragment dependiendo de si es valido fragmento
     * o no devolverá "true" o "false"
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



    /*private void dataTest() {

        Thread task = new Thread(){
            @Override
            public void run() {
                HashMap<String, String> params = new HashMap<>();
                params.put("device_id",Aware.getSetting(getApplicationContext(), Aware_Preferences.DEVICE_ID));


                try {

                    String formattedData = formatData();

                    params.put("data",formattedData);

                    String connectionURLString = URLDecoder.decode(buildURL("participants","insert"),"ascii");


                    URL url = new URL(connectionURLString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.connect();


                    String postInformation = buildPostInformation(params);


                    sendData(conn,postInformation);


                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());

                    conn.disconnect();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };

        task.start();




    }*/







    private void crearDatos(){
        ContentValues new_data = new ContentValues();


        if (Aware.getSetting(getApplicationContext(), Aware_Preferences.DEVICE_ID).length() == 0) {
            UUID uuid = UUID.randomUUID();
            Aware.setSetting(getApplicationContext(), Aware_Preferences.DEVICE_ID, uuid.toString());

        }

        new_data.put(Provider.DEVICE_ID, Aware.getSetting(getApplicationContext(), Aware_Preferences.DEVICE_ID));
        new_data.put(Provider.TIMESTAMP, System.currentTimeMillis());
        //put the rest of the columns you defined

        //Insert the data to the ContentProvider
        getContentResolver().insert(Provider.CONTENT_URI_TESTS, new_data);

    }


    private boolean hasUserConnectivity() {
        // Checking internet connectivity
        ConnectivityManager connectivityMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (connectivityMgr != null) {
            activeNetwork = connectivityMgr.getActiveNetworkInfo();
        }
        return activeNetwork != null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == LINK_CODE){
            if(resultCode == RESULT_OK && data != null){
                String link = data.getStringExtra(INTENT_LINK_LABEL);
                if(link != null){
                    Aware.joinStudy(this, link);
                    if (hasUserConnectivity()) {
                        CognimobilePreferences.setHasUserJoinedStudy(this, true);
                        reloadUiWhenJoined();
                        Toast.makeText(this, R.string.toast_joining_study, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, R.string.toast_could_not_join_study, Toast.LENGTH_LONG).show();
                    }
                }

            }
            else {
                Toast.makeText(this, R.string.toast_could_not_join_study, Toast.LENGTH_LONG).show();
            }
        }

        super.onActivityResult(requestCode,resultCode,data);
    }


    private void goToTest(int id) {
        Intent intent = new Intent(this, Test.class);
        intent.putExtra("id", id);
        startActivity(intent);
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

    @Override
    public void onClick(int id) {
        goToTest(id);
    }


    private void reloadUiWhenJoined() {

        Handler handler = new Handler();
        handler.post(() -> {
            int count = 0;
            while (count == 0) {
                Cursor studies = Aware.getStudy(getApplicationContext(), "");
                count = studies.getCount();
            }
            runOnUiThread(this::initiateWorkerManager);
            runOnUiThread(this::reloadFragment);
        });


    }

    @Override
    public void reloadFragment() {
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(actualFragment);
        ft.attach(actualFragment);
        ft.commit();
    }

    private void initiateWorkerManager() {
        WorkerManager.getInstance().initiateWorkers(getApplicationContext());
    }

    @Override
    public void loadDialog(String message) {
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setTitle(MainActivity.this.getString(R.string.error_occurred));
            builder.setMessage(message);
            builder.setCancelable(false);
            builder.setPositiveButton(MainActivity.this.getString(R.string.continue_next_task), (dialog, which) -> {
                dialog.dismiss();
            });
            builder.show();
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        ErrorHandler.setCallback(this);
    }
}

