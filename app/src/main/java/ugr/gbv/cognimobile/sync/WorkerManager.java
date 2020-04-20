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
    public static String DOWNLOAD_TAG = "downloadResults";
    public static String DOWNLOAD_UID = "download";
    public static String UPLOAD_TAG = "uploadResults";
    public static String UPLOAD_UID = "upload";
    public static String DELETE_TAG = "deleteAll";
    public static String DELETE_UID = "delete";


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

    public void initiateWorkers(@NonNull Context context) {
        Constraints.Builder builder = new Constraints.Builder();


        builder.setRequiredNetworkType(NetworkType.UNMETERED)
        .setRequiresStorageNotLow(true);


        Constraints constraints = builder.build();


        PeriodicWorkRequest downloadRequest =
                new PeriodicWorkRequest.Builder(TestsWorker.class,
                        context.getResources().getInteger(R.integer.fifteen),
                        TimeUnit.MINUTES)
                        .setConstraints(constraints)
                        .addTag(DOWNLOAD_TAG)
                        .build();

        PeriodicWorkRequest uploadRequest =
                new PeriodicWorkRequest.Builder(ResultWorker.class,
                        context.getResources().getInteger(R.integer.fifteen),
                        TimeUnit.MINUTES)
                        .setConstraints(constraints)
                        .addTag(UPLOAD_TAG)
                        .build();

        PeriodicWorkRequest deleteRequest =
                new PeriodicWorkRequest.Builder(DeleteWorker.class,
                        context.getResources().getInteger(R.integer.fifteen),
                        TimeUnit.MINUTES)
                        .setConstraints(constraints)
                        .addTag(DELETE_TAG)
                        .build();

        WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(DOWNLOAD_UID,
                        ExistingPeriodicWorkPolicy.KEEP, downloadRequest);

        WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(UPLOAD_UID,
                        ExistingPeriodicWorkPolicy.KEEP, uploadRequest);

        WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(DELETE_UID,
                        ExistingPeriodicWorkPolicy.KEEP, deleteRequest);

    }


}