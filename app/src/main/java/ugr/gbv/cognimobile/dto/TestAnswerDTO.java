package ugr.gbv.cognimobile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;


public class TestAnswerDTO {
    private String testName;
    private String studyName;
    private String userName;
    private List<ResultTask> tasks;
    @JsonProperty("overall_score")
    private String overallScore;
    private String language;

    private String createdAt;

    public TestAnswerDTO() {
        tasks = new ArrayList<>();
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

    public List<ResultTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<ResultTask> tasks) {
        this.tasks = tasks;
    }

    public String getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(String overallScore) {
        this.overallScore = overallScore;
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
}
