package ugr.gbv.cognimobile.activities;

import android.Manifest;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.providers.Aware_Provider;
import com.aware.ui.PermissionsHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.database.CognimobilePreferences;
import ugr.gbv.cognimobile.database.Provider;
import ugr.gbv.cognimobile.qr_reader.ReadQR;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener,
        NavigationView.OnNavigationItemSelectedListener{

    private final int QR_CODE = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 999;
    private ArrayList<String> REQUIRED_PERMISSIONS = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_actividad_principal);
        initBottomNavBar();

        Button test = findViewById(R.id.testButton);
        test.setOnClickListener(v -> irATest());


        if (CognimobilePreferences.getFirstTimeLaunch(this)) {
            displayTutorialDialog();
            CognimobilePreferences.setFirstTimeLaunch(getApplicationContext(), false);
        }

        /*REQUIRED_PERMISSIONS.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
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
                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);*/

    }

/*
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
    }*/

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
     * @param fragment Fragmento por el cual se va a cambiar la vista
     * @return booleano valor dependiendo de si es valido fragment o no devolverá "true" o "false"
     */
    private boolean loadFragment(Fragment fragment){

        boolean returnValue = false;
        if(fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contenedor_fragmento, fragment)
                    .commit();
            returnValue = true;
        }
        return returnValue;
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
                //readQR();
                //Aware.joinStudy(getApplicationContext(),"http://192.168.1.33:8080/index.php/1/4lph4num3ric");
                break;
            case R.id.nav_tests:
                //irATest();
                //dataTest();
                break;
            case R.id.nav_settings:
                //speechToText();
                //Aware.startBattery(getApplicationContext());
                break;

        }


        return true;



    }

    private void dataTest() {

        Thread task = new Thread(){
            @Override
            public void run() {
                HashMap<String, String> params = new HashMap<>();
                params.put("device_id",Aware.getSetting(getApplicationContext(), Aware_Preferences.DEVICE_ID));


                try {

                    String formattedData = formatData();

                    params.put("data",formattedData);

                    String connectionURLString = URLDecoder.decode(constructURL("participants","insert"),"ascii");


                    URL url = new URL(connectionURLString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.connect();


                    String postInformation = constructPostInformation(params);


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




    }

    private void queryData() {

        Thread task = new Thread(){
            @Override
            public void run() {
                HashMap<String, String> params = new HashMap<>();
                params.put("device_id",Aware.getSetting(getApplicationContext(), Aware_Preferences.DEVICE_ID));


                try {

                    String formattedData = formatData();

                    params.put("data",formattedData);

                    String connectionURLString = URLDecoder.decode(constructURL("participants","insert"),"ascii");


                    URL url = new URL(connectionURLString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.connect();


                    String postInformation = constructPostInformation(params);


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




    }

    private void sendData(HttpURLConnection conn,String postInformation) throws IOException {
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(postInformation);
        wr.flush();
        wr.close();
    }

    private String formatData() throws JSONException {

        double timestamp = System.currentTimeMillis();

        JSONObject jsonParam = new JSONObject();
        jsonParam.put("device_id", Aware.getSetting(getApplicationContext(), Aware_Preferences.DEVICE_ID));
        jsonParam.put("timestamp", timestamp);
        jsonParam.put("data", "1488873360");

        JSONArray realData = new JSONArray();
        realData.put(jsonParam);
        return realData.toString();
    }

    private String constructPostInformation(HashMap<String,String> params) {
        StringBuilder postVariables = new StringBuilder();
        int i = 0;

        for (String key : params.keySet()) {

            if (i != 0){
                postVariables.append("&");
            }
            postVariables.append(key).append("=")
                    .append(params.get(key));
            i++;
        }

        return postVariables.toString();
    }


    private String constructURL(String table,String command) {
        Cursor studies = Aware.getStudy(getApplicationContext(),"");
        studies.moveToFirst();
        String urlDb = studies.getString(studies.getColumnIndex(Aware_Provider.Aware_Studies.STUDY_URL));
        Uri studyUri = Uri.parse(urlDb);
        Uri.Builder urlBuilder = new Uri.Builder();
        List<String> paths = studyUri.getPathSegments();

        urlBuilder.scheme(studyUri.getScheme())
                .authority(studyUri.getAuthority());

        for (String path: paths) {
            urlBuilder.appendPath(path);
        }

        urlBuilder.appendPath(table)
                .appendPath(command);


        return urlBuilder.build().toString();
    }






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
        getContentResolver().insert(Provider.CONTENT_URI, new_data);

    }

    private void readQR() {

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
            Intent qrcode = new Intent(MainActivity.this, ReadQR.class);
            qrcode.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(qrcode,QR_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == QR_CODE){
            if(resultCode == RESULT_OK && data != null){
                String link = data.getStringExtra("link");
                if(link != null){
                    Aware.joinStudy(this, link);
                }
                Toast.makeText(this,"Joining to the study", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this,"Could not get the study link", Toast.LENGTH_LONG).show();
            }
        }

        super.onActivityResult(requestCode,resultCode,data);
    }


    private void irPreferenciasUsuario() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivityForResult(intent,1);
    }

    private void irATest() {
        Intent intent = new Intent(this, Test.class);
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







}

