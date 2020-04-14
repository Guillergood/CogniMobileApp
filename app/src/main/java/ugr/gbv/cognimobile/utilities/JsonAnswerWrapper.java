package ugr.gbv.cognimobile.utilities;

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

    public void addArray(String key, String[] array) throws JSONException {
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
}
