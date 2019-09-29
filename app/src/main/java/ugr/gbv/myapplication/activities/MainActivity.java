package ugr.gbv.myapplication.activities;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.aware.Aware;
import com.aware.ui.PermissionsHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Locale;

import ugr.gbv.myapplication.R;
import ugr.gbv.myapplication.qr_reader.ReadQR;
import ugr.gbv.myapplication.utilities.ExampleService;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener,
        NavigationView.OnNavigationItemSelectedListener{

    private final int QR_CODE = 1;
    private final int STT_CODE = 2;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        inicializaValoresMenuHamburguesa();

        inicializaMenuInferior();

        //TODO AÑADIR LA NOTIFICACION Y SI ESTÁ ACTIVA NO EMPEZAR EL SERVICIO

        //startService();
        Context context = getApplicationContext();
        Aware.startAWARE(context); //initialise core AWARE service

        /*Aware.setSetting(context, Aware_Preferences.FREQUENCY_ACCELEROMETER, 200000); //20Hz
        Aware.setSetting(context, Aware_Preferences.THRESHOLD_ACCELEROMETER, 0.02f); // [x,y,z] > 0.02 to log

        Aware.startAccelerometer(this);

        Accelerometer.setSensorObserver(new Accelerometer.AWARESensorObserver() {
            @Override
            public void onAccelerometerChanged(ContentValues data) {
                double x = data.getAsDouble(Accelerometer_Provider.Accelerometer_Data.VALUES_0);
                double y = data.getAsDouble(Accelerometer_Provider.Accelerometer_Data.VALUES_1);
                double z = data.getAsDouble(Accelerometer_Provider.Accelerometer_Data.VALUES_2);

                println("x = "+x+" y = "+y+" z = "+z);
            }
        });*/

/*
        boolean permissionNotGranted = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            permissionNotGranted = PermissionChecker.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PermissionChecker.PERMISSION_GRANTED;


            if (permissionNotGranted) {
                ArrayList<String> permission = new ArrayList<>();
                permission.add(Manifest.permission.FOREGROUND_SERVICE);

                Intent permissions = new Intent(this, PermissionsHandler.class);
                permissions.putExtra(PermissionsHandler.EXTRA_REQUIRED_PERMISSIONS, permission);
                permissions.putExtra(PermissionsHandler.EXTRA_REDIRECT_ACTIVITY, getPackageName() + "/" + getPackageName() + ".qr_reader.ReadQR");
                permissions.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(permissions);
            }
        }


        boolean isRunning = false;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        assert mNotificationManager != null;
        StatusBarNotification[] notifications = mNotificationManager.getActiveNotifications();
        for (StatusBarNotification notification : notifications) {
            if (notification.getId() == NotificationUtils.ARTICLE_NOTIFICATION_ID) {
                isRunning = true;
            }
        }


        if(!isRunning) {
            Intent aware = new Intent(this, Aware.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(aware);
            } else {
                getBaseContext().startService(aware);
            }
            NotificationUtils.notifyCorrectUpdate(getApplicationContext());

        }

 */





        


    }

    public void startService() {
        Intent aware = new Intent(this, ExampleService.class);
        //Intent serviceIntent = new Intent(this, ExampleService.class);

        ContextCompat.startForegroundService(this, aware);

    }

    public void stopService() {
        Intent serviceIntent = new Intent(this, ExampleService.class);
        stopService(serviceIntent);
    }

    private void inicializaMenuInferior() {
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
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

        boolean cargarFragmento = false;


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        switch (menuItem.getItemId()){
            case R.id.nav_gallery:
                readQR();
                break;
            case R.id.nav_home:
                irATest();
                break;
            case R.id.nav_slideshow:
                Aware.debug(getApplicationContext(), "Conectando");
                Aware.joinStudy(getApplicationContext(),
                        "https://api.awareframework.com/index.php/webservice/index/2501/ZbTIjeyGPlxc");
                break;

        }

        if(cargarFragmento) {
            return cargarFragmento(fragment);
        }
        else{
            return true;
        }


    }


    private void irPreferenciasUsuario() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivityForResult(intent,1);
    }

    private void irATest() {
        Intent intent = new Intent(this, Test.class);
        startActivity(intent);
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
        else if (requestCode == STT_CODE){
            if (resultCode == RESULT_OK && null != data) {
                ArrayList result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                for(int i = 0; i < result.size(); ++i){
                    Log.d("STT", result.get(i).toString());
                }
            }

        }
        super.onActivityResult(requestCode,resultCode,data);
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

    public void speechToText(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Need to speak");
        try {
            startActivityForResult(intent, STT_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getBaseContext(),
                    "Sorry your device not supported",
                    Toast.LENGTH_SHORT).show();
        }
    }



}

