package ugr.gbv.cognimobile.mocks;

import com.android.volley.*;
import com.android.volley.toolbox.BaseHttpStack;
import com.android.volley.toolbox.HttpResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ugr.gbv.cognimobile.database.CognimobilePreferences;
import ugr.gbv.cognimobile.dto.Study;
import ugr.gbv.cognimobile.dto.TestDTO;
import ugr.gbv.cognimobile.payload.response.JwtResponse;

import java.io.IOException;
import java.util.*;


public class MockedHttpStack extends BaseHttpStack {

    @Override
    public HttpResponse executeRequest(Request<?> request, Map<String, String> map) throws IOException, AuthFailureError {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (request.getUrl().contains("/api/auth/signin")) {
                JwtResponse jwtResponse = new JwtResponse("mocked", "user1", Arrays.asList("ADMIN", "USER", "EXPERT"), "123");
                return new HttpResponse(200, new ArrayList<>(), objectMapper.writeValueAsBytes(jwtResponse));
            }
            if (request.getUrl().contains("/study/all")) {
                String study = "{\"id\":1,\"name\":\"Study\",\"description\":\"Cognimobile study\",\"tests\":[\"Test\"],\"collaborators\":[\"user1\"],\"subjects\":[\"user1\"]}";
                List<Study> studies = Collections.singletonList(objectMapper.readValue(study, Study.class));
                return new HttpResponse(200, new ArrayList<>(), objectMapper.writeValueAsBytes(studies));
            }
            if (request.getUrl().contains("/test/getTest/Test")) {
                return new HttpResponse(200, new ArrayList<>(), objectMapper.writeValueAsBytes(CognimobilePreferences.getMockedHttp()));
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
