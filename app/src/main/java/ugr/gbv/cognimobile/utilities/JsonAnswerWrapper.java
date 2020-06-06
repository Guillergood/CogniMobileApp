package ugr.gbv.cognimobile.utilities;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonAnswerWrapper {
    private JSONObject jsonObject;
    private JSONObject subJsonObject;
    private int task;
    private final String TASKNAME = "task_";

    public JsonAnswerWrapper(String name, String locale) throws JSONException {
        jsonObject = new JSONObject();
        subJsonObject = new JSONObject();
        task = 0;
        jsonObject.put("name", name);
        jsonObject.put("language", locale);
    }

    public void addTotalScore(int score) throws JSONException {
        jsonObject.put("overall_score", score);
    }

    public void addArrayList(String key, ArrayList arrayList) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for(Object object:arrayList){
            jsonArray.put(object);
        }
        subJsonObject.put(key,jsonArray);
    }
    public void addField(String key, Object object) throws JSONException {
        subJsonObject.put(key,object);
    }

    public void addStringArray(String key, String[] array) throws JSONException {
        JSONArray jsonArray = new JSONArray(encodeUtf8ForUrl(array));
        subJsonObject.put(key, jsonArray);
    }

    public void addIntArray(String key, int[] array) throws JSONException {
        JSONArray jsonArray = new JSONArray(array);
        subJsonObject.put(key, jsonArray);
    }


    public void addFloatArray(String key, float[] array) throws JSONException {
        JSONArray jsonArray = new JSONArray(array);
        subJsonObject.put(key, jsonArray);
    }

    public void addTaskField() throws JSONException {
        String name = TASKNAME + task;
        jsonObject.put(name,subJsonObject);
        subJsonObject = new JSONObject();
        ++task;
    }

    public JSONArray getJSONArray() throws JSONException {
        JSONArray array = new JSONArray();
        array.put(jsonObject);
        return array;
    }


    public String[] encodeUtf8ForUrl(String[] array) {
        String[] value = array;
        for (int i = 0; i < array.length; ++i) {
            value[i] = Uri.encode(array[i]);
        }

        return value;
    }

    public String encodeUtf8ForUrl(String string) {
        return Uri.encode(string);
    }

    public String getFieldString(String key) throws JSONException {
        return subJsonObject.getString(key);
    }

    public Long getFieldLong(String key) throws JSONException {
        return subJsonObject.getLong(key);
    }

    public int getFieldInt(String key) throws JSONException {
        return subJsonObject.getInt(key);
    }

    public JSONArray getFieldArray(String key) throws JSONException {
        return subJsonObject.getJSONArray(key);
    }
}
