package ugr.gbv.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import ugr.gbv.myapplication.R;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener,
        NavigationView.OnNavigationItemSelectedListener{

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



        /*fragment = new DrawTask();
        cargarFragmento(fragment);*/
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
                //fragment = new DrawTask(DrawTask.GRAPH);
                //cargarFragmento = true;
                break;
            case R.id.nav_home:
                irATest();
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

