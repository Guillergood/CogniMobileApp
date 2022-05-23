package ugr.gbv.cognimobile.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.io.Serializable;


public class QrDTO implements Serializable {
    private final String url;
    private final String qr;

    @JsonCreator
    public QrDTO(String url, String qr) {
        this.url = url;
        this.qr = qr;
    }

    public String getUrl() {
        return url;
    }

    public String getQr() {
        return qr;
    }
}
