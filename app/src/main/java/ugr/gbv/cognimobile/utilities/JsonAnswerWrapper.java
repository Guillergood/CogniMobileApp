package ugr.gbv.cognimobile.utilities;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class to manage the answers json
 */
public class JsonAnswerWrapper {
    private final JSONObject jsonObject;
    private JSONObject subJsonObject;
    private int task;

    /**
     * Constructor
     *
     * @param name   of the test
     * @param locale language of the test
     * @throws JSONException in case that something was not being handled.
     */
    public JsonAnswerWrapper(String name, String locale) throws JSONException {
        jsonObject = new JSONObject();
        subJsonObject = new JSONObject();
        task = 0;
        jsonObject.put("name", name);
        jsonObject.put("language", locale);
    }

    /**
     * Adds the overall score
     *
     * @param score to put inside of the json
     * @throws JSONException in case that something was not being handled.
     */
    public void addTotalScore(int score) throws JSONException {
        jsonObject.put("overall_score", score);
    }

    /**
     * Adds an arraylist to the json
     *
     * @param key       name to retrieve that arraylist after.
     * @param arrayList to be included in the json.
     * @throws JSONException in case that something was not being handled.
     */
    public void addArrayList(String key, ArrayList arrayList) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (Object object : arrayList) {
            jsonArray.put(object);
        }
        subJsonObject.put(key, jsonArray);
    }

    /**
     * Adds an element to the json
     *
     * @param key    name to retrieve that element after.
     * @param object to be included in the json.
     * @throws JSONException in case that something was not being handled.
     */
    public void addField(String key, Object object) throws JSONException {
        subJsonObject.put(key, object);
    }

    /**
     * Adds a string array to the json
     *
     * @param key   name to retrieve that arraylist after.
     * @param array to be included in the json.
     * @throws JSONException in case that something was not being handled.
     */
    public void addStringArray(String key, String[] array) throws JSONException {
        JSONArray jsonArray = new JSONArray(encodeUtf8ForUrl(array));
        subJsonObject.put(key, jsonArray);
    }

    /**
     * Adds an int array to the json
     *
     * @param key   name to retrieve that arraylist after.
     * @param array to be included in the json.
     * @throws JSONException in case that something was not being handled.
     */
    public void addIntArray(String key, int[] array) throws JSONException {
        JSONArray jsonArray = new JSONArray(array);
        subJsonObject.put(key, jsonArray);
    }


    /**
     * Adds a float array to the json
     *
     * @param key   name to retrieve that arraylist after.
     * @param array to be included in the json.
     * @throws JSONException in case that something was not being handled.
     */
    public void addFloatArray(String key, float[] array) throws JSONException {
        JSONArray jsonArray = new JSONArray(array);
        subJsonObject.put(key, jsonArray);
    }

    /**
     * Adds to the main json a task done, and increments the number task to add another
     *
     * @throws JSONException in case that something was not being handled.
     */
    public void addTaskField() throws JSONException {
        String TASKNAME = "task_";
        String name = TASKNAME + task;
        jsonObject.put(name, subJsonObject);
        subJsonObject = new JSONObject();
        ++task;
    }

    /**
     * Gets the main json as an JSONArray to be included in the server.
     *
     * @return json in JSONArray format.
     */
    public JSONArray getJSONArray() {
        JSONArray array = new JSONArray();
        array.put(jsonObject);
        return array;
    }


    /**
     * Encodes an string array to be in an url
     *
     * @param array string array to be encoded
     * @return the encoded array
     */
    public String[] encodeUtf8ForUrl(String[] array) {
        for (int i = 0; i < array.length; ++i) {
            array[i] = Uri.encode(array[i]);
        }

        return array;
    }

    /**
     * Gets a string from the json
     *
     * @param key name of the element to be retrieved.
     * @throws JSONException in case that something was not being handled.
     */
    public String getFieldString(String key) throws JSONException {
        return subJsonObject.getString(key);
    }

    /**
     * Gets a JSONArray from the json
     *
     * @param key name of the element to be retrieved.
     * @throws JSONException in case that something was not being handled.
     */
    public JSONArray getFieldArray(String key) throws JSONException {
        return subJsonObject.getJSONArray(key);
    }
}
