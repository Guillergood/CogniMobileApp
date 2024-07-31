package ugr.gbv.cognimobile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TestDTO {
    private Long id;
    private String name;
    private List<Task> tasks;
    private String language;

    private int periodicity;
    @JsonProperty("display_help")
    private boolean shouldDisplayHelp;


    public TestDTO(Long id, String name, List<Task> tasks, String language, boolean shouldDisplayHelp) {
        this.id = id;
        this.name = name;
        this.tasks = tasks;
        this.language = language;
        this.shouldDisplayHelp = shouldDisplayHelp;
    }

    public TestDTO() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public String getLanguage() {
        return language;
    }

    public boolean isShouldDisplayHelp() {
        return shouldDisplayHelp;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setShouldDisplayHelp(boolean shouldDisplayHelp) {
        this.shouldDisplayHelp = shouldDisplayHelp;
    }

    public int getPeriodicity() {
        return periodicity;
    }

    public void setPeriodicity(final int periodicity) {
        this.periodicity = periodicity;
    }
}
