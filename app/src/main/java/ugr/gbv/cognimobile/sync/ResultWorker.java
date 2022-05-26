package ugr.gbv.cognimobile.sync;

import android.content.Context;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONArray;
import org.json.JSONException;

import ugr.gbv.cognimobile.database.Provider;
import ugr.gbv.cognimobile.utilities.DataSender;

/**
 * Class to do send if there is any results from tests that are on the local database.
 */
public class ResultWorker extends Worker {

    private final Context workerContext;

    /**
     * Constructor
     *
     * @param context from the parent activity.
     * @param params  worker parameters
     */
    public ResultWorker(
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

//
//        String[] projection = new String[]{Provider.Cognimobile_Data.DATA};
//
//        String where = Provider.Cognimobile_Data.SYNCED + " LIKE ?";
//        String[] selectionArgs = {"0"};
//
//        Cursor cursor = workerContext.getContentResolver().query(Provider.CONTENT_URI_RESULTS, projection, where, selectionArgs, Provider.Cognimobile_Data._ID);
//
//        int inserted = 0;
//
//        if (cursor != null && cursor.getCount() > 0) {
//            cursor.moveToFirst();
//            inserted = cursor.getCount();
//            while (!cursor.isAfterLast()) {
//                JSONArray jsonArray = null;
//                try {
//                    //jsonArray = new JSONArray(cursor.getString(cursor.getColumnIndex(Provider.Cognimobile_Data.DATA)));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                if (jsonArray != null) {
//                 //   DataSender.getInstance().postToServer("insert", "results", jsonArray, getApplicationContext());
//                }
//            }
//
//            cursor.close();
//
//        }
//
//
//        if (inserted > 0) {
//            return Result.success();
//        } else {
//            return Result.retry();
//        }

        return Result.success();
    }

}
