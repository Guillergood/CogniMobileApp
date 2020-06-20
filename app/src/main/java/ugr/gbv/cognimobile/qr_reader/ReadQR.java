package ugr.gbv.cognimobile.qr_reader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.material.button.MaterialButton;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.activities.WriteTestLink;

/**
 * Class to read a QR
 * Retrieved from AWARE
 *
 * @see <a href="https://github.com/denzilferreira/aware-client/blob/master/aware-phone/src/main/java/com/aware/phone/ui/Aware_QRCode.java">https://github.com/denzilferreira/aware-client/blob/master/aware-phone/src/main/java/com/aware/phone/ui/Aware_QRCode.java</a>
 * Created by denzil on 27/10/15.
 */
public class ReadQR extends Activity implements ZBarScannerView.ResultHandler {

    private ZBarScannerView mScannerView;
    public static final String ACTIVITY_LINK_ACTION = "activityToLink";
    public static final String INTENT_ACTION_LABEL = "action";
    public static final String INTENT_LINK_LABEL = "link";

    private static final int LINK_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScannerView = new ZBarScannerView(this);

        LinearLayout main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setBackgroundColor(getResources().getColor(R.color.black,getTheme()));

        MaterialButton button = new MaterialButton(this);

        button.setText(R.string.enter_link);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);


        button.setLayoutParams(layoutParams);

        button.setOnClickListener(v -> goToActivityWrittenLink());

        ListView list = new ListView(this);
        list.setId(android.R.id.list);
        list.setVisibility(View.GONE);
        button.setId(View.generateViewId());
        button.setVisibility(View.VISIBLE);
        main.addView(button);
        main.addView(mScannerView);
        main.addView(list);
        setContentView(main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.stopCamera();
        mScannerView.stopCameraPreview();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //Zbar QRCode handler
    @Override
    public void handleResult(Result result) {
        if(result.getContents() != null){
            Intent data = getIntent();
            if(!result.getContents().equals(INTENT_LINK_LABEL)) {
                data.putExtra(INTENT_LINK_LABEL, result.getContents());
                setResult(RESULT_OK, data);
            }
            else{
                setResult(RESULT_CANCELED);
            }
        }
        else{
            setResult(RESULT_CANCELED);
        }

        finish();


    }

    private void goToActivityWrittenLink() {
        Intent intent = new Intent(this, WriteTestLink.class);
        startActivityForResult(intent, LINK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == LINK_CODE) {
            Result result = new Result();
            if (resultCode == RESULT_OK && data != null) {
                result.setContents(data.getStringExtra(INTENT_LINK_LABEL));
            }
            else{
                result.setContents(INTENT_LINK_LABEL);
            }
            handleResult(result);
        }
        finish();
    }
}
