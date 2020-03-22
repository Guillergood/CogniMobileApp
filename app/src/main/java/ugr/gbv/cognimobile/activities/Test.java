package ugr.gbv.cognimobile.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.fragments.DrawTask;
import ugr.gbv.cognimobile.fragments.ImageTask;
import ugr.gbv.cognimobile.fragments.Task;
import ugr.gbv.cognimobile.fragments.TextTask;
import ugr.gbv.cognimobile.interfaces.LoadContent;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Test extends AppCompatActivity implements LoadContent {

    private ArrayList<Fragment> fragments;
    private int index;

    private View mContentView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mContentView = findViewById(R.id.fullscreen_content);
        hideNavBar();

        fragments = new ArrayList<>();
        fragments.add(new DrawTask(Task.GRAPH,this));
        fragments.add(new DrawTask(Task.CUBE,this));
        fragments.add(new DrawTask(Task.WATCH,this));
        fragments.add(new ImageTask(this));
        fragments.add(new TextTask(Task.MEMORY,this));
        fragments.add(new TextTask(Task.ATENTION_NUMBERS,this));
        fragments.add(new TextTask(Task.ATENTION_LETTERS,this));
        fragments.add(new TextTask(Task.ATENTION_SUBSTRACTION,this));
        fragments.add(new TextTask(Task.LANGUAGE,this));
        fragments.add(new TextTask(Task.FLUENCY,this));
        fragments.add(new TextTask(Task.ABSTRACTION,this));
        fragments.add(new TextTask(Task.RECALL,this));
        fragments.add(new TextTask(Task.ORIENTATION,this));
        index = 0;






        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        loadFragment(fragments.get(index));



    }

    @SuppressLint("InlinedApi")
    private void hideNavBar(){
        mContentView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                //| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                //| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
    }

    private void loadFragment(@NonNull Fragment fragment) {
        getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.fullscreen_content, fragment)
        .commit();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void loadContent() {
        ++index;
        if(fragments.size() > index){
            loadFragment(fragments.get(index));
        }
        else{
            onBackPressed();
        }
//        else{
//            sendData();
//        }
    }

    @Override
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null && imm != null) {
            view = new View(this);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
