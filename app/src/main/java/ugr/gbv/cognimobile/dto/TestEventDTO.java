package ugr.gbv.cognimobile.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TestEventDTO {
    private String testName;
    private String studyName;
    private String userName;
    private List<ResultEvent> events;
    private String language;

    private String createdAt;

    public TestEventDTO() {
        events = new ArrayList<>();
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestEventDTO that = (TestEventDTO) o;
        return  Objects.equals(studyName, that.studyName) &&
                Objects.equals(userName, that.userName) &&
                Objects.equals(events, that.events) &&
                Objects.equals(language, that.language);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studyName, userName, events, language);
    }
}
