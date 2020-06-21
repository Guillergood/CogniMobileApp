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

/**
 * Class to manage the workers.
 */
public class WorkerManager {

    private static volatile WorkerManager instance;


    /**
     * Constructor.
     */
    private WorkerManager() {

        if (instance != null) {
            throw new RuntimeException("Use .instantiate() to instantiate UploadManager");
        }
    }

    /**
     * Singleton pattern
     *
     * @return single instance of the WorkerManager
     */
    public static WorkerManager getInstance() {
        if (instance == null) {
            synchronized (WorkerManager.class) {
                if (instance == null) instance = new WorkerManager();
            }
        }

        return instance;
    }

    /**
     * Initiates the workers.
     *
     * @param context from the parent activity.
     */
    public void initiateWorkers(@NonNull Context context) {
        Constraints.Builder builder = new Constraints.Builder();


        builder.setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresStorageNotLow(true);


        Constraints constraints = builder.build();


        String DOWNLOAD_TAG = "downloadResults";
        PeriodicWorkRequest downloadRequest =
                new PeriodicWorkRequest.Builder(TestsWorker.class,
                        context.getResources().getInteger(R.integer.fifteen),
                        TimeUnit.MINUTES)
                        .setConstraints(constraints)
                        .addTag(DOWNLOAD_TAG)
                        .build();

        String UPLOAD_TAG = "uploadResults";
        PeriodicWorkRequest uploadRequest =
                new PeriodicWorkRequest.Builder(ResultWorker.class,
                        context.getResources().getInteger(R.integer.fifteen),
                        TimeUnit.MINUTES)
                        .setConstraints(constraints)
                        .addTag(UPLOAD_TAG)
                        .build();

        String DELETE_TAG = "deleteAll";
        PeriodicWorkRequest deleteRequest =
                new PeriodicWorkRequest.Builder(DeleteWorker.class,
                        context.getResources().getInteger(R.integer.thirty),
                        TimeUnit.DAYS)
                        .setConstraints(constraints)
                        .addTag(DELETE_TAG)
                        .build();

        String DOWNLOAD_UID = "download";
        WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(DOWNLOAD_UID,
                        ExistingPeriodicWorkPolicy.KEEP, downloadRequest);

        String UPLOAD_UID = "upload";
        WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(UPLOAD_UID,
                        ExistingPeriodicWorkPolicy.KEEP, uploadRequest);

        String DELETE_UID = "delete";
        WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(DELETE_UID,
                        ExistingPeriodicWorkPolicy.KEEP, deleteRequest);

    }


}
