package ugr.gbv.myapplication.qr_reader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/**
 * Created by denzil on 27/10/15.
 */
public class ReadQR extends Activity implements ZBarScannerView.ResultHandler {

    private ZBarScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScannerView = new ZBarScannerView(this);

        LinearLayout main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);

        ListView list = new ListView(this);
        list.setId(android.R.id.list);
        list.setVisibility(View.GONE);
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
            data.putExtra("link", result.getContents());
            setResult(RESULT_OK,data);
        }
        else{
            setResult(RESULT_CANCELED);
        }

        finish();


    }
}
