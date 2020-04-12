package ugr.gbv.cognimobile.sync;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;


import java.util.concurrent.TimeUnit;

import ugr.gbv.cognimobile.R;

public class WorkerManager {

    private static volatile WorkerManager instance;
    public static String UPLOAD_TAG = "uploadResults";
    public static String UPLOAD_UID = "upload";
    public static String TEST_CHECKER_TAG = "testChecker";
    public static String TEST_CHECKER_UID = "checker";


    private WorkerManager(){

        if (instance != null){
            throw new RuntimeException("Use .getInstance() to instantiate UploadManager");
        }
    }

    public static WorkerManager getInstance() {
        if (instance == null) {
            synchronized (WorkerManager.class) {
                if (instance == null) instance = new WorkerManager();
            }
        }

        return instance;
    }

    public PeriodicWorkRequest initiateWork(@NonNull Context context){
        Constraints.Builder builder = new Constraints.Builder();


        builder.setRequiredNetworkType(NetworkType.UNMETERED)
        .setRequiresStorageNotLow(true);


        Constraints constraints = builder.build();


        PeriodicWorkRequest uploadRequest =
                new PeriodicWorkRequest.Builder(TestsWorker.class,
                        context.getResources().getInteger(R.integer.fifteen),
                        TimeUnit.MINUTES)
                        .setConstraints(constraints)
                        .addTag(UPLOAD_TAG)
                        .build();

        WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(UPLOAD_UID,
                        ExistingPeriodicWorkPolicy.REPLACE,uploadRequest);


        return uploadRequest;

    }

}
