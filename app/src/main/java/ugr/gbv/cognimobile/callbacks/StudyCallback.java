package ugr.gbv.cognimobile.callbacks;

import ugr.gbv.cognimobile.dto.Study;

import java.util.List;

public interface StudyCallback {
    void getStudies(List<Study> studies);
}
