package ugr.gbv.cognimobile.activities;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;
import ugr.gbv.cognimobile.R;


/**
 * About.class is the activity where all the information about the app is displayed
 */
public class About extends AppCompatActivity {

    /**
     * OnCreate method to create the view and instantiate all the elements and put the info,
     * AboutPage is used to develop this {@see <a href="https://github.com/medyo/android-about-page">https://github.com/medyo/android-about-page</a>}
     *
     * @param savedInstanceState contains the most recent data from the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.mipmap.ic_launcher)
                .setDescription(getString(R.string.cognimobile_description))
                .addGroup("Connect with us")
                .addWebsite("http://guillergood.github.io/")
                .addGitHub("guillergood")
                .addItem(getThirdPartyElements())
                .addItem(getLicenseTerms())
                .create();

        setContentView(aboutPage);
    }

    /**
     * This method returns a button with all the license of the third party components
     *
     * @return Element, view that contains all logic for the data to be displayed
     */
    private Element getThirdPartyElements() {
        Element licenseElement = new Element();
        final String copyrights = "Third party components";
        licenseElement.setTitle(copyrights);
        licenseElement.setOnClickListener(v -> {
            Dialog builder = new Dialog(this);

            builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Window window = builder.getWindow();
            if (window != null) {
                window.setBackgroundDrawable(
                        new ColorDrawable(Color.TRANSPARENT));
            }

            builder.setContentView(R.layout.about);


            builder.setCancelable(true);

            builder.show();

        });
        return licenseElement;
    }

    /**
     * This method returns a button with the license of the Cognimobile components
     *
     * @return Element, view that contains all the data to be displayed
     */
    Element getLicenseTerms() {
        Element licenseElement = new Element();
        final String copyrights = getString(R.string.app_name) + " " + Calendar.getInstance().get(Calendar.YEAR);
        licenseElement.setTitle(copyrights);
        licenseElement.setGravity(Gravity.CENTER);
        licenseElement.setOnClickListener(v -> Toast.makeText(this, copyrights, Toast.LENGTH_SHORT).show());
        return licenseElement;
    }


}
