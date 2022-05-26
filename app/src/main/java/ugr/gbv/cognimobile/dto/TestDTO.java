package ugr.gbv.cognimobile.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TestDTO {
    private final Long id;
    private final String name;
    private final List<Task> tasks;
    private final String language;
    @JsonProperty("display_help")
    private final boolean shouldDisplayHelp;

    @JsonCreator
    public TestDTO(Long id, String name, List<Task> tasks, String language, boolean shouldDisplayHelp) {
        this.id = id;
        this.name = name;
        this.tasks = tasks;
        this.language = language;
        this.shouldDisplayHelp = shouldDisplayHelp;
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

}
