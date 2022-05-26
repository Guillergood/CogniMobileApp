package ugr.gbv.cognimobile.dto;

import java.util.List;

public class TestEventDTO {
    private String testName;
    private String studyName;
    private String userName;
    private List<ResultEvent> events;
    private String language;

    public TestEventDTO() {
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getStudyName() {
        return studyName;
    }

    public void setStudyName(String studyName) {
        this.studyName = studyName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<ResultEvent> getEvents() {
        return events;
    }

    public void setEvents(List<ResultEvent> events) {
        this.events = events;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }
}
