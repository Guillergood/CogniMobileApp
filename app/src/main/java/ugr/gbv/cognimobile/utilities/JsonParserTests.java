package ugr.gbv.cognimobile.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ugr.gbv.cognimobile.database.Provider;
import ugr.gbv.cognimobile.dto.TestDTO;
import ugr.gbv.cognimobile.fragments.DrawTask;
import ugr.gbv.cognimobile.fragments.ImageTask;
import ugr.gbv.cognimobile.fragments.Task;
import ugr.gbv.cognimobile.fragments.TextTask;
import ugr.gbv.cognimobile.interfaces.LoadContent;

import static ugr.gbv.cognimobile.dto.TaskType.*;

/**
 * Class to manage the test json
 */
public class JsonParserTests {
    private static volatile JsonParserTests instantiated;

    /**
     * Private constructor
     */
    private JsonParserTests() {
        if (instantiated != null) {
            throw new RuntimeException("Use .getInstance() to instantiate JsonParserTests");
        }
    }

    /**
     * Getter to return the unique instance in the app.
     *
     * @return the unique instance in the app.
     */
    public static JsonParserTests getInstance() {
        if (instantiated == null) {
            synchronized (JsonParserTests.class) {
                if (instantiated == null) {
                    instantiated = new JsonParserTests();
                }
            }
        }

        return instantiated;
    }

    /**
     * Gets the values from the json to be added in the local database
     *
     * @param testsJson string containing the json
     * @param context   from the parent class
     * @return the values from the json to be added in the local database
     * @throws JSONException in case that something was not being handled.
     */
    public ContentValues[] parse(String testsJson, Context context) throws JSONException {

        ArrayList<JSONObject> retrievedTests = new ArrayList<>();

        JSONObject reader = new JSONObject(testsJson);


        String[] projection = new String[]{Provider.Cognimobile_Data.NAME};
        String whereClause = Provider.Cognimobile_Data.NAME + " = ?";


        String[] whereArgs = new String[]{
                reader.getString(Provider.Cognimobile_Data.NAME),
        };
        Cursor cursor = context.getContentResolver().query(Provider.CONTENT_URI_TESTS,projection,whereClause,whereArgs,null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return null;
        } else {
            retrievedTests.add(reader);

            ContentValues[] contentValues = new ContentValues[retrievedTests.size()];
            for (int i = 0; i < contentValues.length; ++i) {
                ContentValues articleValue = new ContentValues();
                articleValue.put(Provider.Cognimobile_Data.NAME, retrievedTests.get(i).getString(Provider.Cognimobile_Data.NAME));
                articleValue.put(Provider.Cognimobile_Data.DATA, retrievedTests.get(i).toString());
                contentValues[i] = articleValue;
            }
            return contentValues;

        }


    }


    /**
     * Gets the values from the json to be displayed.
     *
     * @param test test to be parsed
     * @param callBack  to interact with the parent activity
     * @return an arraylist of task to be completed by the user.
     * @throws JSONException in case that something was not being handled.
     */
    public ArrayList<Task> parseTestToTasks(@NonNull TestDTO test, LoadContent callBack) throws JSONException {
        ArrayList<Task> tasksFragments = new ArrayList<>();
        Bundle bundle;
        List<ugr.gbv.cognimobile.dto.Task> tasks = test.getTasks();

        for (ugr.gbv.cognimobile.dto.Task task:tasks) {
            bundle = new Bundle();
            bundle.putBoolean("display_help", test.isShouldDisplayHelp());
            switch (task.getTaskType()) {
                case GRAPH:
                case CUBE:
                    tasksFragments.add(new DrawTask(task.getTaskType().ordinal(),callBack,bundle));
                    break;
                case WATCH:
                    bundle.putString("hour", task.getHour());
                    tasksFragments.add(new DrawTask(task.getTaskType().ordinal(),callBack,bundle));
                    break;
                case IMAGE:
                    bundle.putString("language", test.getLanguage());
                    bundle.putStringArray("images", task.getImages().toArray(new String[0]));
                    bundle.putStringArray("answer", task.getAnswer().toArray(new String[0]));
                    tasksFragments.add(new ImageTask(callBack,bundle));
                    break;
                case MEMORY:
                    bundle.putString("language", test.getLanguage());
                    bundle.putStringArray("words", task.getWords().toArray(new String[0]));
                    bundle.putInt("times", Integer.parseInt(task.getTimes()));
                    tasksFragments.add(new TextTask(task.getTaskType().ordinal(), callBack, bundle));
                    break;
                case RECALL:
                    bundle.putString("language", test.getLanguage());
                    bundle.putStringArray("words", task.getWords().toArray(new String[0]));
                    tasksFragments.add(new TextTask(task.getTaskType().ordinal(), callBack, bundle));
                    break;
                case ABSTRACTION:
                    bundle.putString("language", test.getLanguage());
                    bundle.putStringArray("words", task.getWords().toArray(new String[0]));
                    bundle.putStringArray("answer", task.getAnswer().toArray(new String[0]));
                    tasksFragments.add(new TextTask(task.getTaskType().ordinal(),callBack,bundle));
                    break;
                case ATTENTION_NUMBERS:
                    bundle.putString("language", test.getLanguage());
                    bundle.putStringArray("numbers_forward", task.getNumbers_forward().stream().map(String::valueOf).toArray(String[]::new));
                    bundle.putStringArray("numbers_backward", task.getNumbers_backward().stream().map(String::valueOf).toArray(String[]::new));
                    tasksFragments.add(new TextTask(task.getTaskType().ordinal(),callBack,bundle));
                    break;
                case ATTENTION_LETTERS:
                    bundle.putString("language", test.getLanguage());
                    bundle.putStringArray("letters", task.getLetters().toArray(new String[0]));
                    bundle.putString("target_letter", task.getTarget_letter());
                    tasksFragments.add(new TextTask(task.getTaskType().ordinal(),callBack,bundle));
                    break;
                case ATTENTION_SUBTRACTION:
                    bundle.putString("language", test.getLanguage());
                    bundle.putInt("minuend", task.getMinuend());
                    bundle.putInt("subtracting", task.getSubtracting());
                    bundle.putInt("times", Integer.parseInt(task.getTimes()));
                    tasksFragments.add(new TextTask(task.getTaskType().ordinal(),callBack,bundle));
                    break;
                case LANGUAGE:
                    bundle.putString("language", test.getLanguage());
                    bundle.putStringArray("phrases", task.getPhrases().toArray(new String[0]));
                    tasksFragments.add(new TextTask(task.getTaskType().ordinal(),callBack,bundle));
                    break;
                case FLUENCY:
                    bundle.putString("language", test.getLanguage());
                    bundle.putString("target_letter", task.getTarget_letter());
                    bundle.putInt("number_words", task.getNumber_words());
                    tasksFragments.add(new TextTask(task.getTaskType().ordinal(),callBack,bundle));
                    break;
                case ORIENTATION:
                    bundle.putString("language", test.getLanguage());
                    bundle.putStringArray("questions", task.getQuestions().toArray(new String[0]));
                    tasksFragments.add(new TextTask(task.getTaskType().ordinal(), callBack, bundle));
                    break;
            }

        }

        return tasksFragments;
    }

    /**
     * Extracts the JSONArray to a String array.
     *
     * @param jsonArray the JSONArray
     * @return the JSONArray as a String array.
     * @throws JSONException in case that something was not being handled.
     */
    private String[] extractJSONArrayAsStringArray(JSONArray jsonArray) throws JSONException {
        int totalSize = jsonArray.length();
        String[] array = new String[totalSize];
        for (int i = 0; i < totalSize; ++i) {
            array[i] = jsonArray.getString(i);
        }
        return array;
    }

}
