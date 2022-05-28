package ugr.gbv.cognimobile.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.io.Serializable;


public class QrDTO implements Serializable {
    private String url;
    private String qr;

    public QrDTO() {
    }

    public String getUrl() {
        return url;
    }

    public String getQr() {
        return qr;
    }
}
