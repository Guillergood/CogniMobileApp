package ugr.gbv.myapplication.utilities;


import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

public class syncIntentService extends IntentService {

    public syncIntentService() {
        super("syncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent != null) {
            String action = intent.getAction();
            NotificationTasks.executeTask(this, action);
        }
    }
}
