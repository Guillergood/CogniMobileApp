package ugr.gbv.cognimobile.mocks;

import com.android.volley.*;
import com.android.volley.toolbox.BaseHttpStack;
import com.android.volley.toolbox.HttpResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ugr.gbv.cognimobile.database.CognimobilePreferences;
import ugr.gbv.cognimobile.dto.Study;
import ugr.gbv.cognimobile.dto.TestAnswerDTO;
import ugr.gbv.cognimobile.dto.TestEventDTO;
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
                Study study = new Study();
                study.setId(1L);
                study.setName("Study");
                study.setDescription("Cognimobile study");
                study.setTests(Collections.singletonList("Test"));
                study.setCollaborators(Collections.singletonList("user1"));
                study.setSubjects(Collections.singletonList("user1"));
                List<Study> studies = Collections.singletonList(study);
                return new HttpResponse(200, new ArrayList<>(), objectMapper.writeValueAsBytes(studies));
            }
            if (request.getUrl().contains("/test/getTest/Test")) {
                return new HttpResponse(200, new ArrayList<>(), objectMapper.writeValueAsBytes(CognimobilePreferences.getMockedHttp()));
            }
            if (request.getUrl().contains("/test/result/answer")) {
                CognimobilePreferences.setTestAnswerDTO(objectMapper.readValue(request.getBody(), TestAnswerDTO.class));
                return new HttpResponse(201, new ArrayList<>());
            }
            if (request.getUrl().contains("/test/result/event")) {
                CognimobilePreferences.setTestEventDTO(objectMapper.readValue(request.getBody(), TestEventDTO.class));
                return new HttpResponse(201, new ArrayList<>());
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
