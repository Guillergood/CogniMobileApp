package ugr.gbv.cognimobile.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aware.utils.DatabaseHelper;

import java.util.HashMap;
import java.util.Objects;

public class Provider extends ContentProvider {


    public static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "cognimobile.db";

    public static String AUTHORITY = "ugr.gbv.cognimobile.provider";


    /**
     * Your ContentProvider table content URI.<br/>
     * The last segment needs to match your database table name
     */
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/cognimobile");

    /**
     * How your data collection is identified internally in Android (vnd.android.cursor.dir). <br/>
     * It needs to be /vnd.aware.plugin.XXX where XXX is your plugin name (no spaces!).
     */
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.ugr.gbv.cognimobile.provider";

    /**
     * How each row is identified individually internally in Android (vnd.android.cursor.item). <br/>
     * It needs to be /vnd.aware.plugin.XXX where XXX is your plugin name (no spaces!).
     */
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.ugr.gbv.cognimobile.provider";


    private static final int TABLE_COGNIMOBILE_DIR = 1;
    private static final int TABLE_COGNIMOBILE_ITEM = 2;


    public static final String _ID = "_id";
    public static final String TIMESTAMP = "timestamp";
    public static final String DEVICE_ID = "device_id";

    //These are columns that we need to sync data, don't change this!
    public interface AWAREColumns extends BaseColumns {
        String _ID = "_id";
        String TIMESTAMP = "timestamp";
        String DEVICE_ID = "device_id";
    }


    public static final String DB_TBL_COGNIMOBILE = "cognimobile";

    public static final String[] DATABASE_TABLES = {DB_TBL_COGNIMOBILE};



    public static final class Cognimobile_Data implements AWAREColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + DB_TBL_COGNIMOBILE);
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.cognimobile.provider.cognimobile";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.cognimobile.provider.cognimobile";

        static final String DATA = "data";
    }



    //Game table fields
    private static final String DB_TBL_COGNIMOBILE_FIELDS =
            Cognimobile_Data._ID + " integer primary key autoincrement," +
                    Cognimobile_Data.TIMESTAMP + " real default 0," +
                    Cognimobile_Data.DEVICE_ID + " text default ''," +
                    Cognimobile_Data.DATA + " longtext default ''";

    public static final String[] TABLES_FIELDS = {
            DB_TBL_COGNIMOBILE_FIELDS
    };

    //Helper variables for ContentProvider - DO NOT CHANGE
    private UriMatcher sUriMatcher;
    private DatabaseHelper dbHelper;
    private static SQLiteDatabase database;
    private HashMap<String, String> tableMap;
    private void initialiseDatabase() {
        if (dbHelper == null)
            dbHelper = new DatabaseHelper(getContext(), DATABASE_NAME, null, DATABASE_VERSION, DATABASE_TABLES, TABLES_FIELDS);
        if (database == null)
            database = dbHelper.getWritableDatabase();
    }


    /**
     * Returns the provider authority that is dynamic
     * @return AUTHORITY
     */
    public static String getAuthority(Context context) {
        AUTHORITY = context.getPackageName() + ".provider";
        return AUTHORITY;
    }



    @Override
    public boolean onCreate() {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        //Cognimobile table indexes DIR and ITEM
        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[0], TABLE_COGNIMOBILE_DIR); //URI for all records
        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[0]+"/#", TABLE_COGNIMOBILE_ITEM); //URI for a single record


        tableMap = new HashMap<>();
        tableMap.put(Cognimobile_Data._ID, Cognimobile_Data._ID);
        tableMap.put(Cognimobile_Data.TIMESTAMP, Cognimobile_Data.TIMESTAMP);
        tableMap.put(Cognimobile_Data.DEVICE_ID, Cognimobile_Data.DEVICE_ID);
        tableMap.put(Cognimobile_Data.DATA, Cognimobile_Data.DATA);

        return true; //let Android know that the database is ready to be used.
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {



        initialiseDatabase();

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        if (sUriMatcher.match(uri) == TABLE_COGNIMOBILE_DIR) {
            qb.setTables(DATABASE_TABLES[0]);
            qb.setProjectionMap(tableMap); //the hashmap of the table
        } else {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        //Don't change me
        try {
            Cursor c = qb.query(database, projection, selection, selectionArgs,
                    null, null, sortOrder);
            c.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);
            return c;
        } catch (IllegalStateException e) {
            return null;
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case TABLE_COGNIMOBILE_DIR:
                return Cognimobile_Data.CONTENT_TYPE;
            case TABLE_COGNIMOBILE_ITEM:
                return Cognimobile_Data.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues initialValues) {

        initialiseDatabase();

        ContentValues values = (initialValues != null) ? new ContentValues(initialValues) : new ContentValues();

        database.beginTransaction();

        if (sUriMatcher.match(uri) == TABLE_COGNIMOBILE_DIR) {
            long game_id = database.insert(DATABASE_TABLES[0], Cognimobile_Data.DEVICE_ID, values);
            database.setTransactionSuccessful();
            database.endTransaction();
            if (game_id > 0) {
                Uri dataUri = ContentUris.withAppendedId(Cognimobile_Data.CONTENT_URI, game_id);
                Objects.requireNonNull(getContext()).getContentResolver().notifyChange(dataUri, null, false);
                return dataUri;
            }
            database.endTransaction();
            throw new SQLException("Failed to insert row into " + uri);
        }
        database.endTransaction();
        throw new IllegalArgumentException("Unknown URI " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        initialiseDatabase();

        database.beginTransaction();

        int count;
        if (sUriMatcher.match(uri) == TABLE_COGNIMOBILE_DIR) {
            count = database.delete(DATABASE_TABLES[0], selection, selectionArgs);
        } else {
            database.endTransaction();
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        database.setTransactionSuccessful();
        database.endTransaction();

        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null, false);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        initialiseDatabase();

        database.beginTransaction();

        int count;
        if (sUriMatcher.match(uri) == TABLE_COGNIMOBILE_DIR) {
            count = database.update(DATABASE_TABLES[0], values, selection, selectionArgs);
        } else {
            database.endTransaction();
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        database.setTransactionSuccessful();
        database.endTransaction();

        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null, false);

        return count;
    }
}
