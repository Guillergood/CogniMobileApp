package ugr.gbv.cognimobile.sync;

import android.content.Context;
import android.icu.util.Calendar;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import ugr.gbv.cognimobile.database.Provider;

/**
 * Class to do delete periodic tasks on the local database.
 */
public class DeleteWorker extends Worker {

    private final Context workerContext;

    /**
     * Constructor
     *
     * @param context from the parent activity.
     * @param params  worker parameters
     */
    public DeleteWorker(
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
        int deleted = 0;

        deleted += deleteFromCursor(Provider.CONTENT_URI_RESULTS, workerContext);
        deleted += deleteFromCursor(Provider.CONTENT_URI_TESTS, workerContext);


        if (deleted > 0) {
            return Result.success();
        } else {
            return Result.retry();
        }


    }


    /**
     * Retrieves the data to be erased and deletes it.
     *
     * @param uri           to retrieve the data
     * @param workerContext the worker context.
     * @return rows affected.
     */
    private int deleteFromCursor(Uri uri, Context workerContext) {
        String where = Provider.Cognimobile_Data.ERASE_TIMESTAMP + " >= ?";
        String[] selectionArgs = {Long.toString(getCurrentMillis())};
        return workerContext.getContentResolver().delete(uri, where, selectionArgs);
    }

    /**
     * Gets the current time in milliseconds.
     *
     * @return current time in milliseconds.
     */
    private long getCurrentMillis() {
        Calendar c = Calendar.getInstance();
        return c.getTimeInMillis();
    }
}
