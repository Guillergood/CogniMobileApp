package ugr.gbv.cognimobile.sync;

import android.content.Context;
import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import ugr.gbv.cognimobile.database.Provider;
import ugr.gbv.cognimobile.utilities.NotificationUtils;

/**
 * Class to retrieve the tests from the server
 */
public class TestsChecker extends Worker {

    private final Context workerContext;

    /**
     * Constructor
     *
     * @param context from the parent activity.
     * @param params  worker parameters
     */
    public TestsChecker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        workerContext = context;
    }

    /**
     * Overrides the {@link Worker#doWork()} method
     *
     * @return The {@link Result} of the computation; note that
     * dependent work will not execute if you use
     * {@link Result#failure()} or
     * {@link Result#failure(Data)}
     */
    @NonNull
    @Override
    public Result doWork() {
        String[] projection = new String[]{Provider.Cognimobile_Data.REDO_TIMESTAMP};
        Cursor cursor =
                workerContext.getContentResolver()
                        .query(Provider.CONTENT_URI_TESTS,
                                projection, null, null, Provider.Cognimobile_Data._ID);
        int count = 0;
        if (cursor != null) {
            if(cursor.moveToFirst()) {
                do {
                    long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(Provider.Cognimobile_Data.REDO_TIMESTAMP));
                    if (System.currentTimeMillis() >= timestamp) {
                        count++;
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        if(count > 0){
            NotificationUtils.getInstance().notifyNewTestsAvailable(count,workerContext);
        }
        return Result.success();
    }

}
