package ugr.gbv.cognimobile.sync;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import ugr.gbv.cognimobile.database.Provider;
import ugr.gbv.cognimobile.utilities.DataSender;

public class ResultWorker extends Worker {

    private Context workerContext;

    public ResultWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        workerContext = context;
    }

    @NonNull
    @Override
    public Result doWork() {


        String[] projection = new String[]{Provider.Cognimobile_Data.DATA};

        String where = Provider.Cognimobile_Data.SYNCED + " LIKE ?";
        String[] selectionArgs = {"0"};

        Cursor cursor = workerContext.getContentResolver().query(Provider.CONTENT_URI_RESULTS, projection, where, selectionArgs, Provider.Cognimobile_Data._ID);

        int inserted = 0;

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            inserted = cursor.getCount();
            while (!cursor.isAfterLast()) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(cursor.getString(cursor.getColumnIndex(Provider.Cognimobile_Data.DATA)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (jsonArray != null) {
                    DataSender.getInstance().postToServer("insert", "results", jsonArray, getApplicationContext());
                }
            }

            cursor.close();

        }


        Log.d("WORKERS", "Result worker termino :" + System.currentTimeMillis());

        if (inserted > 0) {
            return Result.success();
        } else {
            return Result.retry();
        }


    }


    private Cursor getThoseTestDoneAndNotSynced() {
        String where = Provider.Cognimobile_Data.DONE + " LIKE ?";
        String[] selectionArgs = {"1"};
        String[] projection = new String[]{Provider.Cognimobile_Data.NAME};
        Cursor cursor = workerContext.getContentResolver().query(Provider.CONTENT_URI_RESULTS, projection, where, selectionArgs, Provider.Cognimobile_Data._ID);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            ArrayList<String> names = new ArrayList<>();

            while (!cursor.isAfterLast()) {
                names.add(cursor.getString(cursor.getColumnIndex(Provider.Cognimobile_Data.NAME)));
                cursor.moveToNext();
            }
            cursor.close();

            StringBuilder statement = new StringBuilder();

            if (names.size() > 0) {
                statement.append("  IN ( ");
                String placeHolder = ",";
                for (String name : names) {
                    statement.append(name.trim());
                    statement.append(placeHolder);
                }
                statement.deleteCharAt(statement.length() - 1);
                statement.append(" )");
            }

            where = Provider.Cognimobile_Data.NAME + statement.toString() + " AND " + Provider.Cognimobile_Data.SYNCED + " LIKE ?";
            selectionArgs = new String[]{"0"};
            projection = new String[]{Provider.Cognimobile_Data.NAME, Provider.Cognimobile_Data.DATA};
            cursor = workerContext.getContentResolver().query(Provider.CONTENT_URI_RESULTS, projection, where, selectionArgs, Provider.Cognimobile_Data._ID);

        }

        return cursor;
    }
}
