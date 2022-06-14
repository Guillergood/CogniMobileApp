package ugr.gbv.cognimobile.sync;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import ugr.gbv.cognimobile.database.ContentProvider;

/**
 * Class to retrieve the tests from the server
 */
public class TestDownloader extends Worker {

    private final Context workerContext;

    /**
     * Constructor
     *
     * @param context from the parent activity.
     * @param params  worker parameters
     */
    public TestDownloader(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        workerContext = context;
    }

    /**
     * Overrides the {@link Worker#doWork()} method
     *
     * @return The {@link androidx.work.ListenableWorker.Result} of the computation; note that
     * dependent work will not execute if you use
     * {@link androidx.work.ListenableWorker.Result#failure()} or
     * {@link androidx.work.ListenableWorker.Result#failure(Data)}
     */
    @NonNull
    @Override
    public Result doWork() {
        ContentProvider.getInstance().getTests(workerContext);
        return Result.success();
    }

}
