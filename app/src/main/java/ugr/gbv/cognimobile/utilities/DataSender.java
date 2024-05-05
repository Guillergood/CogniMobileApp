package ugr.gbv.cognimobile.utilities;

import android.content.Context;

import android.text.TextUtils;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ugr.gbv.cognimobile.callbacks.*;
import ugr.gbv.cognimobile.database.CognimobilePreferences;
import ugr.gbv.cognimobile.dto.Study;
import ugr.gbv.cognimobile.dto.StudyEnrollRequest;
import ugr.gbv.cognimobile.dto.TestDTO;
import ugr.gbv.cognimobile.mocks.MockedHttpStack;
import ugr.gbv.cognimobile.payload.response.JwtResponse;

/**
 * Class to send the data to the server
 */
public class DataSender implements Serializable {


    private static volatile DataSender instantiated;

    /**
     * Private constructor "singleton" pattern
     */
    private DataSender() {

        if (instantiated != null) {
            throw new RuntimeException("Use .instantiate() to instantiate TestDataSender");
        }
    }

    /**
     * Get instance of the singleton instance
     *
     * @return the unique instance of the class
     */
    public static DataSender getInstance() {
        if (instantiated == null) {
            synchronized (DataSender.class) {
                if (instantiated == null) instantiated = new DataSender();
            }
        }

        return instantiated;
    }

    /**
     * Post a command in the server, using a RESTful API
     *
     * @param data    the data to post
     * @param context parent activity context
     */
    public void postToServer(@NonNull Object data, Context context, String subPath, CredentialsCallback credentialCallback) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                processUrl(CognimobilePreferences.getServerUrl(context) + subPath),
                response -> {
                    Toast.makeText(context,"Operation done successfully",Toast.LENGTH_LONG).show();
                },
                error -> {
                    //displaying the error in toast if occur
                    if(!TextUtils.isEmpty(CognimobilePreferences.getLogin(context))) {
                        if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                            refreshAccessToken(context);
                        } else {
                            ErrorHandler.displayError("Error sending the data. Do you want to try to send it again?",
                                    data,
                                    subPath);
                        }
                    }
                    else{
                        if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                            ErrorHandler.displayError("Invalid credentials or inactive account.");
                            credentialCallback.doLogout();
                        }
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
                    return objectMapper.writeValueAsBytes(data);
                } catch (JsonProcessingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of the answers or events.");
                    return null;
                }

            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = getAuthHeaders(context);
                if (headers != null)
                    return headers;
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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        requestQueue.add(stringRequest);
    }

    private String processUrl(final String url) {
        return url.replaceAll("//", "/");
    }

    public void enrollInStudy(@NonNull StudyEnrollRequest data, Context context) {
        StringRequest stringRequest = new StringRequest(Request.Method.PATCH,
                processUrl(CognimobilePreferences.getServerUrl(context) + "/study/enroll"),
                response -> {
                    Toast.makeText(context,"Operation done successfully",Toast.LENGTH_LONG).show();
                },
                error -> {
                    //displaying the error in toast if occur
                    if(!TextUtils.isEmpty(CognimobilePreferences.getLogin(context))) {
                        if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                            refreshAccessToken(context);
                        } else {
                            ErrorHandler.displayError("Error sending the data.");
                        }
                    }
                    else{
                        if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                            ErrorHandler.displayError("Invalid credentials or inactive account.");
                        }
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
                    return objectMapper.writeValueAsBytes(data);
                } catch (JsonProcessingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of the answers or events.");
                    return null;
                }

            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = getAuthHeaders(context);
                if (headers != null)
                    return headers;
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
        requestQueue.add(stringRequest);
    }

    @Nullable
    private Map<String, String> getAuthHeaders(final Context context) {
        if(!TextUtils.isEmpty(CognimobilePreferences.getLogin(context))) {
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
        }
        return null;
    }

    public void getAllStudies(Context context, StudyCallback callback, CredentialsCallback credentialCallback) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                processUrl(CognimobilePreferences.getServerUrl(context) + "/study/all"),
                response -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        callback.getStudies(objectMapper.readValue(response.trim(), new TypeReference<List<Study>>() {}));
                    } catch (JsonProcessingException e) {
                        ErrorHandler.displayError("Error trying to get the studies data.");
                    }
                },
                error -> {
                    //displaying the error in toast if occur
                    if(!TextUtils.isEmpty(CognimobilePreferences.getLogin(context))) {
                        if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                            refreshAccessToken(context);
                        } else {
                            ErrorHandler.displayError("Error getting the studies.");
                        }
                    }
                    else{
                        if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                            ErrorHandler.displayError("Invalid credentials or inactive account.");
                            credentialCallback.doLogout();
                        }
                    }
                }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = getAuthHeaders(context);
                if (headers != null)
                    return headers;
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
        requestQueue.add(stringRequest);
    }

    public void getTestToBeDone(Context context, String testName, TestCallback callback, CredentialsCallback credentialCallback) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                processUrl(CognimobilePreferences.getServerUrl(context) + "/test/getTest/" + testName),
                response -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        callback.getTest(objectMapper.readValue(response.trim(), TestDTO.class));
                    } catch (JsonProcessingException e) {
                        ErrorHandler.displayError("Error trying to get the studies data.");
                    }
                },
                error -> {
                    //displaying the error in toast if occur
                    if(!TextUtils.isEmpty(CognimobilePreferences.getLogin(context))) {
                        if (error.networkResponse.statusCode == 401) {
                            refreshAccessToken(context);
                        } else {
                            ErrorHandler.displayError("Error getting the data.");
                        }
                    }
                    else{
                        if (error.networkResponse.statusCode == 401) {
                            ErrorHandler.displayError("Invalid credentials or inactive account.");
                            credentialCallback.doLogout();
                        }
                    }
                }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = getAuthHeaders(context);
                if (headers != null)
                    return headers;
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
        requestQueue.add(stringRequest);
    }


    void refreshAccessToken(Context context) {
        if(CognimobilePreferences.getLogin(context).isEmpty()){
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JwtResponse jwt;
            jwt = objectMapper.readValue(CognimobilePreferences.getLogin(context), JwtResponse.class);
            jsonObject.put("refreshToken",jwt.getRefreshToken());
        } catch (JsonProcessingException | JSONException e) {
            ErrorHandler.displayError("Some error happened when trying to login, please try again.");
        }

        JsonObjectRequest refreshTokenRequest = new JsonObjectRequest(Request.Method.POST,
                CognimobilePreferences.getServerUrl(context), jsonObject, response -> {
            try {
                String accessToken = response.getString("access_token");
                CustomObjectMapper objectMapper = new CustomObjectMapper();
                JwtResponse jwt = objectMapper.readValue(CognimobilePreferences.getLogin(context), JwtResponse.class);
                jwt.setToken(accessToken);
                CognimobilePreferences.setLogin(context,objectMapper.writeValueAsString(jwt));
            } catch (JSONException | JsonProcessingException e) {
                // this will never happen but if so, show error to user.
                ErrorHandler.displayError("Error refreshing the authentication.");
            }
        }, error -> {
            CognimobilePreferences.setLogin(context, "");
        });
        //creating a request queue
        RequestQueue requestQueue;
        if(CognimobilePreferences.isMockedHttp()) {
            requestQueue = Volley.newRequestQueue(context, new MockedHttpStack());
        }
        else {
            requestQueue = Volley.newRequestQueue(context);
        }

        //adding the string request to request queue
        requestQueue.add(refreshTokenRequest);
    }

}
