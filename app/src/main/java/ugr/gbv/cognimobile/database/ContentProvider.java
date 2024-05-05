package ugr.gbv.cognimobile.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONArray;
import ugr.gbv.cognimobile.callbacks.LoginCallback;
import ugr.gbv.cognimobile.dto.StudyDTO;
import ugr.gbv.cognimobile.dto.TestDTO;
import ugr.gbv.cognimobile.mocks.MockedHttpStack;
import ugr.gbv.cognimobile.payload.request.LoginRequest;
import ugr.gbv.cognimobile.payload.response.JwtResponse;
import ugr.gbv.cognimobile.utilities.CustomObjectMapper;
import ugr.gbv.cognimobile.utilities.ErrorHandler;
import ugr.gbv.cognimobile.utilities.NotificationUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static ugr.gbv.cognimobile.database.Provider.CONTENT_URI_STUDIES;
import static ugr.gbv.cognimobile.database.Provider.CONTENT_URI_TESTS;

public class ContentProvider implements Serializable {

    private static volatile ContentProvider instance;


    private ContentProvider(){

        if (instance != null){
            throw new RuntimeException("Use .getInstance() to invoke ContentProvider");
        }
    }

    public static ContentProvider getInstance() {
        if (instance == null) {
            synchronized (ContentProvider.class) {
                if (instance == null) instance = new ContentProvider();
            }
        }

        return instance;
    }


    public void doLogin(Context context, LoginRequest credentials, LoginCallback loginCallback){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                CognimobilePreferences.getServerUrl(context) + "/api/auth/signin",
                response -> {
                    try {
                         CognimobilePreferences.setLogin(context,response);
                         loginCallback.loginStored();
                    } catch (Exception e) {
                        ErrorHandler.displayError("Something happened when loading during the authentication");
                    }
                },
                error -> {
                    if(error instanceof AuthFailureError){
                        ErrorHandler.displayError("Invalid credentials or inactive account.");
                    }
                    else{
                        ErrorHandler.displayError("Some error happened when login, please try again later.");
                    }
                }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    CustomObjectMapper objectMapper = new CustomObjectMapper();
                    return credentials == null ? null : objectMapper.writeValueAsBytes(credentials);

                } catch (JsonProcessingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", credentials, "utf-8");
                    return null;
                }

            }
        };

        //creating a request queue
        RequestQueue requestQueue;
        if(CognimobilePreferences.isMockedHttp()) {
            requestQueue = Volley.newRequestQueue(context, new MockedHttpStack());
        }
        else {
            requestQueue = Volley.newRequestQueue(context);
        }

        //adding the string request to request queue
        requestQueue.add(stringRequest);

    }

    public void getTests(Context context) {
        StringRequest testRequest = new StringRequest(Request.Method.GET,
                CognimobilePreferences.getServerUrl(context) + "/study/tests",
                response -> {
                    try {
                        //getting the whole json object from the response
                        JSONArray obj = new JSONArray(response);
                        CustomObjectMapper mapper = new CustomObjectMapper();
                        ContentValues[] contentValues = new ContentValues[obj.length()];

                        String[] projection = new String[]{Provider.Cognimobile_Data._ID,
                                Provider.Cognimobile_Data.NAME,
                                Provider.Cognimobile_Data.REDO_TIMESTAMP};
                        Cursor cursor =
                                context.getContentResolver()
                                        .query(Provider.CONTENT_URI_TESTS,
                                                projection, null, null, Provider.Cognimobile_Data._ID);

                        for(int i = 0; i < obj.length(); ++i){
                            TestDTO test = mapper.readValue(obj.get(i).toString(),TestDTO.class);
                            ContentValues contentValue = new ContentValues();
                            contentValue.put(Provider.Cognimobile_Data.NAME, test.getName());
                            if (cursor != null) {
                                if(cursor.moveToFirst()) {
                                    do {
                                        String name = cursor.getString(cursor.getColumnIndexOrThrow(Provider.Cognimobile_Data.NAME));
                                        if (name.equals(test.getName())) {
                                            contentValue.put(Provider.Cognimobile_Data._ID,
                                                    cursor.getInt(cursor.getColumnIndexOrThrow(Provider.Cognimobile_Data._ID)));
                                            contentValue.put(Provider.Cognimobile_Data.REDO_TIMESTAMP,
                                                    cursor.getInt(cursor.getColumnIndexOrThrow(Provider.Cognimobile_Data.REDO_TIMESTAMP)));
                                        }
                                    } while (cursor.moveToNext());
                                }
                            }
                            else{
                                contentValue.put(Provider.Cognimobile_Data.REDO_TIMESTAMP, 0);
                            }
                            contentValue.put(Provider.Cognimobile_Data.DATA, obj.get(i).toString());
                            contentValue.put(Provider.Cognimobile_Data.DAYS_TO_DO, test.getPeriodicity());
                            contentValues[i] = contentValue;
                        }

                        ContentResolver contentResolver = context.getContentResolver();

                        boolean triggerNotification = true;
                        int numberOfTests = contentValues.length;
                        if (cursor != null) {
                            cursor.moveToFirst();
                            triggerNotification = cursor.getCount() < contentValues.length;
                            numberOfTests = contentValues.length - cursor.getCount();
                            cursor.close();
                        }

                        contentResolver.delete(
                                CONTENT_URI_TESTS,
                                null,
                                null
                        );

                        contentResolver.bulkInsert(
                                CONTENT_URI_TESTS,
                                contentValues
                        );

                        if(triggerNotification){
                            NotificationUtils.getInstance().notifyNewTestsAvailable(numberOfTests,context);
                        }

                    } catch (Exception e) {
                        ErrorHandler.displayError("Something happened when loading the tests into the database:");
                    }
                },
                error -> {
                    //displaying the error in toast if occur
                    ErrorHandler.displayError("Seems that the connection with the server is not possible.");
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                CustomObjectMapper objectMapper = new CustomObjectMapper();
                try {
                    JwtResponse jwt = objectMapper.readValue(CognimobilePreferences.getLogin(context), JwtResponse.class);
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", jwt.getType() + " " + jwt.getToken());
                    return headers;
                } catch (JsonProcessingException e) {
                    VolleyLog.wtf("Could not parse the credentials to be used in the getTests call");
                    ErrorHandler.displayError("Something happened when loading the tests into the database");
                }
                return super.getHeaders();
            }
        };


        //creating a request queue
        RequestQueue requestQueue;
        if(CognimobilePreferences.isMockedHttp()) {
            requestQueue = Volley.newRequestQueue(context, new MockedHttpStack());
        }
        else {
            requestQueue = Volley.newRequestQueue(context);
        }

        //adding the string request to request queue
        requestQueue.add(testRequest);
    }
    public void getStudies(Context context) {
        StringRequest studyRequest = new StringRequest(Request.Method.GET,
                CognimobilePreferences.getServerUrl(context) + "/study/currentUser",
                response -> {
                    try {
                        //getting the whole json object from the response
                        JSONArray obj = new JSONArray(response);
                        CustomObjectMapper mapper = new CustomObjectMapper();
                        ContentValues[] contentValues = new ContentValues[obj.length()];
                        for(int i = 0; i < obj.length(); ++i){
                            StudyDTO study = mapper.readValue(obj.get(i).toString(),StudyDTO.class);
                            ContentValues contentValue = new ContentValues();
                            contentValue.put(Provider.Cognimobile_Data.DATA, obj.get(i).toString());
                            contentValue.put(Provider.Cognimobile_Data.NAME, study.getName());
                            contentValues[i] = contentValue;
                        }

                        ContentResolver contentResolver = context.getContentResolver();

                        contentResolver.delete(
                                CONTENT_URI_STUDIES,
                                null,
                                null
                        );

                        contentResolver.bulkInsert(
                                CONTENT_URI_STUDIES,
                                contentValues
                        );

                    } catch (Exception e) {
                        ErrorHandler.displayError("Something happened when loading the studies into the database:");
                    }
                },
                error -> {
                    //displaying the error in toast if occur
                    ErrorHandler.displayError("Seems that the connection with the server is not possible.");
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                CustomObjectMapper objectMapper = new CustomObjectMapper();
                try {
                    JwtResponse jwt = objectMapper.readValue(CognimobilePreferences.getLogin(context), JwtResponse.class);
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", jwt.getType() + " " + jwt.getToken());
                    return headers;
                } catch (JsonProcessingException e) {
                    VolleyLog.wtf("Could not parse the credentials to be used in the getTests call");
                    ErrorHandler.displayError("Something happened when loading the studies into the database");
                }
                return super.getHeaders();
            }
        };

        RequestQueue requestQueue;
        if(CognimobilePreferences.isMockedHttp()) {
            requestQueue = Volley.newRequestQueue(context, new MockedHttpStack());
        }
        else {
            requestQueue = Volley.newRequestQueue(context);
        }

        //adding the string request to request queue
        requestQueue.add(studyRequest);
    }

}

