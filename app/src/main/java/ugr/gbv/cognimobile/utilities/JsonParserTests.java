package ugr.gbv.cognimobile.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.aware.Aware;
import com.aware.Aware_Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ugr.gbv.cognimobile.database.Provider;
import ugr.gbv.cognimobile.fragments.DrawTask;
import ugr.gbv.cognimobile.fragments.ImageTask;
import ugr.gbv.cognimobile.fragments.Task;
import ugr.gbv.cognimobile.fragments.TextTask;
import ugr.gbv.cognimobile.interfaces.LoadContent;

public class JsonParserTests {
    private static volatile JsonParserTests instantiated;

    private JsonParserTests(){
        if (instantiated != null){
            throw new RuntimeException("Use .getInstance() to instantiate JsonParserTests");
        }
    }

    public static JsonParserTests getInstance() {
        if (instantiated == null) {
            synchronized (JsonParserTests.class) {
                if (instantiated == null){
                    instantiated = new JsonParserTests();
                }
            }
        }

        return instantiated;
    }

    public ContentValues[] parse(String testsJson, Context context) throws JSONException {

        ArrayList<JSONObject> retrievedTests = new ArrayList<>();

        JSONObject reader = new JSONObject(testsJson);


        String[] projection = new String[]{Provider.Cognimobile_Data.NAME};
        String whereClause = Provider.Cognimobile_Data.NAME + " = ?";



        String[] whereArgs = new String[]{
                reader.getString(Provider.Cognimobile_Data.NAME),
        };
        Cursor cursor = context.getContentResolver().query(Provider.CONTENT_URI_TESTS,projection,whereClause,whereArgs,null);
        if(cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return null;
        }
        else{
            retrievedTests.add(reader);

            ContentValues[] contentValues = new ContentValues[retrievedTests.size()];
            for(int i = 0; i < contentValues.length; ++i){
                ContentValues articleValue = new ContentValues();
                articleValue.put(Provider.Cognimobile_Data.NAME, retrievedTests.get(i).getString(Provider.Cognimobile_Data.NAME));
                articleValue.put(Provider.Cognimobile_Data.DATA, retrievedTests.get(i).toString());
                articleValue.put(Provider.Cognimobile_Data.DEVICE_ID, Aware.getSetting(context, Aware_Preferences.DEVICE_ID));
                contentValues[i] = articleValue;
            }
            return contentValues;

        }


    }


    public ArrayList<Task> parseTestToTasks(@NonNull String testsJson, LoadContent callBack) throws JSONException {
        ArrayList<Task> tasks = new ArrayList<>();
        JSONObject reader = new JSONObject(testsJson);
        int number = 0;
        int entriesNoTasks = 3;
        String task ="task_";
        Bundle bundle;
        String language = reader.getString("language");
        boolean displayHelp = reader.getBoolean("display_help");

        for (; number < reader.length() - entriesNoTasks; ++number) {
            JSONObject object = reader.getJSONObject(task+number);
            int taskType = object.getInt("taskType");
            bundle = new Bundle();
            bundle.putBoolean("display_help", displayHelp);
            switch (taskType) {
                case Task.GRAPH:
                case Task.CUBE:
                    tasks.add(new DrawTask(taskType,callBack,bundle));
                    break;
                case Task.WATCH:

                    bundle.putString("hour", object.getString("hour"));
                    tasks.add(new DrawTask(taskType,callBack,bundle));
                    break;
                case Task.IMAGE:

                    bundle.putString("language", language);
                    bundle.putStringArray("images", extractJSONArrayAsStringArray(object.getJSONArray("images")));
                    bundle.putStringArray("answer", extractJSONArrayAsStringArray(object.getJSONArray("answer")));
                    tasks.add(new ImageTask(callBack,bundle));
                    break;
                case Task.MEMORY:

                    bundle.putString("language", language);
                    bundle.putStringArray("words", extractJSONArrayAsStringArray(object.getJSONArray("words")));
                    int repetitions = object.getInt("times");
                    bundle.putInt("times", repetitions);
                    tasks.add(new TextTask(taskType, callBack, bundle));
                    break;
                case Task.RECALL:

                    bundle.putString("language", language);
                    bundle.putStringArray("words", extractJSONArrayAsStringArray(object.getJSONArray("words")));
                    tasks.add(new TextTask(taskType, callBack, bundle));
                    break;
                case Task.ABSTRACTION:

                    bundle.putString("language", language);
                    bundle.putStringArray("words", extractJSONArrayAsStringArray(object.getJSONArray("words")));
                    bundle.putStringArray("answer", extractJSONArrayAsStringArray(object.getJSONArray("answer")));
                    tasks.add(new TextTask(taskType,callBack,bundle));
                    break;
                case Task.ATTENTION_NUMBERS:

                    bundle.putString("language", language);
                    bundle.putStringArray("numbers_forward", extractJSONArrayAsStringArray(object.getJSONArray("numbers_forward")));
                    bundle.putStringArray("numbers_backward", extractJSONArrayAsStringArray(object.getJSONArray("numbers_backward")));
                    tasks.add(new TextTask(taskType,callBack,bundle));
                    break;
                case Task.ATTENTION_LETTERS:

                    bundle.putString("language", language);
                    bundle.putStringArray("letters", extractJSONArrayAsStringArray(object.getJSONArray("letters")));
                    bundle.putString("target_letter",(object.getString("target_letter")));
                    tasks.add(new TextTask(taskType,callBack,bundle));
                    break;
                case Task.ATTENTION_SUBTRACTION:

                    bundle.putString("language", language);
                    bundle.putInt("minuend", (object.getInt("minuend")));
                    bundle.putInt("subtracting", (object.getInt("subtracting")));
                    bundle.putInt("times", object.getInt("times"));
                    tasks.add(new TextTask(taskType,callBack,bundle));
                    break;
                case Task.LANGUAGE:

                    bundle.putString("language", language);
                    bundle.putStringArray("phrases", extractJSONArrayAsStringArray(object.getJSONArray("phrases")));
                    tasks.add(new TextTask(taskType,callBack,bundle));
                    break;
                case Task.FLUENCY:

                    bundle.putString("language", language);
                    bundle.putString("target_letter",(object.getString("target_letter")));
                    bundle.putInt("number_words", object.getInt("number_words"));
                    tasks.add(new TextTask(taskType,callBack,bundle));
                    break;
                case Task.ORIENTATION:

                    bundle.putString("language", language);
                    bundle.putStringArray("questions", extractJSONArrayAsStringArray(object.getJSONArray("questions")));
                    tasks.add(new TextTask(taskType,callBack,bundle));
                    break;
            }

        }

        return tasks;
    }

    private String[] extractJSONArrayAsStringArray(JSONArray jsonArray) throws JSONException {
        int totalSize = jsonArray.length();
        String[] array = new String[totalSize];
        for (int i = 0; i < totalSize; ++i) {
            array[i] = jsonArray.getString(i);
        }
        return array;
    }

}
