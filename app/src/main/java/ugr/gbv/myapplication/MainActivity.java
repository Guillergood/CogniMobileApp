package ugr.gbv.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.content.PermissionChecker;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.ui.PermissionsHandler;

import java.util.ArrayList;

import ugr.gbv.myapplication.qr_reader.ReadQR;
import ugr.gbv.myapplication.tests.DrawTask;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent aware = new Intent(this, Aware.class);
        startService(aware);
        //Activate Accelerometer
        Aware.setSetting(this, Aware_Preferences.STATUS_BATTERY, true);
        //Set sampling frequency
        //Apply settings
        Aware.startBattery(this);
        //Aware.startAccelerometer(this);

        Aware.debug(getBaseContext(), "DEBUG");
        if(Aware.isStudy(getBaseContext())){
            Aware.joinStudy(getApplicationContext(),"https://api.awareframework.com/index.php/webservice/index/2501/ZbTIjeyGPlxc");
        }


        // ACTUALIZAR DATOS
        /*Intent sync = new Intent(Aware.ACTION_AWARE_SYNC_DATA);
        sendBroadcast(sync);*/

        // ESTO SIRVE PARA LANZAR SI NO ESTAS APUNTADO A UN ESTUDIO
       /* if (!Aware.isStudy(getApplicationContext())) {
            //Shows UI to allow the user to join study
            Intent joinStudy = new Intent(getApplicationContext(), JoinStudy.class);
            joinStudy.putExtra(JoinStudy.EXTRA_STUDY_URL, Aware.getSetting(getApplicationContext(), Aware_Preferences.WEBSERVICE_SERVER));
            startActivity(joinStudy);
        }*/

        Button testButton = findViewById(R.id.testButton);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), DrawTask.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.aware_menu, menu);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.getTitle().toString().equalsIgnoreCase(getResources().getString(R.string.aware_qrcode)) && Aware.is_watch(this))
                item.setVisible(false);
            if (item.getTitle().toString().equalsIgnoreCase(getResources().getString(R.string.aware_team)) && Aware.is_watch(this))
                item.setVisible(false);
            if (item.getTitle().toString().equalsIgnoreCase(getResources().getString(R.string.aware_study)) && Aware.is_watch(this))
                item.setVisible(false);
            if (item.getTitle().toString().equalsIgnoreCase(getResources().getString(R.string.aware_sync)) && !Aware.getSetting(this, Aware_Preferences.STATUS_WEBSERVICE).equals("true"))
                item.setVisible(false);
            if (item.getTitle().toString().equalsIgnoreCase(getResources().getString(R.string.aware_study)) && !Aware.getSetting(this, Aware_Preferences.STATUS_WEBSERVICE).equals("true"))
                item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().toString().equalsIgnoreCase(getResources().getString(R.string.aware_qrcode))) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && PermissionChecker.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ArrayList<String> permission = new ArrayList<>();
                permission.add(Manifest.permission.CAMERA);

                Intent permissions = new Intent(this, PermissionsHandler.class);
                permissions.putExtra(PermissionsHandler.EXTRA_REQUIRED_PERMISSIONS, permission);
                //TODO CAMBIAR ESTO
                permissions.putExtra(PermissionsHandler.EXTRA_REDIRECT_ACTIVITY, getPackageName() + "/" + getPackageName() + ".qr_reader.ReadQR");
                permissions.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(permissions);
            } else {
                Intent qrcode = new Intent(MainActivity.this, ReadQR.class);
                qrcode.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(qrcode);
            }
        }
        if (item.getTitle().toString().equalsIgnoreCase(getResources().getString(R.string.aware_sync))) {
            Toast.makeText(getApplicationContext(), "Syncing data...", Toast.LENGTH_SHORT).show();
            Intent sync = new Intent(Aware.ACTION_AWARE_SYNC_DATA);
            sendBroadcast(sync);
        }
        return super.onOptionsItemSelected(item);
    }
}
