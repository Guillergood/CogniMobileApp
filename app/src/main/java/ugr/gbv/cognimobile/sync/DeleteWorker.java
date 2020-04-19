package ugr.gbv.cognimobile.sync;

import android.content.Context;
import android.icu.util.Calendar;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import ugr.gbv.cognimobile.database.Provider;

public class DeleteWorker extends Worker {

    private Context workerContext;

    public DeleteWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        workerContext = context;
    }

    @NonNull
    @Override
    public Result doWork() {


        int deleted = 0;

        deleted += deleteFromCursor(Provider.CONTENT_URI_RESULTS, workerContext);
        deleted += deleteFromCursor(Provider.CONTENT_URI_TESTS, workerContext);

        Log.d("WORKERS", "Delete worker termino :" + System.currentTimeMillis());

        if (deleted > 0) {
            return Result.success();
        } else {
            return Result.retry();
        }


    }

    private int deleteFromCursor(Uri uri, Context workerContext) {
        String where = Provider.Cognimobile_Data.ERASE_TIMESTAMP + " >= ?";
        String[] selectionArgs = {Long.toString(getCurrentMillis())};
        return workerContext.getContentResolver().delete(uri, where, selectionArgs);
    }

    private long getCurrentMillis() {
        Calendar c = Calendar.getInstance();
        return c.getTimeInMillis();
    }
}
