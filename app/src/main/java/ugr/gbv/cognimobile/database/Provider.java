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
import java.util.List;
import java.util.Objects;

/**
 * Class to make CRUD operations on the local database
 * Template from AWARE
 *
 * @see <a href="https://awareframework.com/creating-a-context-provider/">https://awareframework.com/creating-a-context-provider/</a>
 */
public class Provider extends ContentProvider {


    public static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "cognimobile.db";

    public static String AUTHORITY = "ugr.gbv.cognimobile.provider";
    public static String AWARE_PLUGIN_NAME = "ugr.gbv.cognimobile";


    /**
     * Your ContentProvider table content URI.<br/>
     * The last segment needs to match your database table name
     */
    public static final Uri CONTENT_URI_TESTS = Uri.parse("content://" + AUTHORITY + "/tests");
    public static final Uri CONTENT_URI_RESULTS = Uri.parse("content://" + AUTHORITY + "/results");

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


    private static final int TABLE_TESTS_DIR = 1;
    private static final int TABLE_TESTS_ITEM = 2;
    private static final int TABLE_RESULTS_DIR = 3;
    private static final int TABLE_RESULTS_ITEM = 4;


    public static final String _ID = "_id";
    public static final String TIMESTAMP = "timestamp";
    public static final String DEVICE_ID = "device_id";

    //These are columns that we need to sync data, don't change this!
    public interface AWAREColumns extends BaseColumns {
        String _ID = "_id";
        String TIMESTAMP = "timestamp";
        String DEVICE_ID = "device_id";
    }


    public static final String DB_TBL_TESTS = "tests";
    public static final String DB_TBL_RESULTS = "results";

    public static final String[] DATABASE_TABLES = {
            DB_TBL_TESTS,
            DB_TBL_RESULTS
    };


    //Cognimobile table fields
    private static final String DB_TBL_TESTS_FIELDS =
            Cognimobile_Data._ID + " integer primary key autoincrement," +
                    Cognimobile_Data.TIMESTAMP + " real default 0," +
                    Cognimobile_Data.DEVICE_ID + " text default ''," +
                    Cognimobile_Data.DATA + " longtext default ''," +
                    Cognimobile_Data.NAME + " longtext default ''," +
                    Cognimobile_Data.DONE + " integer default 0," +
                    Cognimobile_Data.ERASE_TIMESTAMP + " real default 0";

    public static final class Cognimobile_Data implements AWAREColumns {
        public static final Uri CONTENT_URI_TESTS = Uri.parse("content://" + AUTHORITY + "/" + DB_TBL_TESTS);
        public static final Uri CONTENT_URI_RESULTS = Uri.parse("content://" + AUTHORITY + "/" + DB_TBL_RESULTS);
        public static final String NAME = "name";
        static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.cognimobile.provider.cognimobile";
        static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.cognimobile.provider.cognimobile";
        public static final String DATA = "data";
        public static final String SYNCED = "synced";
        public static final String DONE = "done";
        public static final String ERASE_TIMESTAMP = "erase_timestamp";

    }

    private static final String DB_TBL_RESULTS_FIELDS =
            Cognimobile_Data._ID + " integer primary key autoincrement," +
                    Cognimobile_Data.TIMESTAMP + " real default 0," +
                    Cognimobile_Data.DEVICE_ID + " text default ''," +
                    Cognimobile_Data.DATA + " longtext default ''," +
                    Cognimobile_Data.NAME + " longtext default ''," +
                    Cognimobile_Data.SYNCED + " integer default 0," +
                    Cognimobile_Data.ERASE_TIMESTAMP + " real default 0";

    public static final String[] TABLES_FIELDS = {
            DB_TBL_TESTS_FIELDS,
            DB_TBL_RESULTS_FIELDS
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
        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[0], TABLE_TESTS_DIR); //URI for all records
        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[0]+"/#", TABLE_TESTS_ITEM); //URI for a single record
        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[1], TABLE_RESULTS_DIR); //URI for all records
        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[1]+"/#", TABLE_RESULTS_ITEM); //URI for a single record


        tableMap = new HashMap<>();
        tableMap.put(Cognimobile_Data._ID, Cognimobile_Data._ID);
        tableMap.put(Cognimobile_Data.TIMESTAMP, Cognimobile_Data.TIMESTAMP);
        tableMap.put(Cognimobile_Data.DEVICE_ID, Cognimobile_Data.DEVICE_ID);
        tableMap.put(Cognimobile_Data.NAME, Cognimobile_Data.NAME);
        tableMap.put(Cognimobile_Data.DATA, Cognimobile_Data.DATA);
        tableMap.put(Cognimobile_Data.SYNCED, Cognimobile_Data.SYNCED);
        tableMap.put(Cognimobile_Data.ERASE_TIMESTAMP, Cognimobile_Data.ERASE_TIMESTAMP);

        return true; //let Android know that the database is ready to be used.
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        initialiseDatabase();

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)){
            case TABLE_TESTS_DIR:
                queryConfiguration(DATABASE_TABLES[0],qb);
                break;
            case TABLE_RESULTS_DIR:
                queryConfiguration(DATABASE_TABLES[1],qb);
                break;
            default:
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

    private void queryConfiguration(String databaseTable, SQLiteQueryBuilder qb) {
        qb.setTables(databaseTable);
        qb.setProjectionMap(tableMap); //the hashmap of the table
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case TABLE_TESTS_DIR:
            case TABLE_RESULTS_DIR:
                return Cognimobile_Data.CONTENT_TYPE;
            case TABLE_TESTS_ITEM:
            case TABLE_RESULTS_ITEM:
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

        Uri returnValue;
        database.beginTransaction();
        switch (sUriMatcher.match(uri)) {
            case TABLE_TESTS_DIR:
                returnValue = insertTransaction(uri, DATABASE_TABLES[0], values);
                break;
            case TABLE_RESULTS_DIR:
                returnValue = insertTransaction(uri, DATABASE_TABLES[1], values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        return returnValue;
    }

    private Uri insertTransaction(Uri uri, String databaseTable, ContentValues values) {
        long id = database.insert(databaseTable, Cognimobile_Data.DEVICE_ID, values);
        database.setTransactionSuccessful();
        database.endTransaction();
        if (id > 0) {
            Uri dataUri;
            List<String> segments = uri.getPathSegments();
            if (segments.get(segments.size() - 1).equals(DB_TBL_TESTS)) {
                dataUri = ContentUris.withAppendedId(Cognimobile_Data.CONTENT_URI_TESTS, id);
            } else if (segments.get(segments.size() - 1).equals(DB_TBL_RESULTS)) {
                dataUri = ContentUris.withAppendedId(Cognimobile_Data.CONTENT_URI_RESULTS, id);
            } else {
                throw new RuntimeException("PETO EN PROVIDER.java:244");
            }

            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(dataUri, null, false);
            return dataUri;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        initialiseDatabase();

        database.beginTransaction();

        int count;

        switch (sUriMatcher.match(uri)) {
            case TABLE_TESTS_DIR:
                count = database.delete(DATABASE_TABLES[0], selection, selectionArgs);
                break;
            case TABLE_RESULTS_DIR:
                count = database.delete(DATABASE_TABLES[1], selection, selectionArgs);
                break;
            default:
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
        switch (sUriMatcher.match(uri)) {
            case TABLE_TESTS_DIR:
                count = database.update(DATABASE_TABLES[0], values, selection, selectionArgs);
                break;
            case TABLE_RESULTS_DIR:
                count = database.update(DATABASE_TABLES[1], values, selection, selectionArgs);
                break;
            default:
                database.endTransaction();
                throw new IllegalArgumentException("Unknown URI " + uri);
        }


        database.setTransactionSuccessful();
        database.endTransaction();

        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null, false);

        return count;
    }
}
