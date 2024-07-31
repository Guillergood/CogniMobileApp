package ugr.gbv.cognimobile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;


public class QrDTO implements Serializable {
    private String url;
    @JsonProperty("study")
    private StudyDTO study;

    public QrDTO() {
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public void setStudy(final StudyDTO study) {
        this.study = study;
    }

    public String getUrl() {
        return url;
    }

    public StudyDTO getStudy() {
        return study;
    }
}
