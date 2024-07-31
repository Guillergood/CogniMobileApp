package ugr.gbv.cognimobile.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cognimobile_app.db";
    private static final int DATABASE_VERSION = 1;

    private static final String RESULT_TABLE_NAME = "RESULTS";
    private static final String TESTS_TABLE_NAME = "TESTS";
    private static final String EVENTS_TABLE_NAME = "EVENTS";

    private static final String STUDIES_TABLE_NAME = "STUDIES";


    private static final String DB_TBL_RESULTS_FIELDS =
            Provider.Cognimobile_Data._ID + " integer primary key autoincrement," +
                    Provider.Cognimobile_Data.TIMESTAMP + " real default 0," +
                    Provider.Cognimobile_Data.DATA + " blob default ''," +
                    Provider.Cognimobile_Data.NAME + " longtext default ''," +
                    Provider.Cognimobile_Data.ERASE_TIMESTAMP + " real default 0";

    //Cognimobile table fields
    private static final String DB_TBL_TESTS_FIELDS =
            Provider.Cognimobile_Data._ID + " integer primary key autoincrement," +
                    Provider.Cognimobile_Data.TIMESTAMP + " real default 0," +
                    Provider.Cognimobile_Data.DATA + " blob default ''," +
                    Provider.Cognimobile_Data.NAME + " longtext default ''," +
                    Provider.Cognimobile_Data.REDO_TIMESTAMP + " real default 0," +
                    Provider.Cognimobile_Data.DAYS_TO_DO + " real default 0";

    private static final String DB_TBL_STUDY_FIELDS =
            Provider.Cognimobile_Data._ID + " integer primary key autoincrement," +
                    Provider.Cognimobile_Data.NAME + " longtext default ''," +
                    Provider.Cognimobile_Data.DATA + " blob default ''";
    private static final String DB_TBL_EVENTS_FIELDS =
            Provider.Cognimobile_Data._ID + " integer primary key autoincrement," +
                    Provider.Cognimobile_Data.TIMESTAMP + " real default 0," +
                    Provider.Cognimobile_Data.DATA + " blob default ''," +
                    Provider.Cognimobile_Data.NAME + " longtext default ''," +
                    Provider.Cognimobile_Data.ERASE_TIMESTAMP + " real default 0";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_RESULTS_TABLE = "CREATE TABLE " +
                RESULT_TABLE_NAME + " ( " + DB_TBL_RESULTS_FIELDS + " );";
        final String SQL_CREATE_TESTS_TABLE = "CREATE TABLE " +
                TESTS_TABLE_NAME + " ( " + DB_TBL_TESTS_FIELDS + " );";
        final String SQL_CREATE_EVENTS_TABLE = "CREATE TABLE " +
                EVENTS_TABLE_NAME + " ( " + DB_TBL_EVENTS_FIELDS + " );";
        final String SQL_CREATE_STUDIES_TABLE = "CREATE TABLE " +
                STUDIES_TABLE_NAME + " ( " + DB_TBL_STUDY_FIELDS + " );";

        sqLiteDatabase.execSQL(SQL_CREATE_RESULTS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TESTS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_EVENTS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_STUDIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RESULT_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TESTS_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + EVENTS_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + STUDIES_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
