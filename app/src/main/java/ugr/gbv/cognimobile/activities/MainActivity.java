package ugr.gbv.cognimobile.activities;

import android.Manifest;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
import com.aware.Battery;
import com.aware.ui.PermissionsHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
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
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 998;
    private static final String[] REQUIRED_PERMISSIONS ={(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            (Manifest.permission.ACCESS_WIFI_STATE),
            (Manifest.permission.CAMERA),
            (Manifest.permission.BLUETOOTH),
            (Manifest.permission.BLUETOOTH_ADMIN),
            (Manifest.permission.ACCESS_COARSE_LOCATION),
            (Manifest.permission.ACCESS_FINE_LOCATION),
            (Manifest.permission.READ_PHONE_STATE),
            (Manifest.permission.GET_ACCOUNTS),
            (Manifest.permission.WRITE_SYNC_SETTINGS),
            (Manifest.permission.READ_SYNC_SETTINGS),
            (Manifest.permission.READ_SYNC_STATS),
            (Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS),
            (Manifest.permission.FOREGROUND_SERVICE)};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        inicializaValoresMenuHamburguesa();

        inicializaMenuInferior();

        Button test = findViewById(R.id.testButton);
        test.setOnClickListener(v -> irATest());


        if (CognimobilePreferences.getFirstTimeLaunch(this)) {
            displayTutorialDialog();
            CognimobilePreferences.setFirstTimeLaunch(getApplicationContext(), false);
        }




        ActivityCompat.requestPermissions(this,
                REQUIRED_PERMISSIONS,
                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);




        


    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        if(permissions.length > 0 && grantResults.length == permissions.length){

            Aware.startAWARE(getApplicationContext());

            boolean isRunning = false;
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            assert mNotificationManager != null;
            StatusBarNotification[] notifications = mNotificationManager.getActiveNotifications();
            for (StatusBarNotification notification : notifications) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (notification.getNotification().getChannelId().equals(Aware.AWARE_NOTIFICATION_ID) ) {
                        isRunning = true;
                    }
                }
                else{
                    isRunning = isMyServiceRunning(Aware.class);
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

    private void inicializaMenuInferior() {
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    /**
     * Devuelve "true" si se ha podido hacer la transsicion de un fragmento a otro.
     * En otro caso "false"
     *
     * @param fragmento Fragmento por el cual se va a cambiar la vista
     * @return booleano valor dependiendo de si es valido fragmento o no devolverá "true" o "false"
     */
    private boolean cargarFragmento (Fragment fragmento){

        boolean devuelve = false;
        if(fragmento != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contenedor_fragmento, fragmento)
                    .commit();
            devuelve = true;
        }
        return devuelve;
    }


    /**
     * Manejando para ir cambiando de fragmento, en funcion de qué botón se pinche de la barra de
     * navegación inferior.
     *
     *
     * @param menuItem Elemento que se ha pinchado
     * @return booleano valor de la función cargarFragmento dependiendo de si es valido fragmento
     * o no devolverá "true" o "false"
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {




        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        switch (menuItem.getItemId()){
            case R.id.nav_gallery:
                //readQR();
                Aware.joinStudy(getApplicationContext(),"https://api.awareframework.com/index.php/webservice/index/2501/ZbTIjeyGPlxc");
                break;
            case R.id.nav_home:
                //irATest();
                comenzarBateria();
                break;
            case R.id.nav_slideshow:
                //speechToText();
                //Aware.startBattery(getApplicationContext());
                pararBateria();
                break;

        }


        return true;



    }


    private void comenzarBateria() {
        Aware.startBattery(this);
        Battery.setSensorObserver(new Battery.AWARESensorObserver() {
            @Override
            public void onBatteryChanged(ContentValues data) {
                Log.d("AWARE","CAMBIO LA BATERIA");
            }

            @Override
            public void onPhoneReboot() {

            }

            @Override
            public void onPhoneShutdown() {

            }

            @Override
            public void onBatteryLow() {
                Log.d("AWARE","BATERIA BAJA");
            }

            @Override
            public void onBatteryCharging() {
                Log.d("AWARE","BATERIA CARGANDO");
            }

            @Override
            public void onBatteryDischarging() {
                Log.d("AWARE","BATERIA DESCARGANDO");
            }
        });
    }



    private void pararBateria() {

        Aware.stopBattery(this);

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

        builder.setTitle(getString(R.string.alert));
        builder.setMessage(getText(R.string.tutorial_message));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.confirm), (dialog, which) -> goToTutorial());
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
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void inicializaValoresMenuHamburguesa(){
        NavigationView navigationView = findViewById(R.id.panel_lateral);
        navigationView.setNavigationItemSelectedListener(this);

/*        View headerView = navigationView.getHeaderView(0);

        TextView titulo = headerView.findViewById(R.id.titulo_hamburgesa);
        TextView descripcion = headerView.findViewById(R.id.descripcion_hamburgesa);*/
    }




}

