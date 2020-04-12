package ugr.gbv.cognimobile.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

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


    public ArrayList<Task> parseTestToTasks(String testsJson, LoadContent callBack) throws JSONException {
        ArrayList<Task> tasks = new ArrayList<>();
        JSONObject reader = new JSONObject(testsJson);
        int number = 0;
        String task ="task_";
        Bundle bundle = null;


        for(int i = 0; i < reader.length(); ++i){
            JSONObject object = reader.getJSONObject(task+number);
            int taskType = object.getInt("taskType");
            switch(object.getInt("taskType")){
                case Task.GRAPH:
                case Task.CUBE:
                    tasks.add(new DrawTask(taskType,callBack,bundle));
                    break;
                case Task.WATCH:
                    bundle = new Bundle();
                    bundle.putString("text",object.getString("text"));
                    tasks.add(new DrawTask(taskType,callBack,bundle));
                    break;
                case Task.IMAGE:
                    bundle = new Bundle();
                    Object[] arrayImages = extractJSONArray(object.getJSONArray("images"));
                    bundle.putStringArray("images",(String[])arrayImages);
                    Object[] arrayAnswerImages = extractJSONArray(object.getJSONArray("images"));
                    bundle.putStringArray("images",(String[])arrayAnswerImages);
                    tasks.add(new ImageTask(callBack,bundle));
                    break;
                case Task.MEMORY:
                case Task.RECALL:
                case Task.ABSTRACTION:
                    bundle = new Bundle();
                    Object[] arrayWords = extractJSONArray(object.getJSONArray("words"));
                    bundle.putStringArray("words",(String[])arrayWords);
                    tasks.add(new TextTask(taskType,callBack,bundle));
                    break;
                case Task.ATTENTION_NUMBERS:
                    bundle = new Bundle();
                    int[] numbersForward = extractJSONArrayAsIntArray(object.getJSONArray("numbers_forward"));
                    bundle.putIntArray("numbers_forward",numbersForward);
                    int[] numbersBackwards = extractJSONArrayAsIntArray(object.getJSONArray("numbers_backward"));
                    bundle.putIntArray("numbers_backward",numbersBackwards);
                    tasks.add(new TextTask(taskType,callBack,bundle));
                    break;
                case Task.ATTENTION_LETTERS:
                    bundle = new Bundle();
                    Object[] arrayLetters = extractJSONArray(object.getJSONArray("letters"));
                    bundle.putStringArray("letters",(String[])arrayLetters);
                    bundle.putString("target_letter",(object.getString("target_letter")));
                    tasks.add(new TextTask(taskType,callBack,bundle));
                    break;
                case Task.ATTENTION_SUBTRACTION:
                    bundle = new Bundle();
                    bundle.putString("minuendo",(object.getString("minuendo")));
                    bundle.putString("substracting",(object.getString("substracting")));
                    tasks.add(new TextTask(taskType,callBack,bundle));
                    break;
                case Task.LANGUAGE:
                    bundle = new Bundle();
                    Object[] phrases = extractJSONArray(object.getJSONArray("phrases"));
                    bundle.putStringArray("phrases",(String[])phrases);
                    tasks.add(new TextTask(taskType,callBack,bundle));
                    break;
                case Task.FLUENCY:
                    bundle = new Bundle();
                    bundle.putString("target_letter",(object.getString("target_letter")));
                    tasks.add(new TextTask(taskType,callBack,bundle));
                    break;
                case Task.ORIENTATION:
                    bundle = new Bundle();
                    Object[] questions = extractJSONArray(object.getJSONArray("questions"));
                    bundle.putStringArray("questions",(String[])questions);
                    tasks.add(new TextTask(taskType,callBack,bundle));
                    break;
            }
        }

        return tasks;
    }

    private Object[] extractJSONArray(JSONArray jsonArray) throws JSONException {
        int totalSize = jsonArray.length();
        Object[] array = new Object[totalSize];
        for(int i = 0; i < totalSize; ++i){
            array[i] = jsonArray.get(i);
        }
        return array;
    }
    private int[] extractJSONArrayAsIntArray(JSONArray jsonArray) throws JSONException {
        int totalSize = jsonArray.length();
        int[] array = new int[totalSize];
        for (int i = 0; i < totalSize; ++i) {
            array[i] = jsonArray.getInt(i);
        }
        return array;
    }

}
