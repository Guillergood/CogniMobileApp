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
import ugr.gbv.cognimobile.BuildConfig;
import ugr.gbv.cognimobile.R;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.mipmap.ic_launcher)
                .addItem(new Element().setTitle(BuildConfig.VERSION_NAME))
                .setDescription(getString(R.string.cognimobile_description))
                .addGroup("Connect with us")
                .addWebsite("http://guillergood.github.io/")
                .addGitHub("guillergood")
                .addItem(getThirdPartyElements())
                .addItem(getLicenseTerms())
                .create();

        setContentView(aboutPage);
    }

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


    Element getLicenseTerms() {
        Element licenseElement = new Element();
        final String copyrights = getString(R.string.app_name) + " " + Calendar.getInstance().get(Calendar.YEAR);
        licenseElement.setTitle(copyrights);
        licenseElement.setGravity(Gravity.CENTER);
        licenseElement.setOnClickListener(v -> Toast.makeText(this, copyrights, Toast.LENGTH_SHORT).show());
        return licenseElement;
    }


}
